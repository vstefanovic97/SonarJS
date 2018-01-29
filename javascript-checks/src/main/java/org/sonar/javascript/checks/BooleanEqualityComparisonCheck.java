/*
 * SonarQube JavaScript Plugin
 * Copyright (C) 2011-2018 SonarSource SA
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
package org.sonar.javascript.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.javascript.api.tree.Tree.Kind;
import org.sonar.plugins.javascript.api.tree.expression.BinaryExpressionTree;
import org.sonar.plugins.javascript.api.tree.expression.ExpressionTree;
import org.sonar.plugins.javascript.api.tree.expression.ParenthesisedExpressionTree;
import org.sonar.plugins.javascript.api.tree.expression.UnaryExpressionTree;
import org.sonar.plugins.javascript.api.visitors.DoubleDispatchVisitorCheck;

@Rule(key = "S1125")
public class BooleanEqualityComparisonCheck extends DoubleDispatchVisitorCheck {

  private static final String MESSAGE_REFACTOR = "Refactor the code to avoid using this boolean literal.";
  private static final String MESSAGE_SIMPLIFY = "Simplify this unnecessary boolean operation.";

  @Override
  public void visitUnaryExpression(UnaryExpressionTree tree) {
    if (tree.is(Kind.LOGICAL_COMPLEMENT)) {
      visitExpression(tree.expression(), MESSAGE_SIMPLIFY);
    }

    super.visitUnaryExpression(tree);
  }


  @Override
  public void visitBinaryExpression(BinaryExpressionTree tree) {
    if (tree.is(Kind.EQUAL_TO, Kind.NOT_EQUAL_TO)) {
      visitExpression(tree.leftOperand(), MESSAGE_REFACTOR);
      visitExpression(tree.rightOperand(), MESSAGE_REFACTOR);
    }

    super.visitBinaryExpression(tree);
  }

  private void visitExpression(ExpressionTree expression, String message) {
    if (expression.is(Kind.PARENTHESISED_EXPRESSION)) {
      visitExpression(((ParenthesisedExpressionTree) expression).expression(), message);
    }

    if (expression.is(Kind.BOOLEAN_LITERAL)) {
      addIssue(expression, message);
    }
  }

}
