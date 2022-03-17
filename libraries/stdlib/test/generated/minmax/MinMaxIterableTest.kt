/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */


package test.generated.minmax

//
// NOTE: THIS FILE IS AUTO-GENERATED by the MinMaxTestGenerator.kt
// See: https://github.com/JetBrains/kotlin/tree/master/libraries/stdlib
//

import kotlin.math.pow
import kotlin.test.*
import test.*

class MinMaxIterableTest {

    
    private fun <T : Comparable<T>> expectMinMax(min: T, max: T, elements: Iterable<T>) {
        assertEquals(min, elements.minOrNull())
        assertEquals(max, elements.maxOrNull())
        assertEquals(min, elements.min())
        assertEquals(max, elements.max())
    }

    @Test
    fun minMax() {
        expectMinMax("a", "a", listOf("a"))
        expectMinMax("a", "bcd", listOf("a", "bcd"))
        expectMinMax("a", "e", listOf("a", "bcd", "e"))
        expectMinMax(1, Int.MAX_VALUE, listOf(1, 2, Int.MAX_VALUE))
        expectMinMax(1, Long.MAX_VALUE, listOf(1, 2, Long.MAX_VALUE))
        expectMinMax(1U, UInt.MAX_VALUE, listOf(1U, 2U, UInt.MAX_VALUE))
        expectMinMax('a', Char.MAX_VALUE, listOf('a', 'b', Char.MAX_VALUE))
                    
    }

    @Test
    fun minMaxEmpty() {
        val empty = listOf<Int>()
        assertNull(empty.minOrNull())
        assertNull(empty.maxOrNull())
        assertFailsWith<NoSuchElementException> { empty.min() }
        assertFailsWith<NoSuchElementException> { empty.max() }
    }


    @Test
    fun minMaxDouble() {
        val zeroes = listOf(0.0, -0.0).shuffled()
        val NaNs = listOf(0.0, Double.NaN).shuffled()

        assertIsNegativeZero(zeroes.min().toDouble())
        assertIsNegativeZero(zeroes.minOrNull()!!.toDouble())
        assertTrue(NaNs.min().isNaN())
        assertTrue(NaNs.minOrNull()!!.isNaN())

        assertIsPositiveZero(zeroes.max().toDouble())
        assertIsPositiveZero(zeroes.maxOrNull()!!.toDouble())
        assertTrue(NaNs.max().isNaN())
        assertTrue(NaNs.maxOrNull()!!.isNaN())             

    }


    @Test
    fun minMaxFloat() {
        val zeroes = listOf(0.0F, -0.0F).shuffled()
        val NaNs = listOf(0.0F, Float.NaN).shuffled()

        assertIsNegativeZero(zeroes.min().toDouble())
        assertIsNegativeZero(zeroes.minOrNull()!!.toDouble())
        assertTrue(NaNs.min().isNaN())
        assertTrue(NaNs.minOrNull()!!.isNaN())

        assertIsPositiveZero(zeroes.max().toDouble())
        assertIsPositiveZero(zeroes.maxOrNull()!!.toDouble())
        assertTrue(NaNs.max().isNaN())
        assertTrue(NaNs.maxOrNull()!!.isNaN())             

    }



    private fun <T> expectMinMaxWith(min: T, max: T, elements: Iterable<T>, comparator: Comparator<T>) {
        assertEquals(min, elements.minWithOrNull(comparator))
        assertEquals(max, elements.maxWithOrNull(comparator))
        assertEquals(min, elements.minWith(comparator))
        assertEquals(max, elements.maxWith(comparator))
    }

    @Test
    fun minMaxWith() {
        expectMinMaxWith("a", "a", listOf("a"), naturalOrder())
        expectMinMaxWith("a", "bcd", listOf("a", "bcd"), naturalOrder())
        expectMinMaxWith("a", "e", listOf("a", "bcd", "e"), naturalOrder())
        expectMinMaxWith("a", "B", listOf("a", "B"), String.CASE_INSENSITIVE_ORDER)

    }

    @Test
    fun minMaxWithEmpty() {
        val empty = listOf<Int>()
        assertNull(empty.minWithOrNull(naturalOrder()))
        assertNull(empty.maxWithOrNull(naturalOrder()))
        assertFailsWith<NoSuchElementException> { empty.minWith(naturalOrder()) }
        assertFailsWith<NoSuchElementException> { empty.maxWith(naturalOrder()) }
    }


    private inline fun <T, K : Comparable<K>> expectMinMaxBy(min: T, max: T, elements: Iterable<T>, selector: (T) -> K) {
        assertEquals(min, elements.minBy(selector))
        assertEquals(min, elements.minByOrNull(selector))
        assertEquals(max, elements.maxBy(selector))
        assertEquals(max, elements.maxByOrNull(selector))
    }

    @Test
    fun minMaxBy() {
        expectMinMaxBy("a", "a", listOf("a"), { it })
        expectMinMaxBy("a", "bcd", listOf("a", "bcd"), { it })
        expectMinMaxBy("a", "e", listOf("a", "bcd", "e"), { it })
        expectMinMaxBy("De", "abc", listOf("abc", "De"), { it.length })

    }

    @Test
    fun minMaxByEmpty() {
        val empty = listOf<Int>()
        assertNull(empty.minByOrNull { it.toString() })
        assertNull(empty.maxByOrNull { it.toString() })
        assertFailsWith<NoSuchElementException> { empty.minBy { it.toString() } }
        assertFailsWith<NoSuchElementException> { empty.maxBy { it.toString() } }                       
    }

    @Test 
    fun minBySelectorEvaluateOnce() {
        val source = listOf("a", "bcd", "e")
        var c = 0
        source.minBy { c++ }
        assertEquals(3, c)
        c = 0
        source.minByOrNull { c++ }
        assertEquals(3, c)
    }

