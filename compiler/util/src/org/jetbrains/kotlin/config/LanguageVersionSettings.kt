/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.config

import org.jetbrains.kotlin.config.LanguageFeature.Kind.*
import org.jetbrains.kotlin.config.LanguageFeatureBehaviorAfterSinceVersion.CanStillBeDisabledForNow
import org.jetbrains.kotlin.config.LanguageFeatureBehaviorAfterSinceVersion.CannotBeDisabled
import org.jetbrains.kotlin.config.LanguageVersion.*
import org.jetbrains.kotlin.utils.DescriptionAware
import java.util.*

sealed class LanguageFeatureBehaviorAfterSinceVersion {
    data object CannotBeDisabled : LanguageFeatureBehaviorAfterSinceVersion()
    data class CanStillBeDisabledForNow(val relevantTicketId: String) : LanguageFeatureBehaviorAfterSinceVersion()
}

enum class LanguageFeature(
    val sinceVersion: LanguageVersion?,
    val sinceApiVersion: ApiVersion = ApiVersion.KOTLIN_1_0,
    val hintUrl: String? = null,
    internal val isEnabledWithWarning: Boolean = false,
    val kind: Kind = OTHER, // NB: default value OTHER doesn't force pre-releaseness (see KDoc)
    val behaviorAfterSinceVersion: LanguageFeatureBehaviorAfterSinceVersion = CannotBeDisabled,
) {
    // Note: names of these entries are also used in diagnostic tests and in user-visible messages (see presentableText below)

    // 1.1

    TypeAliases(KOTLIN_1_1),
    BoundCallableReferences(KOTLIN_1_1, ApiVersion.KOTLIN_1_1),
    LocalDelegatedProperties(KOTLIN_1_1, ApiVersion.KOTLIN_1_1),
    TopLevelSealedInheritance(KOTLIN_1_1),
    AdditionalBuiltInsMembers(KOTLIN_1_1),
    DataClassInheritance(KOTLIN_1_1),
    InlineProperties(KOTLIN_1_1),
    DestructuringLambdaParameters(KOTLIN_1_1),
    SingleUnderscoreForParameterName(KOTLIN_1_1),
    DslMarkersSupport(KOTLIN_1_1),
    UnderscoresInNumericLiterals(KOTLIN_1_1),
    DivisionByZeroInConstantExpressions(KOTLIN_1_1),
    InlineConstVals(KOTLIN_1_1),
    OperatorProvideDelegate(KOTLIN_1_1),
    ShortSyntaxForPropertyGetters(KOTLIN_1_1),
    RefinedSamAdaptersPriority(KOTLIN_1_1),
    SafeCallBoundSmartCasts(KOTLIN_1_1),
    TypeInferenceOnGenericsForCallableReferences(KOTLIN_1_1),
    NoDelegationToJavaDefaultInterfaceMembers(KOTLIN_1_1),
    Coroutines(
        KOTLIN_1_1, ApiVersion.KOTLIN_1_1,
        "https://kotlinlang.org/docs/diagnostics/experimental-coroutines",
        isEnabledWithWarning = true
    ),

    // 1.2

    InlineDefaultFunctionalParameters(KOTLIN_1_2),
    SoundSmartCastsAfterTry(KOTLIN_1_2),
    NullabilityAssertionOnExtensionReceiver(KOTLIN_1_2),
    SafeCastCheckBoundSmartCasts(KOTLIN_1_2),
    CapturedInClosureSmartCasts(KOTLIN_1_2),
    LateinitTopLevelProperties(KOTLIN_1_2),
    LateinitLocalVariables(KOTLIN_1_2),
    InnerClassInEnumEntryClass(KOTLIN_1_2),
    CallableReferencesToClassMembersWithEmptyLHS(KOTLIN_1_2),
    JvmPackageName(KOTLIN_1_2),
    AssigningArraysToVarargsInNamedFormInAnnotations(KOTLIN_1_2),
    ExpectedTypeFromCast(KOTLIN_1_2),

    // 1.3

    RestrictionOfValReassignmentViaBackingField(KOTLIN_1_3, kind = BUG_FIX),
    NestedClassesInEnumEntryShouldBeInner(KOTLIN_1_3, kind = BUG_FIX),
    ProhibitDataClassesOverridingCopy(KOTLIN_1_3, kind = BUG_FIX),
    RestrictionOfWrongAnnotationsWithUseSiteTargetsOnTypes(KOTLIN_1_3, kind = BUG_FIX),
    ProhibitInnerClassesOfGenericClassExtendingThrowable(KOTLIN_1_3, kind = BUG_FIX),
    ProperForInArrayLoopRangeVariableAssignmentSemantic(KOTLIN_1_3, kind = BUG_FIX),
    NestedClassesInAnnotations(KOTLIN_1_3),
    JvmStaticInInterface(KOTLIN_1_3, kind = UNSTABLE_FEATURE),
    JvmFieldInInterface(KOTLIN_1_3, kind = UNSTABLE_FEATURE),
    ProhibitVisibilityOfNestedClassifiersFromSupertypesOfCompanion(KOTLIN_1_3, kind = BUG_FIX),
    ProhibitNonConstValuesAsVarargsInAnnotations(KOTLIN_1_3, kind = BUG_FIX),
    ReleaseCoroutines(KOTLIN_1_3, kind = UNSTABLE_FEATURE),
    ReadDeserializedContracts(KOTLIN_1_3),
    UseReturnsEffect(KOTLIN_1_3),
    UseCallsInPlaceEffect(KOTLIN_1_3),
    AllowContractsForCustomFunctions(KOTLIN_1_3),
    VariableDeclarationInWhenSubject(KOTLIN_1_3),
    ProhibitLocalAnnotations(KOTLIN_1_3, kind = BUG_FIX),
    ProhibitSmartcastsOnLocalDelegatedProperty(KOTLIN_1_3, kind = BUG_FIX),
    ProhibitAssigningSingleElementsToVarargsInNamedForm(KOTLIN_1_3, kind = BUG_FIX),
    FunctionTypesWithBigArity(KOTLIN_1_3, sinceApiVersion = ApiVersion.KOTLIN_1_3),
    RestrictRetentionForExpressionAnnotations(KOTLIN_1_3, kind = BUG_FIX),
    StrictJavaNullabilityAssertions(KOTLIN_1_3, kind = BUG_FIX),
    SoundSmartcastForEnumEntries(KOTLIN_1_3, kind = BUG_FIX),
    ProhibitErroneousExpressionsInAnnotationsWithUseSiteTargets(KOTLIN_1_3, kind = BUG_FIX),
    NewCapturedReceiverFieldNamingConvention(KOTLIN_1_3, kind = BUG_FIX),
    ExtendedMainConvention(KOTLIN_1_3),
    ExperimentalBuilderInference(KOTLIN_1_3),
    InlineClasses(KOTLIN_1_3, isEnabledWithWarning = true, kind = UNSTABLE_FEATURE),

    // 1.4

    DslMarkerOnFunctionTypeReceiver(KOTLIN_1_4, kind = BUG_FIX),
    RestrictReturnStatementTarget(KOTLIN_1_4, kind = BUG_FIX),
    WarningOnMainUnusedParameter(KOTLIN_1_4),
    PolymorphicSignature(KOTLIN_1_4),
    ProhibitConcurrentHashMapContains(KOTLIN_1_4, kind = BUG_FIX),
    ProhibitTypeParametersForLocalVariables(KOTLIN_1_4, kind = BUG_FIX),
    ProhibitJvmOverloadsOnConstructorsOfAnnotationClasses(KOTLIN_1_4, kind = BUG_FIX),
    ProhibitTypeParametersInAnonymousObjects(KOTLIN_1_4, kind = BUG_FIX),
    ProhibitRepeatedUseSiteTargetAnnotations(KOTLIN_1_4, kind = BUG_FIX),
    ProhibitUseSiteTargetAnnotationsOnSuperTypes(KOTLIN_1_4, kind = BUG_FIX),
    ProhibitTypeParametersInClassLiteralsInAnnotationArguments(KOTLIN_1_4, kind = BUG_FIX),
    ProhibitComparisonOfIncompatibleEnums(KOTLIN_1_4, kind = BUG_FIX),
    BareArrayClassLiteral(KOTLIN_1_4),
    ProhibitGenericArrayClassLiteral(KOTLIN_1_4),
    NonParenthesizedAnnotationsOnFunctionalTypes(KOTLIN_1_4),
    UseGetterNameForPropertyAnnotationsMethodOnJvm(KOTLIN_1_4),
    AllowBreakAndContinueInsideWhen(KOTLIN_1_4),
    MixedNamedArgumentsInTheirOwnPosition(KOTLIN_1_4),
    ProhibitTailrecOnVirtualMember(KOTLIN_1_4, kind = BUG_FIX),
    ProperComputationOrderOfTailrecDefaultParameters(KOTLIN_1_4),
    TrailingCommas(KOTLIN_1_4),
    ProhibitProtectedCallFromInline(KOTLIN_1_4, kind = BUG_FIX),
    ProperFinally(KOTLIN_1_4, kind = BUG_FIX),
    AllowAssigningArrayElementsToVarargsInNamedFormForFunctions(KOTLIN_1_4),
    AllowNullOperatorsForResult(KOTLIN_1_4),
    PreferJavaFieldOverload(KOTLIN_1_4),
    AllowContractsForNonOverridableMembers(KOTLIN_1_4),
    AllowReifiedGenericsInContracts(KOTLIN_1_4),
    ProperVisibilityForCompanionObjectInstanceField(KOTLIN_1_4, kind = BUG_FIX),
    DoNotGenerateThrowsForDelegatedKotlinMembers(KOTLIN_1_4),
    ProperIeee754Comparisons(KOTLIN_1_4, kind = BUG_FIX),
    FunctionalInterfaceConversion(KOTLIN_1_4, kind = UNSTABLE_FEATURE),
    GenerateJvmOverloadsAsFinal(KOTLIN_1_4),
    MangleClassMembersReturningInlineClasses(KOTLIN_1_4),
    ImproveReportingDiagnosticsOnProtectedMembersOfBaseClass(KOTLIN_1_4, kind = BUG_FIX),

    NewInference(KOTLIN_1_4),

    // In the next block, features can be enabled only along with new inference
    // v----------------------------------------------------------------------v
    SamConversionForKotlinFunctions(KOTLIN_1_4),
    SamConversionPerArgument(KOTLIN_1_4),
    FunctionReferenceWithDefaultValueAsOtherType(KOTLIN_1_4),
    OverloadResolutionByLambdaReturnType(KOTLIN_1_4),
    ContractsOnCallsWithImplicitReceiver(KOTLIN_1_4),
    // ^----------------------------------------------------------------------^

    // 1.5

    ProhibitSpreadOnSignaturePolymorphicCall(KOTLIN_1_5, kind = BUG_FIX),
    ProhibitInvisibleAbstractMethodsInSuperclasses(KOTLIN_1_5, kind = BUG_FIX),
    ProhibitNonReifiedArraysAsReifiedTypeArguments(KOTLIN_1_5, kind = BUG_FIX),
    ProhibitVarargAsArrayAfterSamArgument(KOTLIN_1_5, kind = BUG_FIX),
    CorrectSourceMappingSyntax(KOTLIN_1_5, kind = UNSTABLE_FEATURE),
    RequiredPrimaryConstructorDelegationCallInEnums(KOTLIN_1_5, kind = BUG_FIX),
    ApproximateAnonymousReturnTypesInPrivateInlineFunctions(KOTLIN_1_5, kind = BUG_FIX),
    ForbidReferencingToUnderscoreNamedParameterOfCatchBlock(KOTLIN_1_5, kind = BUG_FIX),
    UseCorrectExecutionOrderForVarargArguments(KOTLIN_1_5, kind = BUG_FIX),
    JvmRecordSupport(KOTLIN_1_5),
    AllowNullOperatorsForResultAndResultReturnTypeByDefault(KOTLIN_1_5),
    AllowSealedInheritorsInDifferentFilesOfSamePackage(KOTLIN_1_5),
    SealedInterfaces(KOTLIN_1_5),
    JvmInlineValueClasses(KOTLIN_1_5, kind = OTHER),
    SuspendFunctionsInFunInterfaces(KOTLIN_1_5, kind = OTHER),
    SamWrapperClassesAreSynthetic(KOTLIN_1_5, kind = BUG_FIX),
    StrictOnlyInputTypesChecks(KOTLIN_1_5),

    // 1.6

    ProhibitJvmFieldOnOverrideFromInterfaceInPrimaryConstructor(KOTLIN_1_6, kind = BUG_FIX),
    PrivateInFileEffectiveVisibility(KOTLIN_1_6, kind = BUG_FIX),
    ProhibitSelfCallsInNestedObjects(KOTLIN_1_6, kind = BUG_FIX),
    ProperCheckAnnotationsTargetInTypeUsePositions(KOTLIN_1_6, kind = BUG_FIX),
    SuspendFunctionAsSupertype(KOTLIN_1_6),
    UnrestrictedBuilderInference(KOTLIN_1_6),
    ClassTypeParameterAnnotations(KOTLIN_1_6),
    WarnAboutNonExhaustiveWhenOnAlgebraicTypes(KOTLIN_1_6, kind = BUG_FIX),
    InstantiationOfAnnotationClasses(KOTLIN_1_6),
    OptInContagiousSignatures(KOTLIN_1_6, kind = BUG_FIX),
    RepeatableAnnotations(KOTLIN_1_6),
    RepeatableAnnotationContainerConstraints(KOTLIN_1_6, kind = BUG_FIX),
    UseBuilderInferenceOnlyIfNeeded(KOTLIN_1_6),
    SuspendConversion(KOTLIN_1_6),
    ProhibitSuperCallsFromPublicInline(KOTLIN_1_6),
    ProhibitProtectedConstructorCallFromPublicInline(KOTLIN_1_6),

    // 1.7

    /*
     * Improvements include the following:
     *  - taking into account for type enhancement freshly supported type use annotations: KT-11454
     *  - use annotations in the type parameter position to enhance corresponding types: KT-11454
     *  - proper support of the type enhancement of the annotated java arrays: KT-24392
     *  - proper support of the type enhancement of the annotated java varargs' elements: KT-18768
     *  - type enhancement based on annotated bounds of type parameters
     *  - type enhancement within type arguments of the base classes and interfaces
     *  - support type enhancement based on type use annotations on java fields
     *  - preference of a type use annotation to annotation of another type: KT-24392
     *      (if @NotNull has TYPE_USE and METHOD target, then `@NotNull Integer []` -> `Array<Int>..Array<out Int>?` instead of `Array<Int>..Array<out Int>`)
     */
    TypeEnhancementImprovementsInStrictMode(KOTLIN_1_7, behaviorAfterSinceVersion = CanStillBeDisabledForNow("KT-76100")),
    OptInRelease(KOTLIN_1_7),
    ProhibitNonExhaustiveWhenOnAlgebraicTypes(KOTLIN_1_7, kind = BUG_FIX),
    UseBuilderInferenceWithoutAnnotation(KOTLIN_1_7),
    ProhibitSmartcastsOnPropertyFromAlienBaseClass(KOTLIN_1_7, kind = BUG_FIX),
    ProhibitInvalidCharsInNativeIdentifiers(KOTLIN_1_7, kind = BUG_FIX),
    DefinitelyNonNullableTypes(KOTLIN_1_7),
    ProhibitSimplificationOfNonTrivialConstBooleanExpressions(KOTLIN_1_7),
    SafeCallsAreAlwaysNullable(KOTLIN_1_7),
    JvmPermittedSubclassesAttributeForSealed(KOTLIN_1_7),
    ProperTypeInferenceConstraintsProcessing(KOTLIN_1_7, kind = BUG_FIX),
    ForbidExposingTypesInPrimaryConstructorProperties(KOTLIN_1_7, kind = BUG_FIX),
    PartiallySpecifiedTypeArguments(KOTLIN_1_7),
    EliminateAmbiguitiesWithExternalTypeParameters(KOTLIN_1_7),
    EliminateAmbiguitiesOnInheritedSamInterfaces(KOTLIN_1_7),
    ProperInternalVisibilityCheckInImportingScope(KOTLIN_1_7, kind = BUG_FIX),
    InlineClassImplementationByDelegation(KOTLIN_1_7),
    QualifiedSupertypeMayBeExtendedByOtherSupertype(KOTLIN_1_7),
    YieldIsNoMoreReserved(KOTLIN_1_7),
    NoDeprecationOnDeprecatedEnumEntries(KOTLIN_1_7), // KT-37975
    ProhibitQualifiedAccessToUninitializedEnumEntry(KOTLIN_1_7, kind = BUG_FIX), // KT-41124
    ForbidRecursiveDelegateExpressions(KOTLIN_1_7, kind = BUG_FIX),
    KotlinFunInterfaceConstructorReference(KOTLIN_1_7),
    SuspendOnlySamConversions(KOTLIN_1_7),

    // 1.8

    DontLoseDiagnosticsDuringOverloadResolutionByReturnType(KOTLIN_1_8),
    ProhibitConfusingSyntaxInWhenBranches(KOTLIN_1_8, kind = BUG_FIX), // KT-48385
    UseConsistentRulesForPrivateConstructorsOfSealedClasses(sinceVersion = KOTLIN_1_8, kind = BUG_FIX), // KT-44866
    ProgressionsChangingResolve(KOTLIN_1_8), // KT-49276
    AbstractClassMemberNotImplementedWithIntermediateAbstractClass(KOTLIN_1_8, kind = BUG_FIX), // KT-45508
    ForbidSuperDelegationToAbstractAnyMethod(KOTLIN_1_8, kind = BUG_FIX), // KT-38078
    ProperEqualityChecksInBuilderInferenceCalls(KOTLIN_1_8, kind = BUG_FIX),
    ProhibitNonExhaustiveIfInRhsOfElvis(KOTLIN_1_8, kind = BUG_FIX), // KT-44705
    ReportMissingUpperBoundsViolatedErrorOnAbbreviationAtSupertypes(KOTLIN_1_8, kind = BUG_FIX), // KT-29168
    ForbidUsingExtensionPropertyTypeParameterInDelegate(KOTLIN_1_8, kind = BUG_FIX), // KT-24643
    SynchronizedSuspendError(KOTLIN_1_8, kind = BUG_FIX), // KT-48516
    ReportNonVarargSpreadOnGenericCalls(KOTLIN_1_8, kind = BUG_FIX), // KT-48162
    RangeUntilOperator(KOTLIN_1_8), // KT-15613
    GenericInlineClassParameter(sinceVersion = KOTLIN_1_8, kind = UNSTABLE_FEATURE), // KT-32162

    // 1.9

    ProhibitIllegalValueParameterUsageInDefaultArguments(KOTLIN_1_9, kind = BUG_FIX), // KT-25694
    ProhibitConstructorCallOnFunctionalSupertype(KOTLIN_1_9, kind = BUG_FIX), // KT-46344
    ProhibitArrayLiteralsInCompanionOfAnnotation(KOTLIN_1_9, kind = BUG_FIX), // KT-39041
    ProhibitCyclesInAnnotations(KOTLIN_1_9, kind = BUG_FIX), // KT-47932
    ForbidExtensionFunctionTypeOnNonFunctionTypes(KOTLIN_1_9, kind = BUG_FIX), // related to KT-43527
    ProhibitEnumDeclaringClass(KOTLIN_1_9, kind = BUG_FIX), // KT-49653
    StopPropagatingDeprecationThroughOverrides(KOTLIN_1_9, kind = BUG_FIX), // KT-47902
    ReportTypeVarianceConflictOnQualifierArguments(KOTLIN_1_9, kind = BUG_FIX), // KT-50947
    ReportErrorsOnRecursiveTypeInsidePlusAssignment(KOTLIN_1_9, kind = BUG_FIX), // KT-48546
    ForbidExtensionCallsOnInlineFunctionalParameters(KOTLIN_1_9, kind = BUG_FIX), // KT-52502
    SkipStandaloneScriptsInSourceRoots(KOTLIN_1_9, kind = OTHER), // KT-52525
    ModifierNonBuiltinSuspendFunError(KOTLIN_1_9, kind = BUG_FIX), // KT-49264
    EnumEntries(KOTLIN_1_9, sinceApiVersion = ApiVersion.KOTLIN_1_8, kind = UNSTABLE_FEATURE), // KT-48872
    ForbidSuperDelegationToAbstractFakeOverride(KOTLIN_1_9, kind = BUG_FIX), // KT-49017
    DataObjects(KOTLIN_1_9), // KT-4107
    ProhibitAccessToEnumCompanionMembersInEnumConstructorCall(KOTLIN_1_9, kind = BUG_FIX), // KT-49110
    RefineTypeCheckingOnAssignmentsToJavaFields(KOTLIN_1_9, kind = BUG_FIX), // KT-46727
    ValueClassesSecondaryConstructorWithBody(sinceVersion = KOTLIN_1_9, kind = UNSTABLE_FEATURE), // KT-55333
    NativeJsProhibitLateinitIsInitializedIntrinsicWithoutPrivateAccess(KOTLIN_1_9, kind = BUG_FIX), // KT-27002
    TakeIntoAccountEffectivelyFinalInMustBeInitializedCheck(KOTLIN_1_9, kind = OTHER), // KT-58587
    ProhibitUsingNullableTypeParameterAgainstNotNullAnnotated(sinceVersion = KOTLIN_1_9), // KT-36770
    NoSourceCodeInNotNullAssertionExceptions(KOTLIN_1_9, sinceApiVersion = ApiVersion.KOTLIN_1_4, kind = OTHER), // KT-57570

    // 1.9.20 KMP stabilization. Unfortunately, we don't have 1.9.20 LV. So LV=1.9 is the best we can do.
    // At least there won't be false positives for 1.8 users
    MultiplatformRestrictions(KOTLIN_1_9, kind = BUG_FIX), // KT-61668

    // End of 1.* language features --------------------------------------------------

    // 2.0

    EnhanceNullabilityOfPrimitiveArrays(KOTLIN_2_0, kind = BUG_FIX), // KT-54521

    /**
     * This feature is highly related to ForbidInferringTypeVariablesIntoEmptyIntersection and while they belong to the same LV,
     * they might be used interchangeably.
     *
     * But there might be the case that we may postpone ForbidInferringTypeVariablesIntoEmptyIntersection but leave AllowEmptyIntersectionsInResultTypeResolver in 2.0.
     * In that case, we would stick to the simple behavior of just inferring empty intersection (without complicated logic of filtering out expected constraints),
     * but we would report a warning instead of an error (until ForbidInferringTypeVariablesIntoEmptyIntersection is enabled).
     */
    AllowEmptyIntersectionsInResultTypeResolver(KOTLIN_2_0, kind = OTHER), // KT-51221
    ProhibitSmartcastsOnPropertyFromAlienBaseClassInheritedInInvisibleClass(KOTLIN_2_0, kind = BUG_FIX), // KT-57290
    ForbidInferringPostponedTypeVariableIntoDeclaredUpperBound(KOTLIN_2_0, kind = BUG_FIX), // KT-47986
    ProhibitUseSiteGetTargetAnnotations(KOTLIN_2_0, kind = BUG_FIX), // KT-15470
    KeepNullabilityWhenApproximatingLocalType(KOTLIN_2_0, kind = BUG_FIX), // KT-53982
    ProhibitAccessToInvisibleSetterFromDerivedClass(KOTLIN_2_0, kind = BUG_FIX), // KT-56662
    ProhibitOpenValDeferredInitialization(KOTLIN_2_0, kind = BUG_FIX), // KT-57553
    SupportEffectivelyFinalInExpectActualVisibilityCheck(KOTLIN_2_0, kind = BUG_FIX), // KT-61955
    ProhibitMissedMustBeInitializedWhenThereIsNoPrimaryConstructor(KOTLIN_2_0, kind = BUG_FIX), // KT-58472
    MangleCallsToJavaMethodsWithValueClasses(KOTLIN_2_0, kind = OTHER), // KT-55945
    ProhibitDefaultArgumentsInExpectActualizedByFakeOverride(KOTLIN_2_0, kind = BUG_FIX), // KT-62036
    DisableCompatibilityModeForNewInference(KOTLIN_2_0, kind = OTHER), // KT-63558 (umbrella), KT-64306, KT-64307, KT-64308
    DfaBooleanVariables(KOTLIN_2_0), // KT-25747
    LightweightLambdas(KOTLIN_2_0, kind = OTHER), // KT-45375
    ObjCSignatureOverrideAnnotation(KOTLIN_2_0, sinceApiVersion = ApiVersion.KOTLIN_2_0, kind = OTHER), // KT-61323

    // 2.1

    ProhibitImplementingVarByInheritedVal(KOTLIN_2_1, kind = BUG_FIX), // KT-56779
    PrioritizedEnumEntries(KOTLIN_2_1, kind = UNSTABLE_FEATURE), // KT-58920
    ProhibitInlineModifierOnPrimaryConstructorParameters(KOTLIN_2_1, kind = BUG_FIX), // KT-59664
    ProhibitSingleNamedFunctionAsExpression(KOTLIN_2_1, kind = BUG_FIX), // KT-62573
    ForbidLambdaParameterWithMissingDependencyType(KOTLIN_2_1, kind = BUG_FIX), // KT-64474
    JsAllowInvalidCharsIdentifiersEscaping(KOTLIN_2_1, kind = OTHER), // KT-31799
    SupportJavaErrorEnhancementOfArgumentsOfWarningLevelEnhanced(KOTLIN_2_1, kind = BUG_FIX), // KT-63209
    ProhibitPrivateOperatorCallInInline(KOTLIN_2_1, kind = BUG_FIX), // KT-65494
    ProhibitTypealiasAsCallableQualifierInImport(KOTLIN_2_1, kind = BUG_FIX), // KT-64350
    JsExternalPropertyParameters(KOTLIN_2_1), // KT-65965
    CorrectSpecificityCheckForSignedAndUnsigned(KOTLIN_2_1, kind = OTHER), // KT-35305
    AllowAccessToProtectedFieldFromSuperCompanion(KOTLIN_2_1), // KT-39868
    CheckLambdaAgainstTypeVariableContradictionInResolution(KOTLIN_2_1, kind = OTHER), // KT-58310
    ProperUninitializedEnumEntryAccessAnalysis(KOTLIN_2_1, kind = BUG_FIX), // KT-68451
    ImprovedCapturedTypeApproximationInInference(KOTLIN_2_1, kind = OTHER), // KT-64515
    ImprovedVarianceInCst(KOTLIN_2_1, kind = OTHER), // KT-68970
    InferMoreImplicationsFromBooleanExpressions(KOTLIN_2_1, kind = OTHER), // KT-64193
    ImprovedExhaustivenessChecksIn21(KOTLIN_2_1, kind = OTHER), // KT-21908
    ProhibitSynchronizationByValueClassesAndPrimitives(KOTLIN_2_1, kind = BUG_FIX), // KT-67791
    AllowSuperCallToJavaInterface(KOTLIN_2_1, kind = OTHER), // KT-69729
    ProhibitJavaClassInheritingPrivateKotlinClass(KOTLIN_2_1, kind = BUG_FIX), // KT-66328
    ProhibitReturningIncorrectNullabilityValuesFromSamConstructorLambdaOfJdkInterfaces(KOTLIN_2_1, kind = BUG_FIX), // KT-57014
    ProhibitNothingAsCatchParameter(KOTLIN_2_1, kind = BUG_FIX), // KT-8322
    NullableNothingInReifiedPosition(KOTLIN_2_1, kind = UNSTABLE_FEATURE), // KT-54227, KT-67675
    ElvisInferenceImprovementsIn21(KOTLIN_2_1, kind = OTHER), // KT-71751
    // TODO: Remove org.jetbrains.kotlin.fir.resolve.calls.stages.ConstraintSystemForks together with this LF (KT-72961)
    ConsiderForkPointsWhenCheckingContradictions(KOTLIN_2_1), // KT-68768

    // It's not a fully blown LF, but mostly a way to manage potential unexpected semantic changes
    // See the single usage at org.jetbrains.kotlin.fir.types.ConeTypeApproximator.fastPathSkipApproximation
    AvoidApproximationOfRecursiveCapturedTypesWithNoReason(KOTLIN_2_1, kind = OTHER), // KT-69995
    PCLAEnhancementsIn21(KOTLIN_2_1, kind = OTHER), // KT-69170

    // Common feature for all non-PCLA inference enhancements in 2.1
    InferenceEnhancementsIn21(KOTLIN_2_1, kind = OTHER), // KT-61227

    // It's not a fully blown LF, but mostly a way to manage potential unexpected semantic changes
    // See the single usage at org.jetbrains.kotlin.resolve.calls.inference.components.ConstraintIncorporator.computeNewDerivedFrom
    // We enable it for already released 2.1 because it's a bug fix
    StricterConstraintIncorporationRecursionDetector(KOTLIN_2_1, kind = OTHER), // KT-73434

    // It's not a fully blown LF, but mostly a way to manage potential unexpected semantic changes
    // See the single usage at org.jetbrains.kotlin.resolve.calls.inference.components.ConstraintInjector.TypeCheckerStateForConstraintInjector.runForkingPoint
    // We enable it for already released 2.1 because it's a bug fix
    ForkIsNotSuccessfulWhenNoBranchIsSuccessful(KOTLIN_2_1, kind = OTHER), // KT-75444

    // 2.2

    BreakContinueInInlineLambdas(KOTLIN_2_2), // KT-1436
    ForbidUsingExpressionTypesWithInaccessibleContent(KOTLIN_2_2, kind = BUG_FIX), // KT-66691
    ReportExposedTypeForMoreCasesOfTypeParameterBounds(KOTLIN_2_2, kind = BUG_FIX), // KT-69653
    ForbidReifiedTypeParametersOnTypeAliases(KOTLIN_2_2, kind = BUG_FIX), // KT-70163
    ForbidProjectionsInAnnotationProperties(KOTLIN_2_2, kind = BUG_FIX), // KT-70002
    ForbidJvmAnnotationsOnAnnotationParameters(KOTLIN_2_2, kind = BUG_FIX), // KT-25861
    ForbidFieldAnnotationsOnAnnotationParameters(KOTLIN_2_2, kind = BUG_FIX), // KT-70233
    ProhibitConstructorAndSupertypeOnTypealiasWithTypeProjection(KOTLIN_2_2, kind = BUG_FIX), // KT-60305
    CallableReferenceOverloadResolutionInLambda(KOTLIN_2_2), // KT-73011
    ForbidInferOfInvisibleTypeAsReifiedOrVararg(KOTLIN_2_2, kind = BUG_FIX), // KT-25513
    ProhibitGenericQualifiersOnConstructorCalls(KOTLIN_2_2, kind = BUG_FIX), // KT-73527
    AvoidWrongOptimizationOfTypeOperatorsOnValueClasses(KOTLIN_2_2), // KT-67517, KT-67518, KT-67520
    ForbidSyntheticPropertiesWithoutBaseJavaGetter(KOTLIN_2_2, kind = BUG_FIX), // KT-72305, KT-64358
    AnnotationDefaultTargetMigrationWarning(KOTLIN_2_2, kind = BUG_FIX), // KT-73255, KT-73494
    AllowDnnTypeOverridingFlexibleType(KOTLIN_2_2, kind = OTHER), // KT-74049
    ForbidEnumEntryNamedEntries(KOTLIN_2_2, kind = BUG_FIX), // KT-72829, KT-58920
    WhenGuards(KOTLIN_2_2, kind = OTHER), // KT-13626
    MultiDollarInterpolation(KOTLIN_2_2, kind = OTHER), // KT-2425
    JvmDefaultEnableByDefault(KOTLIN_2_2, kind = OTHER), // KT-71768
    ForbidExposureOfPrivateTypesInNonPrivateInlineFunctionsInKlibs(sinceVersion = KOTLIN_2_2, kind = BUG_FIX), // KT-70916
    JvmIndyAllowLambdasWithAnnotations(KOTLIN_2_2, kind = OTHER), // KT-76606
    FixationEnhancementsIn22(KOTLIN_2_2, kind = OTHER), // KT-76345, KT-71854
    ForbidCrossFileIrFieldAccessInKlibs(KOTLIN_2_2, kind = BUG_FIX), // KT-71138
    AllowExpectDeclarationsInJsExport(KOTLIN_2_2, kind = OTHER), // KT-64951

    // 2.3

    ForbidCompanionInLocalInnerClass(KOTLIN_2_3, kind = BUG_FIX),
    ForbidImplementationByDelegationWithDifferentGenericSignature(KOTLIN_2_3, kind = BUG_FIX), // KT-72140
    ForbidJvmSerializableLambdaOnInlinedFunctionLiterals(KOTLIN_2_3, kind = BUG_FIX), // KT-71906
    ErrorAboutDataClassCopyVisibilityChange(KOTLIN_2_3, kind = BUG_FIX), // KT-11914 Deprecation phase 2
    ReportExposedTypeForInternalTypeParameterBounds(KOTLIN_2_3, kind = BUG_FIX), // KTLC-275
    EnableDfaWarningsInK2(KOTLIN_2_3, kind = OTHER), // KT-50965
    ForbidParenthesizedLhsInAssignments(KOTLIN_2_3, kind = BUG_FIX), // KT-70507
    AllowEagerSupertypeAccessibilityChecks(KOTLIN_2_3, kind = OTHER), // KT-73611
    DontMakeExplicitJavaTypeArgumentsFlexible(KOTLIN_2_3, kind = OTHER), // KT-71718
    ResolveTopLevelLambdasAsSyntheticCallArgument(KOTLIN_2_3, kind = OTHER), // KT-67869
    UnstableSmartcastOnDelegatedProperties(KOTLIN_2_3, kind = BUG_FIX), // KTLC-273
    ForbidAnnotationsWithUseSiteTargetOnExpressions(KOTLIN_2_3, kind = BUG_FIX), // KT-75242
    ProhibitNullableTypeThroughTypealias(KOTLIN_2_3, kind = BUG_FIX), // KTLC-279

    // End of 2.* language features --------------------------------------------------

    ExpectActualClasses(sinceVersion = null), // KT-62885

    DataClassCopyRespectsConstructorVisibility(sinceVersion = null), // KT-11914 Deprecation phase 3

    DirectJavaActualization(sinceVersion = null), // KT-67202

    // Disabled for indefinite time. See KT-53751
    IgnoreNullabilityForErasedValueParameters(sinceVersion = null, kind = BUG_FIX),

    // Disabled for indefinite time. Disables restrictions of builder inference without annotation
    // Note: In 1.7.0, builder inference without annotation was introduced.
    // However, later we encountered various situations when it works incorrectly, and decided to forbid them.
    // When this feature is disabled, various errors are reported which are related to these incorrect situations.
    // When this feature is enabled, no such errors are reported.
    NoBuilderInferenceWithoutAnnotationRestriction(sinceVersion = null, kind = OTHER),

    // Disabled for indefinite time. Forces K2 report errors (instead of warnings) for incompatible
    // equality & identity operators in cases where K1 would report warnings or would not report anything.
    ReportErrorsForComparisonOperators(sinceVersion = null, kind = BUG_FIX),

    // Disabled for indefinite time.
    // Disables reporting of new errors (see KT-55055, KT-55056, KT-55079) in DiagnosticReporterByTrackingStrategy.
    // All these errors are "lost" errors which existed always, but wasn't reported before 1.9.0.
    // When this feature is disabled, all these "lost" errors are reported properly.
    // When this feature is enabled, no such errors are reported.
    NoAdditionalErrorsInK1DiagnosticReporter(sinceVersion = null, kind = OTHER),

    // top-level script inner classes never made any sense, but used for some time to overcome the capturing logic limitations
    // Now capturing logic works properly, therefore the warning is reported in K2
    // this feature will eventually switch this warning to an error
    ProhibitScriptTopLevelInnerClasses(sinceVersion = null, kind = OTHER),

    // Experimental features

    ExpectRefinement(sinceVersion = null),
    JsEnableExtensionFunctionInExternals(null, kind = OTHER),
    PackagePrivateFileClassesWithAllPrivateMembers(null), // Disabled until the breaking change is approved by the committee, see KT-10884.
    MultiPlatformProjects(sinceVersion = null),
    ProhibitComparisonOfIncompatibleClasses(sinceVersion = null, kind = BUG_FIX),
    ProhibitAllMultipleDefaultsInheritedFromSupertypes(sinceVersion = null, kind = BUG_FIX),
    ProhibitIntersectionReifiedTypeParameter(sinceVersion = null, kind = BUG_FIX), // KT-71420
    ExplicitBackingFields(sinceVersion = null, kind = UNSTABLE_FEATURE), // KT-14663
    FunctionalTypeWithExtensionAsSupertype(sinceVersion = null),
    JsAllowValueClassesInExternals(sinceVersion = null, kind = OTHER),
    ContextReceivers(sinceVersion = null),
    ContextParameters(sinceVersion = null), // KT-72222
    ValueClasses(sinceVersion = null, kind = UNSTABLE_FEATURE),
    JavaSamConversionEqualsHashCode(sinceVersion = null, kind = UNSTABLE_FEATURE),
    PropertyParamAnnotationDefaultTargetMode(sinceVersion = null, kind = OTHER), // KT-73255
    AnnotationAllUseSiteTarget(sinceVersion = null, kind = OTHER), // KT-73256
    ImplicitJvmExposeBoxed(sinceVersion = null, kind = UNSTABLE_FEATURE), // KT-73466

    // K1 support only. We keep it, as we may want to support it also in K2
    UnitConversionsOnArbitraryExpressions(sinceVersion = null),

    JsAllowImplementingFunctionInterface(sinceVersion = null, kind = OTHER),
    CustomEqualsInValueClasses(sinceVersion = null, kind = OTHER), // KT-24874
    ContractSyntaxV2(sinceVersion = null, kind = UNSTABLE_FEATURE), // KT-56127
    ReferencesToSyntheticJavaProperties(sinceVersion = null, kind = TEST_ONLY), // KT-8575
    ImplicitSignedToUnsignedIntegerConversion(sinceVersion = null, kind = TEST_ONLY), // KT-56583
    ForbidInferringTypeVariablesIntoEmptyIntersection(sinceVersion = null, kind = BUG_FIX), // KT-51221
    IntrinsicConstEvaluation(sinceVersion = null, kind = TEST_ONLY), // KT-49303

    // K1 support only. We keep it, as it's currently unclear what to do with this feature in K2
    DisableCheckingChangedProgressionsResolve(sinceVersion = null, kind = OTHER), // KT-49276

    DontCreateSyntheticPropertiesWithoutBaseJavaGetter(sinceVersion = null, kind = OTHER), // KT-64358
    JavaTypeParameterDefaultRepresentationWithDNN(sinceVersion = null, kind = TEST_ONLY), // KT-59138
    ProperFieldAccessGenerationForFieldAccessShadowedByKotlinProperty(sinceVersion = null, kind = OTHER), // KT-56386
    IrInlinerBeforeKlibSerialization(sinceVersion = null, kind = UNSTABLE_FEATURE), // KT-69765
    NestedTypeAliases(sinceVersion = null, kind = UNSTABLE_FEATURE), // KT-45285
    ForbidUsingSupertypesWithInaccessibleContentInTypeArguments(sinceVersion = null, kind = BUG_FIX), // KT-66691, KT-66742
    UnnamedLocalVariables(sinceVersion = null, kind = OTHER), // KT-74809
    ContextSensitiveResolutionUsingExpectedType(sinceVersion = null, kind = OTHER), // KT-16768
    AnnotationsInMetadata(sinceVersion = null, kind = OTHER), // KT-57919
    DisableWarningsForValueBasedJavaClasses(sinceVersion = null, kind = OTHER), // KT-70722
    DisableWarningsForIdentitySensitiveOperationsOnValueClassesAndPrimitives(sinceVersion = null, kind = OTHER), // KT-70722
    IrRichCallableReferencesInKlibs(sinceVersion = null, kind = OTHER), // KT-72734, KT-74384, KT-74392
    ExportKlibToOlderAbiVersion(sinceVersion = null, kind = UNSTABLE_FEATURE), // KT-76131
    ;

    init {
        if (sinceVersion == null && isEnabledWithWarning) {
            error("$this: '${::isEnabledWithWarning.name}' has no effect if the feature is disabled by default")
        }

        if (kind.testOnly && sinceVersion != null) {
            error("$this: '${::isEnabledWithWarning.name}' should be enabled by default since version $sinceVersion but is test only")
        }
    }

    val presentableName: String
        // E.g. "DestructuringLambdaParameters" -> ["Destructuring", "Lambda", "Parameters"] -> "destructuring lambda parameters"
        get() = name.split("(?<!^)(?=[A-Z])".toRegex()).joinToString(separator = " ", transform = String::lowercase)

    val presentableText get() = if (hintUrl == null) presentableName else "$presentableName (See: $hintUrl)"

    enum class State(override val description: String) : DescriptionAware {
        ENABLED("Enabled"),
        ENABLED_WITH_WARNING("Enabled with warning"),
        DISABLED("Disabled");
    }

    /**
     * If 'true', then this feature will be automatically enabled under '-progressive' mode.
     *
     * Please, see `canBeEnabledInProgressiveMode` in [Kind] for more details.
     */
    val enabledInProgressiveMode: Boolean get() = kind.canBeEnabledInProgressiveMode && sinceVersion != null

    /**
     * # [forcesPreReleaseBinaries]
     * If 'true', then enabling this feature (e.g. by '-XXLanguage:', or dedicated '-X'-flag)
     * will force generation of pre-release binaries (given that [sinceVersion] > [LanguageVersion.LATEST_STABLE]).
     * Use it for features that involve generation of non-trivial low-level code with non-finalized design.
     *
     * Note that [forcesPreReleaseBinaries] makes sense only for features with [sinceVersion] > [LanguageVersion.LATEST_STABLE].
     *
     * Please, DO NOT use features that force pre-release binaries in the Kotlin project, as that would
     * generate 'kotlin-compiler' as pre-release.
     *
     *
     * # [canBeEnabledInProgressiveMode]
     * If 'true', then this feature will be automatically enabled under '-progressive' mode if `sinceKotlin` is set.
     *
     * Restrictions for using this flag for particular feature follow from restrictions of the progressive mode:
     * - enabling it *must not* break compatibility with non-progressive compiler, i.e. code written under progressive
     *   should compile successfully by non-progressive compiler with the same language version settings.
     *   Example: making some "red" code "green" is not fine, because non-progressive compilers won't be able to compile
     *   such code
     *
     * - changes in language semantics should not be "silent": user must receive some message from the compiler
     *   about all affected code. Exceptions are possible on case-by-case basis.
     *   Example: silently changing semantics of generated low-level code is not fine, but deprecating some language
     *   construction immediately instead of a going through complete deprecation cycle is fine.
     *
     * NB: Currently, [canBeEnabledInProgressiveMode] makes sense only for features with [sinceVersion] > [LanguageVersion.LATEST_STABLE]
     */
    enum class Kind(val canBeEnabledInProgressiveMode: Boolean, val forcesPreReleaseBinaries: Boolean, val testOnly: Boolean = false) {
        /**
         * Simple bug fix which just forbids some language constructions.
         * Rule of thumb: it turns "green code" into "red".
         *
         * Note that, some actual bug fixes can affect overload resolution/inference, silently changing semantics of
         * users' code -- DO NOT use Kind.BUG_FIX for them!
         */
        BUG_FIX(canBeEnabledInProgressiveMode = true, forcesPreReleaseBinaries = false),

        /**
         * Enables support of some new and *unstable* construction in language.
         * Rule of thumb: it turns "red" code into "green", and we want to strongly demotivate people from manually enabling
         * that feature in production.
         */
        UNSTABLE_FEATURE(canBeEnabledInProgressiveMode = false, forcesPreReleaseBinaries = true),

        /**
         * A new feature in the language which has no impact on the binary output of the compiler, and therefore
         * does not cause pre-release binaries to be generated.
         * Rule of thumb: it turns "red" code into "green" and the old compilers can correctly use the binaries
         * produced by the new compiler.
         *
         * NB. OTHER is not a conservative fallback, as it doesn't imply generation of pre-release binaries
         */
        OTHER(canBeEnabledInProgressiveMode = false, forcesPreReleaseBinaries = false),

        /**
         * A feature that can be used only in tests, thus it's neither possible to enable it in progressive mode,
         * nor it forces pre-release binaries
         */
        TEST_ONLY(canBeEnabledInProgressiveMode = false, forcesPreReleaseBinaries = false, testOnly = true),
    }

    companion object {
        @JvmStatic
        fun fromString(str: String) = entries.find { it.name == str }
    }
}

