---
title: SelfAssignment
summary: "Variable assigned to itself"
layout: bugpattern
category: JDK
severity: ERROR
maturity: MATURE
---

<!--
*** AUTO-GENERATED, DO NOT MODIFY ***
To make changes, edit the @BugPattern annotation or the explanation in docs/bugpattern.
-->

## The problem
The left-hand side and right-hand side of this assignment are the same. It has no effect.

This also handles assignments in which the right-hand side is a call to Preconditions.checkNotNull(), which returns the variable that was checked for non-nullity.  If you just intended to check that the variable is non-null, please don't assign the result to the checked variable; just call Preconditions.checkNotNull() as a bare statement.

## Suppression
Suppress false positives by adding an `@SuppressWarnings("SelfAssignment")` annotation to the enclosing element.

----------

## Examples
__SelfAssignmentNegativeCases.java__

{% highlight java %}
/*
 * Copyright 2011 Google Inc. All Rights Reserved.
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

package com.google.errorprone.bugpatterns;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Tests for self assignment
 *
 * @author eaftan@google.com (Eddie Aftandilian)
 */
public class SelfAssignmentNegativeCases {
  private int a;
  
  private static int b = StaticClass.b;
  private static final int C = SelfAssignmentNegativeCases.b;
  private static final int D = checkNotNull(SelfAssignmentNegativeCases.C);
  private static final int E = StaticClass.getIntArr().length;
  
  public void test1(int a) {
    int b = SelfAssignmentNegativeCases.b;
    this.a = a;
    this.a = checkNotNull(a);
  }
  
  public void test2() {
    int a = 0;
    int b = a;
    a = b;
  }
  
  public void test3() {
    int a = 10;
  }
  
  public void test4() {
    int i = 1;
    i += i;
  }
  
  public void test5(SelfAssignmentNegativeCases n) {
    a = n.a;
  }
  
  public void test6() {
    Foo foo = new Foo();
    Bar bar = new Bar();
    foo.a = bar.a;
    foo.a = checkNotNull(bar.a);
  }
  
  public void test7() {
    Foobar f1 = new Foobar();
    f1.foo = new Foo();
    f1.bar = new Bar();
    f1.foo.a = f1.bar.a;
    f1.foo.a = checkNotNull(f1.bar.a);
  }
  
  public void test8(SelfAssignmentNegativeCases that) {
    this.a = that.a;
    this.a = checkNotNull(that.a);
  }
  
  private static class Foo {
    int a;
  }
  
  private static class Bar {
    int a;
  }
  
  private static class Foobar {
    Foo foo;
    Bar bar;
  }
  
  private static class StaticClass {
    static int b;
    public static int[] getIntArr() {
      return new int[10];
    }
  }
  
}
{% endhighlight %}

__SelfAssignmentPositiveCases1.java__

{% highlight java %}
/*
 * Copyright 2011 Google Inc. All Rights Reserved.
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

package com.google.errorprone.bugpatterns;

/**
 * Tests for self assignment
 * 
 * @author eaftan@google.com (Eddie Aftandilian)
 */
public class SelfAssignmentPositiveCases1 {
  private int a;
  
  public void test1(int b) {
    // BUG: Diagnostic contains: this.a = b
    this.a = a;
  } 
  
  public void test2(int b) {
    // BUG: Diagnostic contains: remove this line
    a = this.a;
  }
  
  public void test3() {
    int a = 0;
    // BUG: Diagnostic contains: this.a = a
    a = a;
  }
  
  public void test4() {
    // BUG: Diagnostic contains: remove this line
    this.a = this.a;
  }

  public void test5() {
    // BUG: Diagnostic contains: this.a = a
    if ((a = a) != 10) {
      System.out.println("foo");
    }
  }

  // Check that WrappedTreeMap handles folded strings; tested by EndPosTest.
  // See https://code.google.com/p/error-prone/issues/detail?id=209
  public String foldableString() {
    return "foo" + "bar";
  }
}
{% endhighlight %}

__SelfAssignmentPositiveCases2.java__

{% highlight java %}
/*
 * Copyright 2011 Google Inc. All Rights Reserved.
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

package com.google.errorprone.bugpatterns;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Tests for self assignment
 * 
 * @author eaftan@google.com (Eddie Aftandilian)
 */
public class SelfAssignmentPositiveCases2 {
  private int a;
  private Foo foo;
  
  // BUG: Diagnostic contains: private static final Object obj
  private static final Object obj = SelfAssignmentPositiveCases2.obj;
  // BUG: Diagnostic contains: private static final Object obj2
  private static final Object obj2 = checkNotNull(SelfAssignmentPositiveCases2.obj2);
    
  public void test6() {
    Foo foo = new Foo();
    foo.a = 2;
    // BUG: Diagnostic contains: remove this line
    foo.a = foo.a;
    // BUG: Diagnostic contains: checkNotNull(foo.a)
    foo.a = checkNotNull(foo.a);
  }
  
  public void test7() {
    Foobar f = new Foobar();
    f.foo = new Foo();
    f.foo.a = 10;
    // BUG: Diagnostic contains: remove this line
    f.foo.a = f.foo.a;
    // BUG: Diagnostic contains: checkNotNull(f.foo.a)
    f.foo.a = checkNotNull(f.foo.a);
  }
  
  public void test8() {
    foo = new Foo();
    // BUG: Diagnostic contains: remove this line
    this.foo.a = foo.a;
    // BUG: Diagnostic contains: checkNotNull(foo.a)
    this.foo.a = checkNotNull(foo.a);
  }
  
  public void test9(Foo fao, Foo bar) {
    // BUG: Diagnostic contains: this.foo = fao
    this.foo = foo;
    // BUG: Diagnostic contains: this.foo = checkNotNull(fao)
    this.foo = checkNotNull(foo);
  }
  
  public void test10(Foo foo) {
    // BUG: Diagnostic contains: this.foo = foo
    foo = foo;
    // BUG: Diagnostic contains: this.foo = checkNotNull(foo)
    foo = checkNotNull(foo);
  }
     
  private static class Foo {
    int a;
  }
  
  private static class Bar {
    int a;
  }
  
  private static class Foobar {
    Foo foo;
    Bar bar;
  }
}
{% endhighlight %}
