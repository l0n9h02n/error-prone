/*
 * Copyright 2014 The Error Prone Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.errorprone.scanner;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.errorprone.BugCheckerInfo;
import com.google.errorprone.bugpatterns.AmbiguousMethodReference;
import com.google.errorprone.bugpatterns.ArrayEquals;
import com.google.errorprone.bugpatterns.ArrayFillIncompatibleType;
import com.google.errorprone.bugpatterns.ArrayHashCode;
import com.google.errorprone.bugpatterns.ArrayToString;
import com.google.errorprone.bugpatterns.ArraysAsListPrimitiveArray;
import com.google.errorprone.bugpatterns.AssertFalse;
import com.google.errorprone.bugpatterns.AssertionFailureIgnored;
import com.google.errorprone.bugpatterns.AsyncCallableReturnsNull;
import com.google.errorprone.bugpatterns.AsyncFunctionReturnsNull;
import com.google.errorprone.bugpatterns.BadAnnotationImplementation;
import com.google.errorprone.bugpatterns.BadComparable;
import com.google.errorprone.bugpatterns.BadShiftAmount;
import com.google.errorprone.bugpatterns.BigDecimalLiteralDouble;
import com.google.errorprone.bugpatterns.BooleanParameter;
import com.google.errorprone.bugpatterns.BoxedPrimitiveConstructor;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.CannotMockFinalClass;
import com.google.errorprone.bugpatterns.CanonicalDuration;
import com.google.errorprone.bugpatterns.ChainingConstructorIgnoresParameter;
import com.google.errorprone.bugpatterns.CheckReturnValue;
import com.google.errorprone.bugpatterns.ClassCanBeStatic;
import com.google.errorprone.bugpatterns.ClassName;
import com.google.errorprone.bugpatterns.ClassNewInstance;
import com.google.errorprone.bugpatterns.CollectionToArraySafeParameter;
import com.google.errorprone.bugpatterns.CollectorShouldNotUseState;
import com.google.errorprone.bugpatterns.ComparableAndComparator;
import com.google.errorprone.bugpatterns.ComparableType;
import com.google.errorprone.bugpatterns.ComparisonContractViolated;
import com.google.errorprone.bugpatterns.ComparisonOutOfRange;
import com.google.errorprone.bugpatterns.CompileTimeConstantChecker;
import com.google.errorprone.bugpatterns.ComplexBooleanConstant;
import com.google.errorprone.bugpatterns.ConstantField;
import com.google.errorprone.bugpatterns.ConstantOverflow;
import com.google.errorprone.bugpatterns.ConstructorInvokesOverridable;
import com.google.errorprone.bugpatterns.ConstructorLeaksThis;
import com.google.errorprone.bugpatterns.DateFormatConstant;
import com.google.errorprone.bugpatterns.DeadException;
import com.google.errorprone.bugpatterns.DeadThread;
import com.google.errorprone.bugpatterns.DefaultCharset;
import com.google.errorprone.bugpatterns.DepAnn;
import com.google.errorprone.bugpatterns.DivZero;
import com.google.errorprone.bugpatterns.DoNotCallChecker;
import com.google.errorprone.bugpatterns.EmptyIfStatement;
import com.google.errorprone.bugpatterns.EmptyTopLevelDeclaration;
import com.google.errorprone.bugpatterns.EqualsHashCode;
import com.google.errorprone.bugpatterns.EqualsIncompatibleType;
import com.google.errorprone.bugpatterns.EqualsNaN;
import com.google.errorprone.bugpatterns.EqualsReference;
import com.google.errorprone.bugpatterns.ExpectedExceptionChecker;
import com.google.errorprone.bugpatterns.FallThrough;
import com.google.errorprone.bugpatterns.Finally;
import com.google.errorprone.bugpatterns.FloatCast;
import com.google.errorprone.bugpatterns.FloatingPointLiteralPrecision;
import com.google.errorprone.bugpatterns.ForOverrideChecker;
import com.google.errorprone.bugpatterns.FunctionalInterfaceClash;
import com.google.errorprone.bugpatterns.FunctionalInterfaceMethodChanged;
import com.google.errorprone.bugpatterns.FutureReturnValueIgnored;
import com.google.errorprone.bugpatterns.FuturesGetCheckedIllegalExceptionType;
import com.google.errorprone.bugpatterns.FuzzyEqualsShouldNotBeUsedInEqualsMethod;
import com.google.errorprone.bugpatterns.GetClassOnAnnotation;
import com.google.errorprone.bugpatterns.GetClassOnClass;
import com.google.errorprone.bugpatterns.GetClassOnEnum;
import com.google.errorprone.bugpatterns.HashtableContains;
import com.google.errorprone.bugpatterns.HidingField;
import com.google.errorprone.bugpatterns.IdentityBinaryExpression;
import com.google.errorprone.bugpatterns.ImmutableModification;
import com.google.errorprone.bugpatterns.IncompatibleModifiersChecker;
import com.google.errorprone.bugpatterns.IncrementInForLoopAndHeader;
import com.google.errorprone.bugpatterns.IndexOfChar;
import com.google.errorprone.bugpatterns.InexactVarargsConditional;
import com.google.errorprone.bugpatterns.InfiniteRecursion;
import com.google.errorprone.bugpatterns.InputStreamSlowMultibyteRead;
import com.google.errorprone.bugpatterns.InsecureCipherMode;
import com.google.errorprone.bugpatterns.InstanceOfAndCastMatchWrongType;
import com.google.errorprone.bugpatterns.IntLongMath;
import com.google.errorprone.bugpatterns.InvalidPatternSyntax;
import com.google.errorprone.bugpatterns.InvalidTimeZoneID;
import com.google.errorprone.bugpatterns.IsInstanceOfClass;
import com.google.errorprone.bugpatterns.IterableAndIterator;
import com.google.errorprone.bugpatterns.IterablePathParameter;
import com.google.errorprone.bugpatterns.JMockTestWithoutRunWithOrRuleAnnotation;
import com.google.errorprone.bugpatterns.JUnit3FloatingPointComparisonWithoutDelta;
import com.google.errorprone.bugpatterns.JUnit3TestNotRun;
import com.google.errorprone.bugpatterns.JUnit4ClassAnnotationNonStatic;
import com.google.errorprone.bugpatterns.JUnit4ClassUsedInJUnit3;
import com.google.errorprone.bugpatterns.JUnit4SetUpNotRun;
import com.google.errorprone.bugpatterns.JUnit4TearDownNotRun;
import com.google.errorprone.bugpatterns.JUnit4TestNotRun;
import com.google.errorprone.bugpatterns.JUnitAmbiguousTestClass;
import com.google.errorprone.bugpatterns.JUnitAssertSameCheck;
import com.google.errorprone.bugpatterns.JavaLangClash;
import com.google.errorprone.bugpatterns.JdkObsolete;
import com.google.errorprone.bugpatterns.LiteByteStringUtf8;
import com.google.errorprone.bugpatterns.LogicalAssignment;
import com.google.errorprone.bugpatterns.LongLiteralLowerCaseSuffix;
import com.google.errorprone.bugpatterns.LoopConditionChecker;
import com.google.errorprone.bugpatterns.MethodCanBeStatic;
import com.google.errorprone.bugpatterns.MissingCasesInEnumSwitch;
import com.google.errorprone.bugpatterns.MissingDefault;
import com.google.errorprone.bugpatterns.MissingFail;
import com.google.errorprone.bugpatterns.MissingOverride;
import com.google.errorprone.bugpatterns.MissingSuperCall;
import com.google.errorprone.bugpatterns.MisusedWeekYear;
import com.google.errorprone.bugpatterns.MixedArrayDimensions;
import com.google.errorprone.bugpatterns.MockitoCast;
import com.google.errorprone.bugpatterns.MockitoUsage;
import com.google.errorprone.bugpatterns.ModifyCollectionInEnhancedForLoop;
import com.google.errorprone.bugpatterns.ModifyingCollectionWithItself;
import com.google.errorprone.bugpatterns.MultiVariableDeclaration;
import com.google.errorprone.bugpatterns.MultipleParallelOrSequentialCalls;
import com.google.errorprone.bugpatterns.MultipleTopLevelClasses;
import com.google.errorprone.bugpatterns.MultipleUnaryOperatorsInMethodCall;
import com.google.errorprone.bugpatterns.MustBeClosedChecker;
import com.google.errorprone.bugpatterns.MutableConstantField;
import com.google.errorprone.bugpatterns.MutableMethodReturnType;
import com.google.errorprone.bugpatterns.NCopiesOfChar;
import com.google.errorprone.bugpatterns.NarrowingCompoundAssignment;
import com.google.errorprone.bugpatterns.NestedInstanceOfConditions;
import com.google.errorprone.bugpatterns.NoAllocationChecker;
import com.google.errorprone.bugpatterns.NonAtomicVolatileUpdate;
import com.google.errorprone.bugpatterns.NonCanonicalStaticImport;
import com.google.errorprone.bugpatterns.NonCanonicalStaticMemberImport;
import com.google.errorprone.bugpatterns.NonFinalCompileTimeConstant;
import com.google.errorprone.bugpatterns.NonOverridingEquals;
import com.google.errorprone.bugpatterns.NonRuntimeAnnotation;
import com.google.errorprone.bugpatterns.NullTernary;
import com.google.errorprone.bugpatterns.NullableConstructor;
import com.google.errorprone.bugpatterns.NullablePrimitive;
import com.google.errorprone.bugpatterns.NullableVoid;
import com.google.errorprone.bugpatterns.NumericEquality;
import com.google.errorprone.bugpatterns.OperatorPrecedence;
import com.google.errorprone.bugpatterns.OptionalEquality;
import com.google.errorprone.bugpatterns.OptionalNotPresent;
import com.google.errorprone.bugpatterns.OverrideThrowableToString;
import com.google.errorprone.bugpatterns.Overrides;
import com.google.errorprone.bugpatterns.PackageInfo;
import com.google.errorprone.bugpatterns.PackageLocation;
import com.google.errorprone.bugpatterns.ParameterComment;
import com.google.errorprone.bugpatterns.ParameterName;
import com.google.errorprone.bugpatterns.PreconditionsCheckNotNull;
import com.google.errorprone.bugpatterns.PreconditionsCheckNotNullPrimitive;
import com.google.errorprone.bugpatterns.PreconditionsInvalidPlaceholder;
import com.google.errorprone.bugpatterns.PredicateIncompatibleType;
import com.google.errorprone.bugpatterns.PrimitiveArrayPassedToVarargsMethod;
import com.google.errorprone.bugpatterns.PrivateConstructorForUtilityClass;
import com.google.errorprone.bugpatterns.PrivateSecurityContractProtoAccess;
import com.google.errorprone.bugpatterns.ProtoFieldNullComparison;
import com.google.errorprone.bugpatterns.ProtoFieldPreconditionsCheckNotNull;
import com.google.errorprone.bugpatterns.ProtoStringFieldReferenceEquality;
import com.google.errorprone.bugpatterns.ProtocolBufferOrdinal;
import com.google.errorprone.bugpatterns.ProvidesFixChecker;
import com.google.errorprone.bugpatterns.RandomCast;
import com.google.errorprone.bugpatterns.RandomModInteger;
import com.google.errorprone.bugpatterns.ReachabilityFenceUsage;
import com.google.errorprone.bugpatterns.RedundantThrows;
import com.google.errorprone.bugpatterns.ReferenceEquality;
import com.google.errorprone.bugpatterns.RemoveUnusedImports;
import com.google.errorprone.bugpatterns.RequiredModifiersChecker;
import com.google.errorprone.bugpatterns.RestrictedApiChecker;
import com.google.errorprone.bugpatterns.ReturnValueIgnored;
import com.google.errorprone.bugpatterns.SelfAssignment;
import com.google.errorprone.bugpatterns.SelfComparison;
import com.google.errorprone.bugpatterns.SelfEquals;
import com.google.errorprone.bugpatterns.ShortCircuitBoolean;
import com.google.errorprone.bugpatterns.ShouldHaveEvenArgs;
import com.google.errorprone.bugpatterns.SizeGreaterThanOrEqualsZero;
import com.google.errorprone.bugpatterns.StaticQualifiedUsingExpression;
import com.google.errorprone.bugpatterns.StreamResourceLeak;
import com.google.errorprone.bugpatterns.StreamToString;
import com.google.errorprone.bugpatterns.StringBuilderInitWithChar;
import com.google.errorprone.bugpatterns.StringEquality;
import com.google.errorprone.bugpatterns.StringSplit;
import com.google.errorprone.bugpatterns.StringSplitter;
import com.google.errorprone.bugpatterns.SuppressWarningsDeprecated;
import com.google.errorprone.bugpatterns.SwitchDefault;
import com.google.errorprone.bugpatterns.TestExceptionChecker;
import com.google.errorprone.bugpatterns.ThreadJoinLoop;
import com.google.errorprone.bugpatterns.ThreadLocalUsage;
import com.google.errorprone.bugpatterns.ThreeLetterTimeZoneID;
import com.google.errorprone.bugpatterns.ThrowIfUncheckedKnownChecked;
import com.google.errorprone.bugpatterns.ThrowNull;
import com.google.errorprone.bugpatterns.ThrowsUncheckedException;
import com.google.errorprone.bugpatterns.TruthConstantAsserts;
import com.google.errorprone.bugpatterns.TruthSelfEquals;
import com.google.errorprone.bugpatterns.TryFailThrowable;
import com.google.errorprone.bugpatterns.TypeParameterNaming;
import com.google.errorprone.bugpatterns.TypeParameterQualifier;
import com.google.errorprone.bugpatterns.TypeParameterShadowing;
import com.google.errorprone.bugpatterns.TypeParameterUnusedInFormals;
import com.google.errorprone.bugpatterns.URLEqualsHashCode;
import com.google.errorprone.bugpatterns.UngroupedOverloads;
import com.google.errorprone.bugpatterns.UnnecessaryDefaultInEnumSwitch;
import com.google.errorprone.bugpatterns.UnnecessarySetDefault;
import com.google.errorprone.bugpatterns.UnnecessaryStaticImport;
import com.google.errorprone.bugpatterns.UnnecessaryTypeArgument;
import com.google.errorprone.bugpatterns.UnsafeFinalization;
import com.google.errorprone.bugpatterns.UnsynchronizedOverridesSynchronized;
import com.google.errorprone.bugpatterns.UnusedAnonymousClass;
import com.google.errorprone.bugpatterns.UnusedCollectionModifiedInPlace;
import com.google.errorprone.bugpatterns.UseCorrectAssertInTests;
import com.google.errorprone.bugpatterns.VarChecker;
import com.google.errorprone.bugpatterns.WaitNotInLoop;
import com.google.errorprone.bugpatterns.WildcardImport;
import com.google.errorprone.bugpatterns.WrongParameterPackage;
import com.google.errorprone.bugpatterns.android.BinderIdentityRestoredDangerously;
import com.google.errorprone.bugpatterns.android.BundleDeserializationCast;
import com.google.errorprone.bugpatterns.android.FragmentInjection;
import com.google.errorprone.bugpatterns.android.FragmentNotInstantiable;
import com.google.errorprone.bugpatterns.android.HardCodedSdCardPath;
import com.google.errorprone.bugpatterns.android.IsLoggableTagLength;
import com.google.errorprone.bugpatterns.android.MislabeledAndroidString;
import com.google.errorprone.bugpatterns.android.RectIntersectReturnValueIgnored;
import com.google.errorprone.bugpatterns.android.RestrictToEnforcer;
import com.google.errorprone.bugpatterns.android.StaticOrDefaultInterfaceMethod;
import com.google.errorprone.bugpatterns.android.WakelockReleasedDangerously;
import com.google.errorprone.bugpatterns.argumentselectiondefects.ArgumentSelectionDefectChecker;
import com.google.errorprone.bugpatterns.argumentselectiondefects.AssertEqualsArgumentOrderChecker;
import com.google.errorprone.bugpatterns.argumentselectiondefects.AutoValueConstructorOrderChecker;
import com.google.errorprone.bugpatterns.argumentselectiondefects.NamedParameterChecker;
import com.google.errorprone.bugpatterns.collectionincompatibletype.CollectionIncompatibleType;
import com.google.errorprone.bugpatterns.collectionincompatibletype.CompatibleWithMisuse;
import com.google.errorprone.bugpatterns.collectionincompatibletype.IncompatibleArgumentType;
import com.google.errorprone.bugpatterns.formatstring.FormatString;
import com.google.errorprone.bugpatterns.formatstring.FormatStringAnnotationChecker;
import com.google.errorprone.bugpatterns.inject.AssistedInjectAndInjectOnConstructors;
import com.google.errorprone.bugpatterns.inject.AssistedInjectAndInjectOnSameConstructor;
import com.google.errorprone.bugpatterns.inject.AutoFactoryAtInject;
import com.google.errorprone.bugpatterns.inject.InjectOnConstructorOfAbstractClass;
import com.google.errorprone.bugpatterns.inject.InjectedConstructorAnnotations;
import com.google.errorprone.bugpatterns.inject.InvalidTargetingOnScopingAnnotation;
import com.google.errorprone.bugpatterns.inject.JavaxInjectOnAbstractMethod;
import com.google.errorprone.bugpatterns.inject.JavaxInjectOnFinalField;
import com.google.errorprone.bugpatterns.inject.MoreThanOneInjectableConstructor;
import com.google.errorprone.bugpatterns.inject.MoreThanOneQualifier;
import com.google.errorprone.bugpatterns.inject.MoreThanOneScopeAnnotationOnClass;
import com.google.errorprone.bugpatterns.inject.OverlappingQualifierAndScopeAnnotation;
import com.google.errorprone.bugpatterns.inject.QualifierOrScopeOnInjectMethod;
import com.google.errorprone.bugpatterns.inject.QualifierWithTypeUse;
import com.google.errorprone.bugpatterns.inject.ScopeAnnotationOnInterfaceOrAbstractClass;
import com.google.errorprone.bugpatterns.inject.ScopeOrQualifierAnnotationRetention;
import com.google.errorprone.bugpatterns.inject.dagger.AndroidInjectionBeforeSuper;
import com.google.errorprone.bugpatterns.inject.dagger.EmptySetMultibindingContributions;
import com.google.errorprone.bugpatterns.inject.dagger.PrivateConstructorForNoninstantiableModule;
import com.google.errorprone.bugpatterns.inject.dagger.ProvidesNull;
import com.google.errorprone.bugpatterns.inject.dagger.UseBinds;
import com.google.errorprone.bugpatterns.inject.guice.AssistedInjectScoping;
import com.google.errorprone.bugpatterns.inject.guice.AssistedParameters;
import com.google.errorprone.bugpatterns.inject.guice.BindingToUnqualifiedCommonType;
import com.google.errorprone.bugpatterns.inject.guice.InjectOnFinalField;
import com.google.errorprone.bugpatterns.inject.guice.OverridesGuiceInjectableMethod;
import com.google.errorprone.bugpatterns.inject.guice.OverridesJavaxInjectableMethod;
import com.google.errorprone.bugpatterns.inject.guice.ProvidesMethodOutsideOfModule;
import com.google.errorprone.bugpatterns.nullness.FieldMissingNullable;
import com.google.errorprone.bugpatterns.nullness.ParameterNotNullable;
import com.google.errorprone.bugpatterns.nullness.ReturnMissingNullable;
import com.google.errorprone.bugpatterns.overloading.InconsistentOverloads;
import com.google.errorprone.bugpatterns.threadsafety.DoubleCheckedLocking;
import com.google.errorprone.bugpatterns.threadsafety.GuardedByChecker;
import com.google.errorprone.bugpatterns.threadsafety.ImmutableAnnotationChecker;
import com.google.errorprone.bugpatterns.threadsafety.ImmutableChecker;
import com.google.errorprone.bugpatterns.threadsafety.ImmutableEnumChecker;
import com.google.errorprone.bugpatterns.threadsafety.LockMethodChecker;
import com.google.errorprone.bugpatterns.threadsafety.StaticGuardedByInstance;
import com.google.errorprone.bugpatterns.threadsafety.SynchronizeOnNonFinalField;
import com.google.errorprone.bugpatterns.threadsafety.UnlockMethodChecker;
import java.util.Arrays;

/**
 * Static helper class that provides {@link ScannerSupplier}s and {@link BugChecker}s for the
 * built-in Error Prone checks, as opposed to plugin checks or checks used in tests.
 */