enum class LanguageVersion(val major: Int, val minor: Int) : DescriptionAware, LanguageOrApiVersion {
    KOTLIN_1_0(1, 0),
    KOTLIN_1_1(1, 1),
    KOTLIN_1_2(1, 2),
    KOTLIN_1_3(1, 3),
    KOTLIN_1_4(1, 4),
    KOTLIN_1_5(1, 5),
    KOTLIN_1_6(1, 6),
    KOTLIN_1_7(1, 7),
    KOTLIN_1_8(1, 8),
    KOTLIN_1_9(1, 9),

    KOTLIN_2_0(2, 0),
    KOTLIN_2_1(2, 1),
    KOTLIN_2_2(2, 2),
    KOTLIN_2_3(2, 3),
    ;

    override val isStable: Boolean
        get() = this <= LATEST_STABLE

    val usesK2: Boolean
        get() = this >= KOTLIN_2_0

    override val isDeprecated: Boolean
        get() = FIRST_SUPPORTED <= this && this < FIRST_NON_DEPRECATED

    override val isUnsupported: Boolean
        get() = this < FIRST_SUPPORTED

    override val versionString: String = "$major.$minor"

    override fun toString() = versionString

    companion object {
        @JvmStatic
        fun fromVersionString(str: String?) = entries.find { it.versionString == str }

        @JvmStatic
        fun fromFullVersionString(str: String) =
            str.split(".", "-").let { if (it.size >= 2) fromVersionString("${it[0]}.${it[1]}") else null }

        // Version status
        //            1.0..1.7        1.8..1.9           2.0..2.2    2.3
        // Language:  UNSUPPORTED --> DEPRECATED ------> STABLE ---> EXPERIMENTAL
        // API:       UNSUPPORTED --> DEPRECATED ------> STABLE ---> EXPERIMENTAL

        @JvmField
        val FIRST_API_SUPPORTED = KOTLIN_1_8

        @JvmField
        val FIRST_SUPPORTED = KOTLIN_1_8

        @JvmField
        val FIRST_NON_DEPRECATED = KOTLIN_2_0

        @JvmField
        val LATEST_STABLE = KOTLIN_2_2
    }
}

