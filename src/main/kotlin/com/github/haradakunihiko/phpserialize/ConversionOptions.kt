package com.github.haradakunihiko.phpserialize

/**
 * Configuration options for PHP to JSON conversion
 */
data class ConversionOptions(
    /**
     * Whether to include class name information in the JSON output
     * When true, objects will include a special key with the class name
     */
    val includeClassName: Boolean = true,
    
    /**
     * The key name to use for storing class name information
     * Only used when includeClassName is true
     */
    val classNameKey: String = "__classname"
)

/**
 * Builder class for DSL-style configuration
 */
class ConversionOptionsBuilder {
    /**
     * Whether to include class name information in the JSON output
     */
    var includeClassName: Boolean = true
    
    /**
     * The key name to use for storing class name information
     */
    var classNameKey: String = "__classname"
    
    /**
     * Build the ConversionOptions instance
     */
    fun build(): ConversionOptions {
        return ConversionOptions(
            includeClassName = includeClassName,
            classNameKey = classNameKey
        )
    }
}

/**
 * DSL function for creating ConversionOptions
 * 
 * Usage:
 * ```
 * val options = ConversionOptions {
 *     includeClassName = true
 *     classNameKey = "@type"
 * }
 * ```
 */
fun ConversionOptions(block: ConversionOptionsBuilder.() -> Unit): ConversionOptions {
    val builder = ConversionOptionsBuilder()
    builder.block()
    return builder.build()
}
