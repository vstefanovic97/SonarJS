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

import java.util.Optional;
import org.sonar.check.Rule;
import org.sonar.javascript.se.Constraint;
import org.sonar.javascript.se.ProgramState;
import org.sonar.javascript.se.points.ProgramPoint;
import org.sonar.plugins.javascript.api.symbols.Symbol;
import org.sonar.plugins.javascript.api.tree.Tree;
import org.sonar.plugins.javascript.api.tree.expression.AssignmentExpressionTree;
import org.sonar.plugins.javascript.api.tree.expression.CallExpressionTree;
import org.sonar.plugins.javascript.api.tree.expression.ExpressionTree;
import org.sonar.plugins.javascript.api.tree.expression.IdentifierTree;
import org.sonar.plugins.javascript.api.tree.expression.MemberExpressionTree;

@Rule(key = "S4043")
public class ArrayReverseCheck extends AbstractAnyPathSeCheck {

  private static final String REVERSE = "reverse";
  private static final String MESSAGE = "Move this array \"" + REVERSE + "\" operation to a separate statement.";

  @Override
  public void beforeBlockElement(ProgramState currentState, Tree element, ProgramPoint programPoint) {
    if (element.is(Tree.Kind.CALL_EXPRESSION)) {
      CallExpressionTree callExpression = (CallExpressionTree) element;

      if (callExpression.callee().is(Tree.Kind.DOT_MEMBER_EXPRESSION)) {
        MemberExpressionTree memberExpression = (MemberExpressionTree) callExpression.callee();
        checkMemberExpression(currentState, callExpression, memberExpression);
      }
    }
  }

  private void checkMemberExpression(ProgramState currentState, CallExpressionTree callExpression, MemberExpressionTree memberExpression) {
    ExpressionTree object = memberExpression.object();

    if (!isReverseMethod(memberExpression.property()) || !object.is(Tree.Kind.IDENTIFIER_REFERENCE)) {
      return;
    }

    ((IdentifierTree) object).symbol().ifPresent(objectSymbol -> {
      if (isArray(objectSymbol, currentState) && isBeingPassedElsewhere(objectSymbol, callExpression)) {
        this.addUniqueIssue(callExpression, MESSAGE);
      }
    });
  }

  private static boolean isBeingPassedElsewhere(Symbol objectSymbol, Tree element) {
    if (element.parent().is(Tree.Kind.ASSIGNMENT)) {
      AssignmentExpressionTree assignment = (AssignmentExpressionTree) element.parent();

      if (assignment.variable().is(Tree.Kind.IDENTIFIER_REFERENCE)) {
        IdentifierTree identifier = (IdentifierTree) assignment.variable();
        return !sameSymbol(objectSymbol, identifier);
      }
    }

    return element.parent().is(Tree.Kind.ARGUMENT_LIST, Tree.Kind.INITIALIZED_BINDING_ELEMENT);
  }

  private static boolean sameSymbol(Symbol objectSymbol, IdentifierTree lastIdentifier) {
    Optional<Symbol> bindingSymbol = lastIdentifier.symbol();
    return bindingSymbol.map(symbol -> symbol.equals(objectSymbol)).orElse(false);
  }

  private static boolean isArray(Symbol symbol, ProgramState currentState) {
    return currentState.getConstraint(symbol).isStricterOrEqualTo(Constraint.ARRAY);
  }

  private static boolean isReverseMethod(ExpressionTree property) {
    if (property.is(Tree.Kind.PROPERTY_IDENTIFIER)) {
      IdentifierTree propertyIdentifier = (IdentifierTree) property;
      if (REVERSE.equals(propertyIdentifier.name())) {
        return true;
      }
    }
    return false;
  }
}
