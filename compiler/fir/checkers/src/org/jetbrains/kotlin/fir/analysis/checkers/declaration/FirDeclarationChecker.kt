/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.declaration

import org.jetbrains.kotlin.CompilerVersionOfApiDeprecation
import org.jetbrains.kotlin.DeprecatedForRemovalCompilerApi
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.fir.analysis.checkers.FirCheckerWithMppKind
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirFunction

// We don't declare it as `in D` because we want to prevent accidentally adding more general checkers to sets of specific checkers.
abstract class FirDeclarationChecker<D : FirDeclaration>(final override val mppKind: MppCheckerKind) : FirCheckerWithMppKind {
    @OptIn(DeprecatedForRemovalCompilerApi::class)
    context(context: CheckerContext, reporter: DiagnosticReporter)
    open fun check(declaration: D) {
        check(declaration, context, reporter)
    }

    @DeprecatedForRemovalCompilerApi(CompilerVersionOfApiDeprecation._2_2_0)
    open fun check(declaration: D, context: CheckerContext, reporter: DiagnosticReporter) {
        throw NotImplementedError("Neither overload of 'check' was overridden.")
    }
}