interface LanguageOrApiVersion : DescriptionAware {
    val versionString: String

    val isStable: Boolean

    val isDeprecated: Boolean

    val isUnsupported: Boolean

    override val description: String
        get() = when {
            !isStable -> "$versionString (experimental)"
            isDeprecated -> "$versionString (deprecated)"
            isUnsupported -> "$versionString (unsupported)"
            else -> versionString
        }
}

// This is a public API used in IDEA kotlin plugin code, in particular in
// community/plugins/kotlin/base/compiler-configuration-ui/src/org/jetbrains/kotlin/idea/base/compilerPreferences/configuration/KotlinCompilerConfigurableTab.java
@Suppress("unused")
@Deprecated(
    message = "This function is no more actual after 2.0 release, consider replacing with isStable",
    replaceWith = ReplaceWith("isStable")
)
fun LanguageVersion.isStableOrReadyForPreview(): Boolean =
    isStable || this == KOTLIN_1_9 || this == KOTLIN_2_0

fun LanguageVersion.toKotlinVersion() = KotlinVersion(major, minor)
fun LanguageVersionSettings.toKotlinVersion() = languageVersion.toKotlinVersion()

interface LanguageVersionSettings {
    fun getFeatureSupport(feature: LanguageFeature): LanguageFeature.State

