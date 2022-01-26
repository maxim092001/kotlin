// TARGET_BACKEND: JVM_IR
// ISSUE: KT-38895

fun takeLong(x: Long): String = "O"

fun take(x: Int): String = "K"
fun take(x: Long): String = "Error"

fun box(): String {
    return takeLong(1 + 1) + take(1 + 1)
}

