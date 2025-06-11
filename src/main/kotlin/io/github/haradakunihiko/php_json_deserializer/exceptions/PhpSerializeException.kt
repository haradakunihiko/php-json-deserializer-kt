package io.github.haradakunihiko.php_json_deserializer.exceptions

/**
 * Exception thrown during PHP serialization processing
 */
class PhpSerializeException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
