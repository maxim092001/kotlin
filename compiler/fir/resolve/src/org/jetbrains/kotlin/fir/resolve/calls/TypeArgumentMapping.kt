/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.resolve.calls

import org.jetbrains.kotlin.fir.declarations.FirCallableDeclaration
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.declarations.FirTypeParameterRefsOwner
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.fir.types.ConeTypeIntersector
import org.jetbrains.kotlin.fir.types.FirTypeProjection
import org.jetbrains.kotlin.fir.types.builder.buildPlaceholderProjection
import org.jetbrains.kotlin.fir.types.builder.buildTypeProjectionWithVariance
import org.jetbrains.kotlin.fir.types.impl.ConeClassLikeTypeImpl
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.fir.types.builder.buildTypeProjectionWithVariance
import org.jetbrains.kotlin.fir.types.isRaw
import org.jetbrains.kotlin.fir.types.toFirResolvedTypeRef
import org.jetbrains.kotlin.types.Variance

sealed class TypeArgumentMapping {
    abstract operator fun get(typeParameterIndex: Int): FirTypeProjection

    object NoExplicitArguments : TypeArgumentMapping() {
        override fun get(typeParameterIndex: Int): FirTypeProjection = buildPlaceholderProjection()
    }

    class Mapped(private val ordered: List<FirTypeProjection>) : TypeArgumentMapping() {
        override fun get(typeParameterIndex: Int): FirTypeProjection {
            return ordered.getOrElse(typeParameterIndex) { buildPlaceholderProjection() }
        }
    }
}

internal object MapTypeArguments : ResolutionStage() {
    override suspend fun check(candidate: Candidate, callInfo: CallInfo, sink: CheckerSink, context: ResolutionContext) {
        val typeArguments = enrichedTypeArgumentsWithSelfType(candidate, callInfo, context, callInfo.typeArguments)
        val owner = candidate.symbol.fir as FirTypeParameterRefsOwner

        if (typeArguments.isEmpty()) {
            if (owner is FirCallableDeclaration && owner.dispatchReceiverType?.isRaw() == true) {
                candidate.typeArgumentMapping = computeDefaultMappingForRawTypeMember(owner, context)
            } else {
                candidate.typeArgumentMapping = TypeArgumentMapping.NoExplicitArguments
            }
            return
        }

        if (
            typeArguments.size == owner.typeParameters.size ||
            callInfo.callKind == CallKind.DelegatingConstructorCall ||
            (owner as? FirDeclaration)?.origin is FirDeclarationOrigin.DynamicScope
        ) {
            candidate.typeArgumentMapping = TypeArgumentMapping.Mapped(typeArguments)
        } else {
            candidate.typeArgumentMapping = TypeArgumentMapping.Mapped(
                if (typeArguments.size > owner.typeParameters.size) typeArguments.take(owner.typeParameters.size)
                else typeArguments
            )
            sink.yieldDiagnostic(InapplicableCandidate)
        }
    }

    private fun enrichedTypeArgumentsWithSelfType(
        candidate: Candidate,
        callInfo: CallInfo,
        context: ResolutionContext,
        typeArguments: List<FirTypeProjection>
    ): List<FirTypeProjection> {
        if (candidate.symbol is FirConstructorSymbol) {

            val firConstructorSymbol = candidate.symbol

            if (firConstructorSymbol.callableId.classId != null) {

                val classLikeSymbol = context.session.symbolProvider.getClassLikeSymbolByClassId(firConstructorSymbol.callableId.classId!!)

                if (classLikeSymbol != null) {
                    val firClassSymbol = classLikeSymbol as FirClassSymbol

                    val isSelf = firClassSymbol.hasAnnotation(StandardClassIds.Annotations.Self)

                    if (isSelf && callInfo.callKind is CallKind.Function) {
                        val constructorTypeParametersSize = firConstructorSymbol.typeParameterSymbols.size
                        val callTypeArgumentsSize = typeArguments.size

                        if (constructorTypeParametersSize != callTypeArgumentsSize) {
                            val coneArgumentsWithStar =
                                (typeArguments.map { it.toConeTypeProjection() }.toList() + ConeStarProjection).toTypedArray()
                            val typeRef = firConstructorSymbol.fir.returnTypeRef
                            val oldConeType = typeRef.coneType as ConeClassLikeType
                            val coneTypeWithStar =
                                ConeClassLikeTypeImpl(
                                    oldConeType.lookupTag,
                                    coneArgumentsWithStar,
                                    oldConeType.isNullable,
                                    oldConeType.attributes
                                )

                            val typeRefWithStar = typeRef.withReplacedConeType(coneTypeWithStar)
                            val baseClassStarProjection = buildTypeProjectionWithVariance {
                                source = typeRefWithStar.source
                                this.typeRef = typeRefWithStar
                                variance = Variance.INVARIANT
                            }

                            return typeArguments + baseClassStarProjection
                        }
                    }
                }

            }
        }
        return typeArguments
    }

    private fun computeDefaultMappingForRawTypeMember(
        owner: FirTypeParameterRefsOwner,
        context: ResolutionContext
    ): TypeArgumentMapping.Mapped {
        // There might be some minor inconsistencies where in K2, there might be a raw type, while in K1, there was a regular flexible type
        // And in that case for K2 we would start a regular inference process leads to TYPE_INFERENCE_NO_INFORMATION_FOR_PARAMETER because raw scopes
        // don't leave type variables there (see KT-54526)
        // Also, it might be a separate feature of K2, because even in cases where both compilers had a raw type, it's convenient not to
        // require explicit type arguments for the places where it doesn't make sense
        // (See `generic1.foo(w)` call in testData/diagnostics/tests/platformTypes/rawTypes/noTypeArgumentsForRawScopedMembers.fir.kt)
        val resultArguments = owner.typeParameters.map { typeParameterRef ->
            buildTypeProjectionWithVariance {
                typeRef =
                    ConeTypeIntersector.intersectTypes(
                        context.typeContext, typeParameterRef.symbol.resolvedBounds.map { it.type }
                    ).toFirResolvedTypeRef()
                variance = Variance.INVARIANT
            }
        }
        return TypeArgumentMapping.Mapped(resultArguments)
    }
}

internal object NoTypeArguments : ResolutionStage() {
    override suspend fun check(candidate: Candidate, callInfo: CallInfo, sink: CheckerSink, context: ResolutionContext) {
        if (callInfo.typeArguments.isNotEmpty()) {
            sink.yieldDiagnostic(InapplicableCandidate)
        }
        candidate.typeArgumentMapping = TypeArgumentMapping.NoExplicitArguments
    }
}
