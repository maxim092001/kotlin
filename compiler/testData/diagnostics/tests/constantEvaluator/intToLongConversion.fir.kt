// FIR_DUMP
// ISSUE: KT-38895

fun takeByte(x: Byte) {}
fun takeShort(x: Short) {}
fun takeInt(x: Int) {}
fun takeLong(x: Long) {}

fun take(x: Int): Int = x
fun take(x: Long): Long = x

fun test_1() {
    takeByte(<!ARGUMENT_TYPE_MISMATCH!>1 + 1<!>)
    takeShort(<!ARGUMENT_TYPE_MISMATCH!>1 + 1<!>)
    takeInt(1 + 1)
    takeLong(1 + 1)

    takeLong(1000000 * 1000000)
}

fun test_2() {
    val x = take(1 + 1)
    val y = take(1000000 * 1000000)
}