public class BuiltInCheckerSuppliers {
  @SafeVarargs
  public static ImmutableSet<BugCheckerInfo> getSuppliers(Class<? extends BugChecker>... checkers) {
    return getSuppliers(Arrays.asList(checkers));
  }

  public static ImmutableSet<BugCheckerInfo> getSuppliers(
      Iterable<Class<? extends BugChecker>> checkers) {
    ImmutableSet.Builder<BugCheckerInfo> result = ImmutableSet.builder();
    for (Class<? extends BugChecker> checker : checkers) {
      result.add(BugCheckerInfo.create(checker));
    }
    return result.build();
  }

  /** Returns a {@link ScannerSupplier} with all {@link BugChecker}s in Error Prone. */
  public static ScannerSupplier allChecks() {
    return ScannerSupplier.fromBugCheckerInfos(
        Iterables.concat(ENABLED_ERRORS, ENABLED_WARNINGS, DISABLED_CHECKS));
  }

  /**
   * Returns a {@link ScannerSupplier} with the {@link BugChecker}s that are in the ENABLED lists.
   */
  public static ScannerSupplier defaultChecks() {
    return allChecks()
        .filter(Predicates.or(Predicates.in(ENABLED_ERRORS), Predicates.in(ENABLED_WARNINGS)));
  }

