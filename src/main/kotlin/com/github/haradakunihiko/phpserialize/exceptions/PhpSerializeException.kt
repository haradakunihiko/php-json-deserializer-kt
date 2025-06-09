package com.github.haradakunihiko.phpserialize.exceptions

/**
 * Exception thrown during PHP serialization processing
 */
class PhpSerializeException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
