@Grab('io.github.haradakunihiko:php-json-deserializer-kt:1.0.0')
import io.github.haradakunihiko.php_json_deserializer.PhpToJson

println "PhpToJson library test with prettyPrint:"
println "========================================"

// テスト用のオブジェクトデータ
def complexObject = 'O:4:"Test":3:{s:4:"name";s:4:"John";s:3:"age";i:30;s:7:"address";a:3:{s:4:"city";s:5:"Tokyo";s:7:"country";s:5:"Japan";s:8:"zipcode";s:7:"100-001";}}'

println "Complex Object Test:"
println "PHP serialized:"
println complexObject
println ""

println "Compact JSON:"
println PhpToJson.INSTANCE.convert(complexObject)
println ""

println "Pretty-printed JSON (with parameter):"
println PhpToJson.INSTANCE.convert(complexObject, true)
println ""

println "Pretty-printed JSON (with convenience method):"
println PhpToJson.INSTANCE.convertPretty(complexObject)
println ""

println "Test completed successfully!"
