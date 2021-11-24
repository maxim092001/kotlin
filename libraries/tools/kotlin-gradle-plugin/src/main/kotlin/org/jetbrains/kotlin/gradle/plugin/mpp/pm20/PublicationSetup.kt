/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin.mpp.pm20

interface PublicationSetup<in T : KotlinGradleVariant> : KotlinGradleFragmentFactory.FragmentConfigurator<T> {

    object NoPublication : PublicationSetup<KotlinGradleVariant> {
        override fun configure(fragment: KotlinGradleVariant) = Unit
    }

    object SingleVariantPublication : PublicationSetup<KotlinGradlePublishedVariantWithRuntime> {
        override fun configure(fragment: KotlinGradlePublishedVariantWithRuntime) {
            VariantPublishingConfigurator.get(fragment.project).configureSingleVariantPublication(fragment)
        }
    }

    object NativeVariantPublication : PublicationSetup<KotlinNativeVariantInternal> {
        override fun configure(fragment: KotlinNativeVariantInternal) {
            VariantPublishingConfigurator.get(fragment.project).configureNativeVariantPublication(fragment)
        }
    }
}