    @Test 
    fun maxBySelectorEvaluateOnce() {
        val source = listOf("a", "bcd", "e")
        var c = 0
        source.maxBy { c++ }
        assertEquals(3, c)
        c = 0
        source.maxByOrNull { c++ }
        assertEquals(3, c)
    }
    
    
    private inline fun <T, R : Comparable<R>> expectMinMaxOf(min: R, max: R, elements: Iterable<T>, selector: (T) -> R) {
        assertEquals(min, elements.minOf(selector))
        assertEquals(min, elements.minOfOrNull(selector))
        assertEquals(max, elements.maxOf(selector))
        assertEquals(max, elements.maxOfOrNull(selector))
    }
    
    @Test
    fun minMaxOf() {
        expectMinMaxOf("_" + "a", "_" + "a", listOf("a"), { "_$it" })
        expectMinMaxOf("_" + "a", "_" + "bcd", listOf("a", "bcd"), { "_$it" })
        expectMinMaxOf("_" + "a", "_" + "e", listOf("a", "bcd", "e"), { "_$it" })

    }
    
    @Test
    fun minMaxOfDouble() {
        val middle = "bcd"
        val items = listOf("a", "bcd", "e").shuffled()
        assertTrue(items.minOf { it.compareTo(middle).toDouble().pow(0.5) }.isNaN())
        assertTrue(items.minOfOrNull { it.compareTo(middle).toDouble().pow(0.5) }!!.isNaN())
        assertTrue(items.maxOf { it.compareTo(middle).toDouble().pow(0.5) }.isNaN())
        assertTrue(items.maxOfOrNull { it.compareTo(middle).toDouble().pow(0.5) }!!.isNaN())
        
        assertIsNegativeZero(items.minOf { it.compareTo(middle) * 0.0 })
        assertIsNegativeZero(items.minOfOrNull { it.compareTo(middle) * 0.0 }!!)
        assertIsPositiveZero(items.maxOf { it.compareTo(middle) * 0.0 })
        assertIsPositiveZero(items.maxOfOrNull { it.compareTo(middle) * 0.0 }!!)
    }
    
    @Test
    fun minMaxOfFloat() {
        val middle = "bcd"
        val items = listOf("a", "bcd", "e").shuffled()
        assertTrue(items.minOf { it.compareTo(middle).toFloat().pow(0.5F) }.isNaN())
        assertTrue(items.minOfOrNull { it.compareTo(middle).toFloat().pow(0.5F) }!!.isNaN())
        assertTrue(items.maxOf { it.compareTo(middle).toFloat().pow(0.5F) }.isNaN())
        assertTrue(items.maxOfOrNull { it.compareTo(middle).toFloat().pow(0.5F) }!!.isNaN())
        
        assertIsNegativeZero(items.minOf { it.compareTo(middle) * 0.0F }.toDouble())
        assertIsNegativeZero(items.minOfOrNull { it.compareTo(middle) * 0.0F }!!.toDouble())
        assertIsPositiveZero(items.maxOf { it.compareTo(middle) * 0.0F }.toDouble())
        assertIsPositiveZero(items.maxOfOrNull { it.compareTo(middle) * 0.0F }!!.toDouble())
    }
    
    @Test
    fun minMaxOfEmpty() {
        val empty = listOf<Int>()

        assertNull(empty.minOfOrNull { it.toString() })
        assertNull(empty.maxOfOrNull { it.toString() })
        assertFailsWith<NoSuchElementException> { empty.minOf { it.toString() } }
        assertFailsWith<NoSuchElementException> { empty.maxOf { it.toString() } }                       


        assertNull(empty.minOfOrNull { 0.0 })
        assertNull(empty.maxOfOrNull { 0.0 })
        assertFailsWith<NoSuchElementException> { empty.minOf { 0.0 } }
        assertFailsWith<NoSuchElementException> { empty.maxOf { 0.0 } }                       


        assertNull(empty.minOfOrNull { 0.0F })
        assertNull(empty.maxOfOrNull { 0.0F })
        assertFailsWith<NoSuchElementException> { empty.minOf { 0.0F } }
        assertFailsWith<NoSuchElementException> { empty.maxOf { 0.0F } }                       


    }
    
    
    private inline fun <T, R> expectMinMaxOfWith(min: R, max: R, elements: Iterable<T>, comparator: Comparator<R>, selector: (T) -> R) {
        assertEquals(min, elements.minOfWith(comparator, selector))
        assertEquals(min, elements.minOfWithOrNull(comparator, selector))
        assertEquals(max, elements.maxOfWith(comparator, selector))
        assertEquals(max, elements.maxOfWithOrNull(comparator, selector))
    }
    
    @Test
    fun minMaxOfWith() {
        expectMinMaxOfWith("_" + "a", "_" + "a", listOf("a"), reverseOrder(), { "_$it" })
        expectMinMaxOfWith("_" + "bcd", "_" + "a", listOf("a", "bcd"), reverseOrder(), { "_$it" })
        expectMinMaxOfWith("_" + "e", "_" + "a", listOf("a", "bcd", "e"), reverseOrder(), { "_$it" })

    }
    
    @Test
    fun minMaxOfWithEmpty() {
        val empty = listOf<Int>()
        assertNull(empty.minOfWithOrNull(naturalOrder()) { it.toString() })
        assertNull(empty.maxOfWithOrNull(naturalOrder()) { it.toString() })
        assertFailsWith<NoSuchElementException> { empty.minOfWith(naturalOrder()) { it.toString() } }
        assertFailsWith<NoSuchElementException> { empty.maxOfWith(naturalOrder()) { it.toString() } }
    }

}
