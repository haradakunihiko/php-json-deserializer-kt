@Grab('io.github.haradakunihiko:php-json-deserializer-kt:1.2.0')
import com.github.haradakunihiko.phpserialize.PhpToJson

println "PhpToJson library test with prettyPrint:"
println "========================================"

// テスト用のシンプルなデータ
def simpleString = 's:5:"Hello";'
def simpleInt = 'i:42;'
def simpleArray = 'a:2:{i:0;s:3:"foo";i:1;s:3:"bar";}'
def simpleObject = 'O:4:"Test":2:{s:4:"name";s:4:"John";s:3:"age";i:30;}'

def converter = new PhpToJson()

println "Simple String Test:"
println "PHP serialized: ${simpleString}"
println "JSON: ${converter.convert(simpleString, false)}"
println ""

println "Simple Integer Test:"
println "PHP serialized: ${simpleInt}"
println "JSON: ${converter.convert(simpleInt, false)}"
println ""

println "Simple Array Test:"
println "PHP serialized: ${simpleArray}"
println "JSON (compact): ${converter.convert(simpleArray, false)}"
println "JSON (pretty): ${converter.convert(simpleArray, true)}"
println ""

println "Simple Object Test:"
println "PHP serialized: ${simpleObject}"
println "JSON (compact): ${converter.convert(simpleObject, false)}"
println "JSON (pretty): ${converter.convert(simpleObject, true)}"
println ""

println "Test completed successfully!"
