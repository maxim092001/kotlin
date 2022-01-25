/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.resolve.transformers.body.resolve

import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds

object ConvertibleIntegerOperators {
    val operators = setOf(
        "plus".id, "minus".id, "times".id, "div".id, "rem".id,
        "and".id, "or".id, "xor".id,
        "shl".id, "shr".id, "ushr".id
    )

    private val String.id: CallableId
        get() = CallableId(StandardClassIds.Int, Name.identifier(this))
}
