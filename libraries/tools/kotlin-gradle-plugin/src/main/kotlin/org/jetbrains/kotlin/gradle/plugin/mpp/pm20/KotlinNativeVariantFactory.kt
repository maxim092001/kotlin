/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("FunctionName")

package org.jetbrains.kotlin.gradle.plugin.mpp.pm20

import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.FragmentNameDisambiguation

fun <T : KotlinNativeVariantInternal> KotlinNativeVariantFactory(
    nativeVariantInstantiator: KotlinNativeVariantInstantiator<T>,
    nativeVariantConfigurator: KotlinNativeVariantConfigurator<T> = KotlinNativeVariantConfigurator()
) = KotlinGradleFragmentFactory(
    fragmentInstantiator = nativeVariantInstantiator,
    fragmentConfigurator = nativeVariantConfigurator
)

class KotlinNativeVariantInstantiator<T : KotlinNativeVariantInternal>(
    private val module: KotlinGradleModule,

    private val kotlinNativeVariantConstructor: KotlinNativeVariantConstructor<T>,

    private val dependenciesConfigurationFactory: KotlinDependencyConfigurationsFactory =
        DefaultKotlinDependencyConfigurationsFactory,

    private val compileDependenciesConfigurationFactory: KotlinCompileDependenciesConfigurationFactory =
        DefaultKotlinCompileDependenciesConfigurationFactory,

    private val apiElementsConfigurationFactory: KotlinApiElementsConfigurationFactory =
        DefaultKotlinApiElementsConfigurationFactory,

    private val hostSpecificMetadataElementsConfigurationFactory: KotlinNativeHostSpecificMetadataElementsConfigurationFactory? =
        DefaultKotlinNativeHostSpecificMetadataElementsConfigurationFactory(kotlinNativeVariantConstructor.konanTarget)

) : KotlinGradleFragmentFactory.FragmentInstantiator<T> {

    override fun create(name: String): T {
        val names = FragmentNameDisambiguation(module, name)
        val dependencies = dependenciesConfigurationFactory.create(module, names)
        return kotlinNativeVariantConstructor.invoke(
            containingModule = module,
            fragmentName = name,
            dependencyConfigurations = dependencies,
            compileDependencyConfiguration = compileDependenciesConfigurationFactory.create(module, names, dependencies),
            apiElementsConfiguration = apiElementsConfigurationFactory.create(module, names, dependencies),
            hostSpecificMetadataElementsConfiguration =
            hostSpecificMetadataElementsConfigurationFactory?.create(module, names, dependencies)
        )
    }
}

class KotlinNativeVariantConfigurator<T : KotlinNativeVariantInternal>(
    private val compileTaskConfigurator: KotlinCompileTaskConfigurator<T> =
        KotlinNativeCompileTaskConfigurator,

    private val sourceArchiveTaskConfigurator: KotlinSourceArchiveTaskConfigurator<T> =
        DefaultKotlinSourceArchiveTaskConfigurator,

    private val sourceDirectoriesSetup: KotlinSourceDirectoriesSetup<T> =
        DefaultKotlinSourceDirectoriesSetup,

    private val compileDependenciesSetup: KotlinConfigurationsSetup<T> =
        DefaultKotlinCompileDependenciesSetup + KonanTargetAttributesSetup,

    private val apiElementsSetup: KotlinConfigurationsSetup<T> =
        DefaultKotlinApiElementsSetup + KonanTargetAttributesSetup,

    private val hostSpecificMetadataElementsSetup: KotlinConfigurationsSetup<T> =
        DefaultHostSpecificMetadataElementsSetup,

    private val hostSpecificMetadataArtifactConfigurator: KotlinGradleFragmentFactory.FragmentConfigurator<T> =
        KotlinHostSpecificMetadataArtifactConfigurator,

    private val publicationSetup: PublicationSetup<KotlinNativeVariantInternal> = PublicationSetup.NativeVariantPublication
) : KotlinGradleFragmentFactory.FragmentConfigurator<T> {

    override fun configure(fragment: T) {
        compileDependenciesSetup.configure(fragment, fragment.compileDependencyConfiguration)
        apiElementsSetup.configure(fragment, fragment.apiElementsConfiguration)

        fragment.hostSpecificMetadataElementsConfiguration?.let { hostSpecificMetadataElementsConfiguration ->
            hostSpecificMetadataElementsSetup.configure(fragment, hostSpecificMetadataElementsConfiguration)
        }

        compileTaskConfigurator.registerCompileTasks(fragment)
        sourceArchiveTaskConfigurator.registerSourceArchiveTask(fragment)
        sourceDirectoriesSetup.configure(fragment)
        hostSpecificMetadataArtifactConfigurator.configure(fragment)
        publicationSetup.configure(fragment)
    }
}
