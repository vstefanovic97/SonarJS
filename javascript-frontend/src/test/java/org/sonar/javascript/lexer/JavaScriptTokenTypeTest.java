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
package org.sonar.javascript.lexer;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JavaScriptTokenTypeTest {

  @Test
  public void test() {
    assertThat(JavaScriptTokenType.values().length).isEqualTo(4);

    for (JavaScriptTokenType type : JavaScriptTokenType.values()) {
      assertThat(type.getName()).isEqualTo(type.name());
      assertThat(type.getValue()).isEqualTo(type.name());
    }
  }

  @Test(expected = IllegalStateException.class)
  public void hasToBeSkippedFromAst() throws Exception {
    JavaScriptTokenType.IDENTIFIER.hasToBeSkippedFromAst(null);
  }

}
