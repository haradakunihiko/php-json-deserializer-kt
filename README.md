# PHP JSON Deserializer for Kotlin/Java

[![CI](https://github.com/haradakunihiko/php-json-deserializer-kt/workflows/CI/badge.svg)](https://github.com/haradakunihiko/php-json-deserializer-kt/actions)
[![codecov](https://codecov.io/gh/haradakunihiko/php-json-deserializer-kt/branch/main/graph/badge.svg)](https://codecov.io/gh/haradakunihiko/php-json-deserializer-kt)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.haradakunihiko/php-json-deserializer-kt.svg)](https://search.maven.org/artifact/io.github.haradakunihiko/php-json-deserializer-kt)
[![GitHub release](https://img.shields.io/github/release/haradakunihiko/php-json-deserializer-kt.svg)](https://github.com/haradakunihiko/php-json-deserializer-kt/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

> A Kotlin port of [bd808/php-unserialize-js](https://github.com/bd808/php-unserialize-js)

A Kotlin/Java library for deserializing PHP serialized strings to JSON.

## 🚀 Features

- **Convert PHP serialized strings to JSON**
- **Complete PHP type support**: Private/protected properties, references, custom classes
- **High-precision array detection**: Accurately distinguish between numeric index arrays and objects
- **Configurable class name inclusion**: Choose whether to include PHP class names in JSON output
- **Pretty print support**: Format JSON output with indentation

## 📦 Installation

### Gradle (Kotlin DSL)
```kotlin
dependencies {
    implementation("io.github.haradakunihiko:php-json-deserializer-kt:1.0.0")
}
```

### Maven
```xml
<dependency>
    <groupId>io.github.haradakunihiko</groupId>
    <artifactId>php-json-deserializer-kt</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 🔧 Usage

### Basic Usage

```kotlin
import com.github.haradakunihiko.phpserialize.PhpToJson

// Simple conversion
val json = PhpToJson.convert("i:123;")
println(json) // "123"

// Pretty print
val prettyJson = PhpToJson.convert("O:4:\"Test\":1:{s:4:\"name\";s:4:\"John\";}", prettyPrint = true)
println(prettyJson)
// {
//     "__classname": "Test",
//     "name": "John"
// }
```

### Advanced Configuration

The library supports advanced configuration through `ConversionOptions` :

```kotlin
import com.github.haradakunihiko.phpserialize.PhpToJson
import com.github.haradakunihiko.phpserialize.ConversionOptions

// Disable class names
val noClassJson = PhpToJson(ConversionOptions {
    includeClassName = false
}).convert("O:4:\"Test\":1:{s:4:\"name\";s:4:\"John\";}")
println(noClassJson) // {"name":"John"}

```

### Configuration Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `includeClassName` | `Boolean` | `true` | Include PHP class names in JSON output |
| `classNameKey` | `String` | `"__classname"` | Key name for storing class information |

**Examples:**

```kotlin
// Default behavior (includes class names)
PhpToJson.convert("O:4:\"Test\":1:{s:4:\"name\";s:4:\"John\";}")
// → {"__classname":"Test","name":"John"}

// Disable class names
val noClassConverter = PhpToJson(ConversionOptions {
    includeClassName = false
})
noClassConverter.convert("O:4:\"Test\":1:{s:4:\"name\";s:4:\"John\";}")
// → {"name":"John"}

// Custom class name key
val customConverter = PhpToJson(ConversionOptions {
    includeClassName = true
    classNameKey = "@type"
})
customConverter.convert("O:4:\"Test\":1:{s:4:\"name\";s:4:\"John\";}")
// → {"@type":"Test","name":"John"}
```

### Supported PHP Data Types

```kotlin
// Basic types
PhpToJson.convert("N;")                              // "null"
PhpToJson.convert("b:1;")                           // "true"
PhpToJson.convert("b:0;")                           // "false"
PhpToJson.convert("i:123;")                         // "123"
PhpToJson.convert("d:1.5;")                         // "1.5"
PhpToJson.convert("s:5:\"hello\";")                 // "\"hello\""

// Arrays (numeric index)
PhpToJson.convert("a:3:{i:0;i:1;i:1;i:2;i:2;i:3;}") // "[1,2,3]"

// Associative arrays
PhpToJson.convert("a:2:{s:4:\"name\";s:4:\"John\";s:3:\"age\";i:30;}") // "{\"name\":\"John\",\"age\":30}"

// Objects
PhpToJson.convert("O:8:\"stdClass\":1:{s:4:\"name\";s:4:\"test\";}") // "{\"name\":\"test\"}"

// Custom serializable classes
PhpToJson.convert("C:4:\"Test\":13:{\"custom data\"}") // "{\"__PHP_Incomplete_Class_Name\":\"Test\",\"serialized\":\"\\\"custom data\\\"\"}"

// References
PhpToJson.convert("a:2:{i:0;s:4:\"test\";i:1;r:2;}") // "[\"test\",\"test\"]"
```

### Complex Object Example

```kotlin
// PHP object with private/protected properties
val phpObject = """
    O:4:"User":3:{
        s:4:"name";s:4:"John";
        s:7:"\0*\0email";s:13:"john@test.com";
        s:9:"\0User\0age";i:30;
    }
""".trimIndent()

val json = PhpToJson.convert(phpObject)
println(json) // {"name":"John","email":"john@test.com","age":30}
```

## 📊 Supported Data Types

| PHP Type | Notation | JSON Output | Description |
|----------|----------|-------------|-------------|
| null | `N;` | `null` | Null value |
| boolean | `b:0;` / `b:1;` | `false` / `true` | Boolean value |
| integer | `i:123;` | `123` | Integer |
| double | `d:1.5;` | `1.5` | Floating point number |
| string | `s:5:"hello";` | `"hello"` | String |
| array | `a:2:{...}` | `[...]` or `{...}` | Array or object |
| object | `O:8:"ClassName":1:{...}` | `{...}` | Object |
| custom | `C:8:"ClassName":1:{...}` | `{...}` | Custom serializable |
| enum | `E:4:"Test":5:"value";` | `"value"` | Treated as string |
| reference | `r:1;` | (referenced value) | Object reference |
| ref value | `R:1;` | (referenced value) | Value reference |

## 🔧 Advanced Features

### Automatic Property Name Cleaning

PHP private/protected properties are processed as follows:

- **Public**: `propertyName` → `propertyName`
- **Protected**: `\0*\0propertyName` → `propertyName`
- **Private**: `\0ClassName\0propertyName` → `propertyName`
- **Other class private**: `\0OtherClass\0propertyName` → `OtherClass::propertyName`

### Array vs Object Detection

High-precision logic for array and object detection:

```kotlin
// Consecutive numeric indices starting from 0 → array
"a:3:{i:0;i:1;i:1;i:2;i:2;i:3;}" → "[1,2,3]"

// Non-consecutive or mixed keys → object
"a:2:{s:4:\"name\";s:4:\"John\";i:0;s:4:\"test\";}" → "{\"name\":\"John\",\"0\":\"test\"}"
```

### UTF-8 String Processing

Uses accurate UTF-8 byte length calculation logic and handles non-compliant encoding.

### Reference Processing

Supports PHP reference functionality:
- `r:N;`: Object reference (affects reference stack)
- `R:N;`: Value reference (does not affect reference stack)

## 🏗️ Architecture

```
src/main/kotlin/com/github/haradakunihiko/phpserialize/
├── PhpToJson.kt                    # Main conversion class
├── ConversionOptions.kt            # Configuration options and DSL
└── exceptions/
    └── PhpSerializeException.kt    # Exception class
```

### Key Components

- **PhpToJson**: Main class for PHP to JSON conversion with configurable options
- **ConversionOptions**: Configuration class with DSL support for customizing conversion behavior
- **PhpSerializeException**: Custom exception for handling conversion errors

## 🧪 Testing

```bash
./gradlew test
```

Main test cases:
- Basic type conversion tests
- Array/associative array conversion tests
- Object conversion tests (property visibility support)
- Custom class tests
- Reference processing tests
- UTF-8 string processing tests
- Real sample data tests

## 🤝 Contributing

1. Fork this repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is published under the MIT License. See the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) - JSON processing library