  /**
   * Returns a {@link ScannerSupplier} with the {@link BugChecker}s that are in the ENABLED_ERRORS
   * list.
   */
  public static ScannerSupplier errorChecks() {
    return allChecks().filter(Predicates.in(ENABLED_ERRORS));
  }

  /** A list of all checks with severity ERROR that are on by default. */
  public static final ImmutableSet<BugCheckerInfo> ENABLED_ERRORS =
      getSuppliers(
          AndroidInjectionBeforeSuper.class,
          ArrayEquals.class,
          ArrayFillIncompatibleType.class,
          ArrayHashCode.class,
          ArrayToString.class,
          ArraysAsListPrimitiveArray.class,
          AutoValueConstructorOrderChecker.class,
          AssistedInjectScoping.class,
          AssistedParameters.class,
          AsyncCallableReturnsNull.class,
          AsyncFunctionReturnsNull.class,
          BadShiftAmount.class,
          BundleDeserializationCast.class,
          ChainingConstructorIgnoresParameter.class,
          CheckReturnValue.class,
          CollectionIncompatibleType.class,
          ComparableType.class,
          CompatibleWithMisuse.class,
          ComparisonOutOfRange.class,
          CompileTimeConstantChecker.class,
          ComplexBooleanConstant.class,
          ConstantOverflow.class,
          DeadException.class,
          DeadThread.class,
          DoNotCallChecker.class,
          EqualsNaN.class,
          EqualsReference.class,
          ForOverrideChecker.class,
          FormatString.class,
          FormatStringAnnotationChecker.class,
          FunctionalInterfaceMethodChanged.class,
          FuturesGetCheckedIllegalExceptionType.class,
          GetClassOnAnnotation.class,
          GetClassOnClass.class,
          GuardedByChecker.class,
          HashtableContains.class,
          IdentityBinaryExpression.class,
          ImmutableChecker.class,
          ImmutableModification.class,
          IncompatibleArgumentType.class,
          IndexOfChar.class,
          InexactVarargsConditional.class,
          InfiniteRecursion.class,
          InjectOnFinalField.class,
          InvalidPatternSyntax.class,
          InvalidTimeZoneID.class,
          IsInstanceOfClass.class,
          IsLoggableTagLength.class,
          JavaxInjectOnAbstractMethod.class,
          JUnit3TestNotRun.class,
          JUnit4ClassAnnotationNonStatic.class,
          JUnit4SetUpNotRun.class,
          JUnit4TearDownNotRun.class,
          JUnit4TestNotRun.class,
          JUnitAssertSameCheck.class,
          LiteByteStringUtf8.class,
          LoopConditionChecker.class,
          MislabeledAndroidString.class,
          MissingSuperCall.class,
          MisusedWeekYear.class,
          MockitoCast.class,
          MockitoUsage.class,
          ModifyingCollectionWithItself.class,
          MoreThanOneInjectableConstructor.class,
          MoreThanOneScopeAnnotationOnClass.class,
          MustBeClosedChecker.class,
          NCopiesOfChar.class,
          NonCanonicalStaticImport.class,
          NonFinalCompileTimeConstant.class,
          NonRuntimeAnnotation.class,
          NullTernary.class,
          OptionalEquality.class,
          OverlappingQualifierAndScopeAnnotation.class,
          OverridesJavaxInjectableMethod.class,
          PackageInfo.class,
          PreconditionsCheckNotNull.class,
          PreconditionsCheckNotNullPrimitive.class,
          PredicateIncompatibleType.class,
          PrivateSecurityContractProtoAccess.class,
          ProtocolBufferOrdinal.class,
          ProtoFieldNullComparison.class,
          ProvidesMethodOutsideOfModule.class,
          ProvidesNull.class,
          RandomModInteger.class,
          RandomCast.class,
          RectIntersectReturnValueIgnored.class,
          RestrictedApiChecker.class,
          ReturnValueIgnored.class,
          SelfAssignment.class,
          SelfComparison.class,
          SelfEquals.class,
          SizeGreaterThanOrEqualsZero.class,
          ShouldHaveEvenArgs.class,
          StreamResourceLeak.class,
          StreamToString.class,
          StringBuilderInitWithChar.class,
          SuppressWarningsDeprecated.class,
          ThrowIfUncheckedKnownChecked.class,
          ThrowNull.class,
          TruthSelfEquals.class,
          TryFailThrowable.class,
          TypeParameterQualifier.class,
          UnnecessaryTypeArgument.class,
          UnusedAnonymousClass.class,
          UnusedCollectionModifiedInPlace.class);