    fun supportsFeature(feature: LanguageFeature): Boolean =
        getFeatureSupport(feature).let {
            it == LanguageFeature.State.ENABLED ||
                    it == LanguageFeature.State.ENABLED_WITH_WARNING
        }

    fun getManuallyEnabledLanguageFeatures(): List<LanguageFeature>

    fun getManuallyDisabledLanguageFeatures(): List<LanguageFeature>

    fun isPreRelease(): Boolean

    fun <T> getFlag(flag: AnalysisFlag<T>): T

    val apiVersion: ApiVersion

    // Please do not use this to enable/disable specific features/checks. Instead add a new LanguageFeature entry and call supportsFeature
    val languageVersion: LanguageVersion

    companion object {
        const val RESOURCE_NAME_TO_ALLOW_READING_FROM_ENVIRONMENT = "META-INF/allow-configuring-from-environment"
    }
}

class LanguageVersionSettingsImpl @JvmOverloads constructor(
    override val languageVersion: LanguageVersion,
    override val apiVersion: ApiVersion,
    analysisFlags: Map<AnalysisFlag<*>, Any?> = emptyMap(),
    specificFeatures: Map<LanguageFeature, LanguageFeature.State> = emptyMap()
) : LanguageVersionSettings {
    private val analysisFlags: Map<AnalysisFlag<*>, *> = Collections.unmodifiableMap(analysisFlags)
    private val specificFeatures: Map<LanguageFeature, LanguageFeature.State> = Collections.unmodifiableMap(specificFeatures)

    @Suppress("UNCHECKED_CAST")
    override fun <T> getFlag(flag: AnalysisFlag<T>): T = analysisFlags[flag] as T? ?: flag.defaultValue

    override fun getFeatureSupport(feature: LanguageFeature): LanguageFeature.State {
        specificFeatures[feature]?.let { return it }

        if (isEnabledByDefault(feature)) {
            return if (feature.isEnabledWithWarning) LanguageFeature.State.ENABLED_WITH_WARNING else LanguageFeature.State.ENABLED
        }

        return LanguageFeature.State.DISABLED
    }

    override fun getManuallyEnabledLanguageFeatures(): List<LanguageFeature> =
        specificFeatures.filter { isEnabledOnlyByFlag(it.key, it.value) }.keys.toList()

    override fun getManuallyDisabledLanguageFeatures(): List<LanguageFeature> =
        specificFeatures.filter { isDisabledOnlyByFlag(it.key, it.value) }.keys.toList()

    private fun isEnabledOnlyByFlag(feature: LanguageFeature, state: LanguageFeature.State): Boolean =
        !isEnabledByDefault(feature) && (state == LanguageFeature.State.ENABLED || state == LanguageFeature.State.ENABLED_WITH_WARNING)

    private fun isDisabledOnlyByFlag(feature: LanguageFeature, state: LanguageFeature.State): Boolean =
        isEnabledByDefault(feature) && state == LanguageFeature.State.DISABLED

    private fun isEnabledByDefault(feature: LanguageFeature): Boolean =
        feature.sinceVersion != null && languageVersion >= feature.sinceVersion && apiVersion >= feature.sinceApiVersion

    override fun toString() = buildString {
        append("Language = $languageVersion, API = $apiVersion")
        specificFeatures.entries.sortedBy { (feature, _) -> feature.ordinal }.forEach { (feature, state) ->
            val char = when (state) {
                LanguageFeature.State.ENABLED -> '+'
                LanguageFeature.State.ENABLED_WITH_WARNING -> '~'
                LanguageFeature.State.DISABLED -> '-'
            }
            append(" $char$feature")
        }
        analysisFlags.entries.sortedBy { (flag, _) -> flag.toString() }.forEach { (flag, value) ->
            append(" $flag:$value")
        }
    }

    override fun isPreRelease(): Boolean = languageVersion.isPreRelease() ||
            specificFeatures.any { (feature, state) ->
                state == LanguageFeature.State.ENABLED && feature.forcesPreReleaseBinariesIfEnabled()
            }

    companion object {
        @JvmField
        val DEFAULT = LanguageVersionSettingsImpl(LanguageVersion.LATEST_STABLE, ApiVersion.LATEST_STABLE)
    }
}

fun LanguageVersion.isPreRelease(): Boolean {
    if (!isStable) return true

    return KotlinCompilerVersion.isPreRelease() && this == LanguageVersion.LATEST_STABLE
}

fun LanguageFeature.forcesPreReleaseBinariesIfEnabled(): Boolean {
    val isFeatureNotReleasedYet = sinceVersion?.isStable != true
    return isFeatureNotReleasedYet && kind.forcesPreReleaseBinaries
}
