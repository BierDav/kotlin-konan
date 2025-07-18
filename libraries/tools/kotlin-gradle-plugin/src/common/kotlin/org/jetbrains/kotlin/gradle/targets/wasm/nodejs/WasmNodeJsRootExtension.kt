/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.targets.wasm.nodejs

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.targets.web.HasPlatformDisambiguator
import org.jetbrains.kotlin.gradle.targets.web.nodejs.BaseNodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec

/**
 * Extension for configuring Node.js-specific settings and tasks in a Kotlin/JS project,
 * specifically tailored towards supporting WebAssembly (Wasm) runtime via Node.js.
 */
abstract class WasmNodeJsRootExtension internal constructor(
    project: Project,
    nodeJs: () -> NodeJsEnvSpec,
    rootDir: String,
) : BaseNodeJsRootExtension(
    project,
    nodeJs,
    rootDir
), HasPlatformDisambiguator by WasmPlatformDisambiguator {

    companion object : HasPlatformDisambiguator by WasmPlatformDisambiguator {
        val EXTENSION_NAME: String
            get() = extensionName("nodeJs")
    }
}