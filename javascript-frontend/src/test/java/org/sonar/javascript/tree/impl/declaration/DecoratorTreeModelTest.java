/*
 * SonarQube JavaScript Plugin
 * Copyright (C) 2011-2021 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.javascript.tree.impl.declaration;

import org.junit.Test;
import org.sonar.javascript.utils.JavaScriptTreeModelTest;
import org.sonar.plugins.javascript.api.tree.Tree.Kind;
import org.sonar.plugins.javascript.api.tree.declaration.DecoratorTree;

import static org.assertj.core.api.Assertions.assertThat;

public class DecoratorTreeModelTest extends JavaScriptTreeModelTest {

  @Test
  public void test() throws Exception {
    DecoratorTree tree = parse("@foo class A {}", Kind.DECORATOR);

    assertThat(tree.is(Kind.DECORATOR)).isTrue();
    assertThat(tree.atToken().text()).isEqualTo("@");
    assertThat(tree.body()).hasSize(1);
    assertThat(tree.body().get(0).name()).isEqualTo("foo");
    assertThat(tree.argumentClause()).isNull();
  }

  @Test
  public void with_arguments() throws Exception {
    DecoratorTree tree = parse("@foo.bar(true) class A {}", Kind.DECORATOR);

    assertThat(tree.is(Kind.DECORATOR)).isTrue();
    assertThat(tree.atToken().text()).isEqualTo("@");
    assertThat(tree.body()).hasSize(2);
    assertThat(tree.body().get(0).name()).isEqualTo("foo");
    assertThat(tree.body().get(1).name()).isEqualTo("bar");
    assertThat(tree.argumentClause().arguments()).hasSize(1);
  }
}
