/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.backend.js.transformers.irToJs

import org.jetbrains.kotlin.backend.common.compilationException
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.backend.js.utils.JsGenerationContext
import org.jetbrains.kotlin.ir.backend.js.utils.emptyScope
import org.jetbrains.kotlin.ir.backend.js.utils.getJsFunAnnotation
import org.jetbrains.kotlin.js.backend.ast.*

class FunctionWithJsFuncAnnotationInliner(private val jsFuncCall: IrCall, private val context: JsGenerationContext) {
    private val function = getJsFunctionImplementation()
    private val replacements = collectReplacementsForCall()

    fun generateStatement(): JsStatement {
        val statements = function.body.statements.replaceDeclaredParameterWithActual()
        return when (statements.size) {
            0 -> JsEmpty
            1 -> statements.single().withSource(jsFuncCall, context)
            // TODO: use transparent block (e.g. JsCompositeBlock)
            else -> JsBlock(statements)
        }
    }

    fun generateExpression(): JsExpression {
        val statements = function.body.statements.replaceDeclaredParameterWithActual()

        if (statements.isEmpty()) return JsPrefixOperation(JsUnaryOperator.VOID, JsIntLiteral(3)) // TODO: report warning or even error

        val lastStatement = statements.last()
        if (statements.size == 1) {
            if (lastStatement is JsExpressionStatement) return lastStatement.expression.withSource(jsFuncCall, context)
        }

        val newStatements = statements.toMutableList()

        when (lastStatement) {
            is JsReturn -> {
            }
            is JsExpressionStatement -> {
                newStatements[statements.lastIndex] = JsReturn(lastStatement.expression)
            }
            // TODO: report warning or even error
            else -> newStatements += JsReturn(JsPrefixOperation(JsUnaryOperator.VOID, JsIntLiteral(3)))
        }

        val syntheticFunction = JsFunction(emptyScope, JsBlock(newStatements), "")
        return JsInvocation(syntheticFunction).withSource(jsFuncCall, context)
    }

    private fun getJsFunctionImplementation(): JsFunction {
        val code = jsFuncCall.symbol.owner.getJsFunAnnotation() ?: compilationException("JsFun annotation is expected", jsFuncCall)
        val statements = parseJsCode(code) ?: compilationException("Cannot compute js code", jsFuncCall)
        return statements.singleOrNull()
            ?.let { it as? JsExpressionStatement }
            ?.let { it.expression as? JsFunction } ?: compilationException("Provided js code is not a js function", jsFuncCall.symbol.owner)
    }

    private fun collectReplacementsForCall(): Map<JsName, JsExpression> {
        val translatedArguments = Array(jsFuncCall.valueArgumentsCount) {
            jsFuncCall.getValueArgument(it)!!.accept(IrElementToJsExpressionTransformer(), context)
        }
        return function.parameters
            .mapIndexed { i, param -> param.name to translatedArguments[i] }
            .toMap()
    }

    private fun List<JsStatement>.replaceDeclaredParameterWithActual(): List<JsStatement> {
        return apply { SimpleJsCodeInliner(replacements).acceptList(this) }
    }
}

private class SimpleJsCodeInliner(private val replacements: Map<JsName, JsExpression>): RecursiveJsVisitor() {
    private val temporaryNamesForExpressions = mutableMapOf<JsName, JsExpression>()
    val neededToDeclareVariables: Map<JsName, JsExpression> get() = temporaryNamesForExpressions

    override fun visitNameRef(nameRef: JsNameRef) {
        super.visitNameRef(nameRef)
        if (nameRef.qualifier != null) return
        nameRef.name = nameRef.name?.getReplacement() ?: return
    }

    private fun declareNewTemporaryFor(expression: JsExpression): JsName {
        return JsName("tmp", true)
            .also { temporaryNamesForExpressions[it] = expression }
    }

    private fun JsName.getReplacement(): JsName? {
        val expression = replacements[this] ?: return null
        return when {
            expression is JsNameRef && expression.qualifier == null -> expression.name!!
            else -> declareNewTemporaryFor(expression)
        }
    }
}