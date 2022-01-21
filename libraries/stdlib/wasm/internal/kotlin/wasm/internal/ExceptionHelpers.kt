/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.wasm.internal

@JsFun("(message, wasmTypeName, stack) => { const error = new Error(); error.message = message; error.name = wasmTypeName; error.stack = stack; return error }")
private external fun buildJsError(message: ExternalInterfaceType?, wasmTypeName: ExternalInterfaceType?, stack: ExternalInterfaceType): ExternalInterfaceType

@JsFun("(e) => { throw e }")
private external fun jsThrow(jsError: ExternalInterfaceType)

internal fun throwAsJsException(t: Throwable): Nothing {
    jsThrow(
        buildJsError(
            kotlinToJsStringAdapter(t.message),
            kotlinToJsStringAdapter(t::class.simpleName),
            t.jsStack
        )
    )
    error("!!!")
}

internal var isNotFirstWasmExportCall: Boolean = false
