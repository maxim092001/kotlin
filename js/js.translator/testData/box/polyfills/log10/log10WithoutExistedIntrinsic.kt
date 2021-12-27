// WITH_STDLIB
// IGNORE_BACKEND: JS
// FILE: main.js
var isLegacyBackend =
    typeof Kotlin != "undefined" && typeof Kotlin.kotlin != "undefined"

if (!isLegacyBackend) {
    Math.log10 = undefined
}
// FILE: main.kt
import kotlin.math.log10

fun box(): String {
    assertEquals(log10(1.0), 0)
    assertEquals(log10(10.0), 1)
    assertEquals(log10(100.0), 2)
    assertEquals(js("Math.log10.called"), js("undefined"))

    return "OK"
}
