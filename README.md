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
- **UTF-8 support**: Handles non-compliant encoding
- **Simple API**: Convert with just one method

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

// Method call
val json = PhpToJson.convert("i:123;")
println(json) // "123"

// Get as JsonElement
val jsonElement = PhpToJson.convertToJsonElement("b:1;")
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
        s:7:"\u0000*\u0000email";s:13:"john@test.com";
        s:9:"\u0000User\u0000age";i:30;
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
└── exceptions/
    └── PhpSerializeException.kt    # Exception class
```

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
