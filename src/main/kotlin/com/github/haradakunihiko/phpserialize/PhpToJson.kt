package com.github.haradakunihiko.phpserialize

import com.github.haradakunihiko.phpserialize.exceptions.PhpSerializeException
import kotlinx.serialization.json.*
import kotlinx.serialization.encodeToString

/**
 * Library for converting PHP serialized strings to JSON
 */
object PhpToJson {

    /**
     * Convert PHP serialized string to JSON string
     */
    fun convert(phpSerialized: String): String {
        val jsonElement = convertToJsonElement(phpSerialized)
        return Json.encodeToString(jsonElement)
    }

    /**
     * Convert PHP serialized string to JsonElement
     */
    fun convertToJsonElement(phpSerialized: String): JsonElement {
        try {
            val parser = PhpUnserializeParser(phpSerialized)
            val result = parser.parseNext()
            return convertToJson(result)
        } catch (e: Exception) {
            throw PhpSerializeException("Failed to convert PHP serialized data to JSON: ${e.message}", e)
        }
    }

    /**
     * Convert parsed data to JsonElement
     */
    private fun convertToJson(value: Any?): JsonElement {
        return when (value) {
            null -> JsonNull
            is Boolean -> JsonPrimitive(value)
            is Long -> JsonPrimitive(value)
            is Int -> JsonPrimitive(value)
            is Double -> JsonPrimitive(value)
            is Float -> JsonPrimitive(value)
            is String -> JsonPrimitive(value)
            is List<*> -> {
                buildJsonArray {
                    value.forEach { item ->
                        add(convertToJson(item))
                    }
                }
            }
            is Map<*, *> -> {
                buildJsonObject {
                    value.forEach { (key, v) ->
                        put(key.toString(), convertToJson(v))
                    }
                }
            }
            else -> JsonPrimitive(value.toString())
        }
    }

    /**
     * PHP serialized string parser
     */
    private class PhpUnserializeParser(private val phpstr: String) {
        private var idx = 0
        private val refStack = mutableListOf<Any?>()
        private var ridx = 0

        fun parseNext(): Any? {
            val type = readType()
            return when (type) {
                'i' -> parseAsInt()
                'd' -> parseAsFloat()
                'b' -> parseAsBoolean()
                's' -> parseAsString()
                'a' -> parseAsArray()
                'O' -> parseAsObject()
                'C' -> parseAsCustom()
                'E' -> parseAsString() // enum as string
                'r' -> parseAsRefValue()
                'R' -> parseAsRef()
                'N' -> parseAsNull()
                else -> throw PhpSerializeException("Unknown type '$type' at position ${idx - 2}")
            }
        }

        private fun readType(): Char {
            val type = phpstr[idx]
            idx += 2
            return type
        }

        private fun readLength(): Int {
            val del = phpstr.indexOf(':', idx)
            val value = phpstr.substring(idx, del)
            idx = del + 2
            return value.toInt()
        }

        private fun readInt(): Int {
            val del = phpstr.indexOf(';', idx)
            val value = phpstr.substring(idx, del)
            idx = del + 1
            return value.toInt()
        }

        private fun readString(expect: Char = '"'): String {
            val len = readLength()
            var utfLen = 0
            var bytes = 0

            // UTF-8 byte length calculation
            while (bytes < len) {
                val ch = phpstr[idx + utfLen].code
                utfLen++
                when {
                    ch <= 0x007F -> bytes++
                    ch > 0x07FF -> bytes += 3
                    else -> bytes += 2
                }
            }

            // Handle non-compliant UTF-8 encoding
            if (idx + utfLen < phpstr.length && phpstr[idx + utfLen] != expect) {
                val adjustIdx = phpstr.indexOf(expect, idx + utfLen)
                if (adjustIdx != -1) {
                    utfLen = adjustIdx - idx
                }
            }

            val value = phpstr.substring(idx, idx + utfLen)
            idx += utfLen + 2
            return value
        }

        private fun readKey(): Any {
            val type = readType()
            return when (type) {
                'i' -> readInt()
                's' -> readString()
                else -> throw PhpSerializeException("Unknown key type '$type' at position ${idx - 2}")
            }
        }

