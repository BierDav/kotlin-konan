/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.konan.target

import org.jetbrains.kotlin.konan.util.InternalServer
import java.nio.file.Path
import org.jetbrains.kotlin.konan.properties.KonanPropertiesLoader
import org.jetbrains.kotlin.konan.properties.Properties
import org.jetbrains.kotlin.konan.util.ProgressCallback
import java.nio.file.Paths

class MingwConfigurablesImpl(target: KonanTarget, properties: Properties, dependenciesRoot: String?, progressCallback : ProgressCallback) : MingwConfigurables,
    KonanPropertiesLoader(target, properties, dependenciesRoot, progressCallback = progressCallback) {
    override val windowsKit: WindowsKit by lazy {
        when (windowsSdkPartsProvider) {
            WindowsSdkPartsProvider.InternalServer -> createCustomWindowsKitPath(Paths.get(absolute(windowsKitParts)))
            WindowsSdkPartsProvider.Local -> WindowsKit.DefaultPath
        }
    }

    private val windowsSdkPartsProvider by lazy {
        if (InternalServer.isAvailable) {
            WindowsSdkPartsProvider.InternalServer
        } else {
            WindowsSdkPartsProvider.Local
        }
    }

    private val windowsHostDependencies by lazy {
        when (windowsSdkPartsProvider) {
            WindowsSdkPartsProvider.InternalServer -> listOf(windowsKitParts, msvcParts)
            WindowsSdkPartsProvider.Local -> emptyList()
        }
    }

    override val dependencies
        get() = super.dependencies + if (HostManager.hostIsMingw) {
            windowsHostDependencies
        } else {
            emptyList()
        }
}

private fun createCustomWindowsKitPath(windowsKitParts: Path): WindowsKit.CustomPath {
    return WindowsKit.CustomPath(
            libraryDirectories = listOf(
                    windowsKitParts.resolve("Lib").resolve("ucrt").resolve("x64"),
                    windowsKitParts.resolve("Lib").resolve("um").resolve("x64")
            ),
            includeDirectories = listOf(
                    windowsKitParts.resolve("Include").resolve("ucrt")
            )
    )
}

sealed class WindowsKit {
    abstract fun compilerFlags(): List<String>

    object DefaultPath : WindowsKit() {
        override fun compilerFlags(): List<String> = emptyList()
    }

    class CustomPath(
            private val includeDirectories: List<Path>,
            private val libraryDirectories: List<Path>
    ) : WindowsKit() {
        // Note that this approach doesn't exclude default Windows Kit path.
        // TODO: A better (but harder) way would be LIB environment variable.
        override fun compilerFlags(): List<String> =
                includeDirectories.flatMap { listOf("-isystem", it.toAbsolutePath().toString()) } +
                        libraryDirectories.flatMap { listOf("-L", it.toAbsolutePath().toString()) }

    }
}

private sealed class WindowsSdkPartsProvider {
    object Local : WindowsSdkPartsProvider()
    object InternalServer : WindowsSdkPartsProvider()
}