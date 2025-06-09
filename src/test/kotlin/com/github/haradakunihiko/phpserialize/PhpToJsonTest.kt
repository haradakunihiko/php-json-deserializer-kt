package com.github.haradakunihiko.phpserialize

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.serialization.json.*

/**
 * Tests for PHP JSON Deserializer
 */
class PhpToJsonTest : StringSpec() {

    init {


        // Execute comprehensive test cases as individual tests
        val comprehensiveTestCases = mapOf(

            "Basic type conversion - null value" to mapOf(
                "phpData" to "N;",
                "expectedJson" to "null"
            ),
            "Basic type conversion - boolean true" to mapOf(
                "phpData" to "b:1;",
                "expectedJson" to "true"
            ),
            "Basic type conversion - boolean false" to mapOf(
                "phpData" to "b:0;",
                "expectedJson" to "false"
            ),
            "Basic type conversion - integer" to mapOf(
                "phpData" to "i:123;",
                "expectedJson" to "123"
            ),
            "Basic type conversion - double/float" to mapOf(
                "phpData" to "d:1.5;",
                "expectedJson" to "1.5"
            ),
            "Basic type conversion - string" to mapOf(
                "phpData" to "s:5:\"hello\";",
                "expectedJson" to "\"hello\""
            ),

            "Array conversion - sequential key array (3 elements)" to mapOf(
                "phpData" to "a:3:{i:0;i:1;i:1;i:2;i:2;i:3;}",
                "expectedJson" to "[1,2,3]"
            ),
            "Array conversion - sequential key array (2 elements)" to mapOf(
                "phpData" to "a:2:{i:0;i:1;i:1;i:2;}",
                "expectedJson" to "[1,2]"
            ),

            "Associative array/hash conversion - string key" to mapOf(
                "phpData" to "a:2:{s:4:\"name\";s:4:\"John\";s:3:\"age\";i:30;}",
                "expectedJson" to "{\"name\":\"John\",\"age\":30}"
            ),
            "Associative array/hash conversion - mixed key" to mapOf(
                "phpData" to "a:3:{i:0;s:5:\"first\";s:4:\"name\";s:4:\"John\";i:2;s:5:\"third\";}",
                "expectedJson" to "{\"0\":\"first\",\"name\":\"John\",\"2\":\"third\"}"
            ),

            "Object conversion - basic object" to mapOf(
                "phpData" to "O:4:\"Test\":1:{s:4:\"name\";s:4:\"John\";}",
                "expectedJson" to "{\"name\":\"John\"}"
            ),

            "Custom serialization class conversion - C type class" to mapOf(
                "phpData" to "C:4:\"Test\":13:{\"custom data\"}",
                "expectedJson" to "{\"__PHP_Incomplete_Class_Name\":\"Test\",\"serialized\":\"\\\"custom data\\\"\"}"
            ),


            "complex nested object with all property types" to mapOf(
                "phpData" to loadSampleFile("sample1/serialized.txt"),
                "expectedJson" to loadSampleFile("sample1/deserialized.txt")
            ),
        )

        // Execute each comprehensive test case as an individual test
        comprehensiveTestCases.forEach { (testName, config) ->
            testName {
                val phpData = config["phpData"] as String
                val expectedJson = config["expectedJson"] as String

                val actualJson = PhpToJson.convert(phpData)

                println("PHP: $phpData")
                println("JSON: $actualJson")

                // Use common comparison processing
                assertJsonEquals(expectedJson, actualJson, testName)

                println("✅ $testName test completed")
            }
        }
    }
}

/**
 * Load sample file
 */
private fun loadSampleFile(relativePath: String): String {
    val resourcePath = "sample/$relativePath"
    val inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream(resourcePath)
        ?: throw IllegalArgumentException("Sample file not found in resources: $resourcePath")

    return inputStream.bufferedReader().use { reader -> reader.readText().trim() }
}

/**
 * Common function to assert JSON equivalence
 */
private fun assertJsonEquals(expectedJson: String, actualJson: String, testName: String = "JSON") {
    // Confirm that it can be parsed correctly as JSON
    val actualJsonElement = Json.parseToJsonElement(actualJson)
    val expectedJsonElement = Json.parseToJsonElement(expectedJson)

    // Compare with parsed JSON objects (ignoring format differences)
    if (expectedJsonElement == actualJsonElement) {
        println("✅ $testName Expected JSON is a complete match!")
    } else {
        println("⚠️ $testName There are differences between the expected JSON and the actual JSON")
        println()
        println("=== Expected JSON ===")
        println(expectedJson)
        println()
        println("=== Actual JSON ===")
        println(actualJson)
        println()

        // Compare parsed JSON objects for more detailed comparison
        println("=== Start detailed comparison ===")
        try {
            compareJsonElements(expectedJsonElement, actualJsonElement, "root")
            println("=== Detailed comparison completed ===")
        } catch (e: Exception) {
            println("❌ Error during detailed comparison: ${e.message}")
            e.printStackTrace()
        }

        // Fail the test
        throw AssertionError("$testName: The expected JSON and the actual JSON do not match. Check the comparison results above for details.")
    }
}

/**
 * Compare JSONElement in detail and output differences
 */
private fun compareJsonElements(expected: JsonElement, actual: JsonElement, path: String) {
    println("🔍 Comparing: $path")
    when {
        expected is JsonObject && actual is JsonObject -> {
            // Object comparison
            val expectedKeys = expected.keys
            val actualKeys = actual.keys

            // Check if the expected key actually exists
            expectedKeys.forEach { key ->
                if (!actualKeys.contains(key)) {
                    println("❌ Missing key: $path.$key")
                } else {
                    compareJsonElements(expected[key]!!, actual[key]!!, "$path.$key")
                }
            }

            // Check for keys that actually exist but are not expected
            actualKeys.forEach { key ->
                if (!expectedKeys.contains(key)) {
                    println("➕ Added key: $path.$key = ${actual[key]}")
                }
            }
        }

        expected is JsonArray && actual is JsonArray -> {
            // Array comparison
            if (expected.size != actual.size) {
                println("❌ Array size difference: $path - Expected=${expected.size}, Actual=${actual.size}")
            }

            val minSize = minOf(expected.size, actual.size)
            for (i in 0 until minSize) {
                compareJsonElements(expected[i], actual[i], "$path[$i]")
            }
        }

        expected is JsonPrimitive && actual is JsonPrimitive -> {
            // Primitive value comparison
            if (expected.content != actual.content) {
                println("❌ Value difference: $path - Expected='${expected.content}', Actual='${actual.content}'")
            }
        }

        expected is JsonNull && actual is JsonNull -> {
            // Both null - OK
        }

        else -> {
            // Different type
            println("❌ Type difference: $path - Expected=${expected::class.simpleName}, Actual=${actual::class.simpleName}")
        }
    }
}