        private fun parseAsInt(): Int {
            val value = readInt()
            refStack.add(value)
            ridx++
            return value
        }

        private fun parseAsFloat(): Double {
            val del = phpstr.indexOf(';', idx)
            val value = phpstr.substring(idx, del).toDouble()
            idx = del + 1
            refStack.add(value)
            ridx++
            return value
        }

        private fun parseAsBoolean(): Boolean {
            val del = phpstr.indexOf(';', idx)
            val value = phpstr.substring(idx, del) == "1"
            idx = del + 1
            refStack.add(value)
            ridx++
            return value
        }

        private fun parseAsString(): String {
            val value = readString()
            refStack.add(value)
            ridx++
            return value
        }

        private fun parseAsNull(): Any? {
            val value = null
            refStack.add(value)
            ridx++
            return value
        }

        private fun parseAsArray(): Any {
            val len = readLength()
            val resultArray = mutableListOf<Any?>()
            val resultHash = mutableMapOf<String, Any?>()
            var keep: Any = resultArray
            val lref = ridx++

            refStack.add(keep)

            try {
                for (i in 0 until len) {
                    val key = readKey()
                    val value = parseNext()

                    // Logic to determine whether it is an array or a hash
                    if (keep === resultArray && key.toString() == i.toString()) {
                        // Save as array
                        resultArray.add(value)
                    } else {
                        if (keep !== resultHash) {
                            // Convert to hash upon encountering the first non-sequential numeric key
                            for (j in resultArray.indices) {
                                resultHash[j.toString()] = resultArray[j]
                            }
                            keep = resultHash
                            refStack[lref] = keep
                        }
                        resultHash[key.toString()] = value
                    }
                }
            } catch (e: Exception) {
                throw PhpSerializeException("Error parsing array: ${e.message}", e)
            }

            idx++ // skip '}'
            return keep
        }

        private fun parseAsObject(): Map<String, Any?> {
            val obj = mutableMapOf<String, Any?>()
            val lref = ridx++
            val className = readString()
            
            refStack.add(obj)
            val len = readLength()

            try {
                for (i in 0 until len) {
                    val rawKey = readKey()
                    val value = parseNext()
                    val cleanKey = fixPropertyName(rawKey.toString(), className)
                    obj[cleanKey] = value
                }
            } catch (e: Exception) {
                throw PhpSerializeException("Error parsing object: ${e.message}", e)
            }

            idx++ // skip '}'
            return obj
        }

        private fun parseAsCustom(): Map<String, Any?> {
            val className = readString()
            val content = readString('}')
            idx-- // There is no char after the closing quote

            return mapOf(
                "__PHP_Incomplete_Class_Name" to className,
                "serialized" to content
            )
        }

        private fun parseAsRefValue(): Any? {
            val ref = readInt()
            // PHP ref counter is 1-based, stack is 0-based
            val value = refStack[ref - 1]
            refStack.add(value)
            ridx++
            return value
        }

        private fun parseAsRef(): Any? {
            val ref = readInt()
            // PHP ref counter is 1-based, stack is 0-based
            return refStack[ref - 1]
        }

        /**
         * PHP property name modification logic
         */
        private fun fixPropertyName(parsedName: String, baseClassName: String): String {
            if (parsedName.isNotEmpty() && parsedName[0] == '\u0000') {
                // "\0*\0property" or "\0class\0property"
                val pos = parsedName.indexOf('\u0000', 1)
                if (pos > 0) {
                    val className = parsedName.substring(1, pos)
                    val propName = parsedName.substring(pos + 1)

                    return when {
                        className == "*" -> propName // protected
                        className == baseClassName -> propName // own private
                        else -> "$className::$propName" // private of descendant
                    }
                } else {
                    throw PhpSerializeException(
                        "Expected two <NUL> characters in non-public property name '$parsedName' at position ${idx - parsedName.length - 2}"
                    )
                }
            } else {
                // public property
                return parsedName
            }
        }
    }
}
