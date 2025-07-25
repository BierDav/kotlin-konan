/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.konan.target

enum class Architecture(@Deprecated("Compare Architecture entries instead.") val bitness: Int) {
    X64(64),
    X86(32),
    ARM64(64),
    ARM32(32),
    XTENSA(32);

    @Deprecated(message = REMOVED_TARGET_MESSAGE, level = DeprecationLevel.ERROR)
    object MIPS32

    @Deprecated(message = REMOVED_TARGET_MESSAGE, level = DeprecationLevel.ERROR)
    object MIPSEL32

    @Deprecated(message = REMOVED_TARGET_MESSAGE, level = DeprecationLevel.ERROR)
    object WASM32
}