  /** A list of all checks with severity WARNING that are on by default. */
  public static final ImmutableSet<BugCheckerInfo> ENABLED_WARNINGS =
      getSuppliers(
          AmbiguousMethodReference.class,
          ArgumentSelectionDefectChecker.class,
          AssertEqualsArgumentOrderChecker.class,
          AssertionFailureIgnored.class,
          BadAnnotationImplementation.class,
          BadComparable.class,
          BoxedPrimitiveConstructor.class,
          CannotMockFinalClass.class,
          CanonicalDuration.class,
          ClassCanBeStatic.class,
          ClassNewInstance.class,
          CollectionToArraySafeParameter.class,
          CollectorShouldNotUseState.class,
          ComparableAndComparator.class,
          DefaultCharset.class,
          DoubleCheckedLocking.class,
          EqualsHashCode.class,
          EqualsIncompatibleType.class,
          FallThrough.class,
          Finally.class,
          FloatCast.class,
          FloatingPointLiteralPrecision.class,
          FragmentInjection.class,
          FragmentNotInstantiable.class,
          FutureReturnValueIgnored.class,
          GetClassOnEnum.class,
          HidingField.class,
          ImmutableAnnotationChecker.class,
          ImmutableEnumChecker.class,
          IncompatibleModifiersChecker.class,
          IncrementInForLoopAndHeader.class,
          InjectOnConstructorOfAbstractClass.class,
          InputStreamSlowMultibyteRead.class,
          InstanceOfAndCastMatchWrongType.class,
          IntLongMath.class,
          IterableAndIterator.class,
          JavaLangClash.class,
          JdkObsolete.class,
          JUnit3FloatingPointComparisonWithoutDelta.class,
          JUnit4ClassUsedInJUnit3.class,
          JUnitAmbiguousTestClass.class,
          LogicalAssignment.class,
          MissingCasesInEnumSwitch.class,
          MissingFail.class,
          MissingOverride.class,
          ModifyCollectionInEnhancedForLoop.class,
          MultipleParallelOrSequentialCalls.class,
          MutableConstantField.class,
          NamedParameterChecker.class,
          NarrowingCompoundAssignment.class,
          NestedInstanceOfConditions.class,
          NonAtomicVolatileUpdate.class,
          NonOverridingEquals.class,
          NullableConstructor.class,
          NullablePrimitive.class,
          NullableVoid.class,
          OperatorPrecedence.class,
          OptionalNotPresent.class,
          Overrides.class,
          OverridesGuiceInjectableMethod.class,
          OverrideThrowableToString.class,
          ParameterName.class,
          PreconditionsInvalidPlaceholder.class,
          ProtoFieldPreconditionsCheckNotNull.class,
          ReachabilityFenceUsage.class,
          ReferenceEquality.class,
          RequiredModifiersChecker.class,
          ShortCircuitBoolean.class,
          StringSplitter.class,
          DateFormatConstant.class,
          StaticGuardedByInstance.class,
          SynchronizeOnNonFinalField.class,
          ThreadJoinLoop.class,
          ThreadLocalUsage.class,
          ThreeLetterTimeZoneID.class,
          TruthConstantAsserts.class,
          TypeParameterShadowing.class,
          TypeParameterUnusedInFormals.class,
          UnsafeFinalization.class,
          UnsynchronizedOverridesSynchronized.class,
          URLEqualsHashCode.class,
          UseCorrectAssertInTests.class,
          WaitNotInLoop.class,
          WakelockReleasedDangerously.class);

