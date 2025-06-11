@Grab('io.github.haradakunihiko:php-json-deserializer-kt:1.0.0')
import io.github.haradakunihiko.php_json_deserializer.PhpToJson

// テスト用データ
def testData = 's:11:"Hello World";'
def iterations = 10000

println "Performance comparison test:"
println "Iterations: ${iterations}"
println "=========================="

// 方法1: PhpToJsonの現在の実装（prettyPrint指定）
long start1 = System.currentTimeMillis()
for (int i = 0; i < iterations; i++) {
    PhpToJson.INSTANCE.convert(testData, false)
}
long time1 = System.currentTimeMillis() - start1
println "Method 1 (current implementation): ${time1}ms"

// 方法2: 通常の変換
long start2 = System.currentTimeMillis()
for (int i = 0; i < iterations; i++) {
    PhpToJson.INSTANCE.convert(testData)
}
long time2 = System.currentTimeMillis() - start2
println "Method 2 (default convert): ${time2}ms"

println ""
println "Performance difference: ${Math.abs(time1 - time2)}ms"

if (Math.abs(time1 - time2) < 50) {
    println "結論: prettyPrintオプションのコストは無視できるレベル"
    println "シンプルなAPIを優先する方が良い場合もある"
} else {
    println "結論: 最適化が有効"
}