  /** A list of all checks that are off by default. */
  public static final ImmutableSet<BugCheckerInfo> DISABLED_CHECKS =
      getSuppliers(
          AutoFactoryAtInject.class,
          AssertFalse.class,
          AssistedInjectAndInjectOnConstructors.class,
          AssistedInjectAndInjectOnSameConstructor.class,
          BigDecimalLiteralDouble.class,
          BinderIdentityRestoredDangerously.class, // TODO: enable this by default.
          BindingToUnqualifiedCommonType.class,
          ClassName.class,
          ComparisonContractViolated.class,
          ConstantField.class,
          BooleanParameter.class,
          ConstructorInvokesOverridable.class,
          ConstructorLeaksThis.class,
          DepAnn.class,
          DivZero.class,
          EmptyIfStatement.class,
          EmptySetMultibindingContributions.class,
          EmptyTopLevelDeclaration.class,
          ExpectedExceptionChecker.class,
          FieldMissingNullable.class,
          FunctionalInterfaceClash.class,
          FuzzyEqualsShouldNotBeUsedInEqualsMethod.class,
          HardCodedSdCardPath.class,
          InconsistentOverloads.class,
          InjectedConstructorAnnotations.class,
          InsecureCipherMode.class,
          InvalidTargetingOnScopingAnnotation.class,
          IterablePathParameter.class,
          JMockTestWithoutRunWithOrRuleAnnotation.class,
          JavaxInjectOnFinalField.class,
          LockMethodChecker.class,
          LongLiteralLowerCaseSuffix.class,
          MethodCanBeStatic.class,
          MissingDefault.class,
          MixedArrayDimensions.class,
          MoreThanOneQualifier.class,
          MutableMethodReturnType.class,
          MultiVariableDeclaration.class,
          MultipleTopLevelClasses.class,
          MultipleUnaryOperatorsInMethodCall.class,
          NoAllocationChecker.class,
          NonCanonicalStaticMemberImport.class,
          NumericEquality.class,
          PackageLocation.class,
          ParameterComment.class,
          ParameterNotNullable.class,
          PrimitiveArrayPassedToVarargsMethod.class,
          PrivateConstructorForUtilityClass.class,
          PrivateConstructorForNoninstantiableModule.class,
          ProtoStringFieldReferenceEquality.class,
          ProvidesFixChecker.class,
          QualifierOrScopeOnInjectMethod.class,
          QualifierWithTypeUse.class,
          RedundantThrows.class,
          RemoveUnusedImports.class,
          RestrictToEnforcer.class,
          ReturnMissingNullable.class,
          ScopeAnnotationOnInterfaceOrAbstractClass.class,
          ScopeOrQualifierAnnotationRetention.class,
          StaticQualifiedUsingExpression.class,
          StaticOrDefaultInterfaceMethod.class,
          StringEquality.class,
          StringSplit.class,
          SwitchDefault.class,
          TestExceptionChecker.class,
          ThrowsUncheckedException.class,
          TypeParameterNaming.class,
          UngroupedOverloads.class,
          UnlockMethodChecker.class,
          UnnecessaryDefaultInEnumSwitch.class,
          UnnecessarySetDefault.class,
          UnnecessaryStaticImport.class,
          UseBinds.class,
          VarChecker.class,
          WildcardImport.class,
          WrongParameterPackage.class);

  // May not be instantiated
  private BuiltInCheckerSuppliers() {}
}
