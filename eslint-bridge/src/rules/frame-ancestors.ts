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
// https://jira.sonarsource.com/browse/RSPEC-5732

import { Rule } from 'eslint';
import * as estree from 'estree';
import { getModuleNameOfNode, getObjectExpressionProperty } from './utils';
import { Express } from './utils-express';

const HELMET = 'helmet';
const HELMET_CSP = 'helmet-csp';
const DIRECTIVES = 'directives';
const NONE = "'none'";
const CONTENT_SECURITY_POLICY = 'contentSecurityPolicy';
const FRAME_ANCESTORS_CAMEL = 'frameAncestors';
const FRAME_ANCESTORS_HYPHEN = 'frame-ancestors';

export const rule: Rule.RuleModule = Express.SensitiveMiddlewarePropertyRule(
  findDirectivesWithSensitiveFrameAncestorsPropertyFromHelmet,
  `Make sure disabling content security policy frame-ancestors directive is safe here.`,
);

function findDirectivesWithSensitiveFrameAncestorsPropertyFromHelmet(
  context: Rule.RuleContext,
  node: estree.CallExpression,
): estree.Property[] {
  const { arguments: args } = node;
  if (isValidHelmetModuleCall(context, node) && args.length === 1) {
    const [options] = args;
    const maybeDirectives = getObjectExpressionProperty(options, DIRECTIVES);
    if (maybeDirectives) {
      const maybeFrameAncestors = getFrameAncestorsProperty(maybeDirectives);
      if (!maybeFrameAncestors) {
        return [maybeDirectives];
      }
      if (isSetNoneFrameAncestorsProperty(maybeFrameAncestors)) {
        return [maybeFrameAncestors];
      }
    }
  }
  return [];
}

function isValidHelmetModuleCall(context: Rule.RuleContext, callExpr: estree.CallExpression) {
  const { callee } = callExpr;

  /* csp(options) */
  if (callee.type === 'Identifier' && getModuleNameOfNode(context, callee)?.value === HELMET_CSP) {
    return true;
  }

  /* helmet.contentSecurityPolicy(options) */
  if (
    callee.type === 'MemberExpression' &&
    getModuleNameOfNode(context, callee.object)?.value === HELMET &&
    callee.property.type === 'Identifier' &&
    callee.property.name === CONTENT_SECURITY_POLICY
  ) {
    return true;
  }

  return false;
}

function isSetNoneFrameAncestorsProperty(frameAncestors: estree.Property): boolean {
  const { value } = frameAncestors;
  return (
    value.type === 'ArrayExpression' &&
    Boolean(
      value.elements.find(
        v => v?.type === 'Literal' && typeof v.value === 'string' && v.value === NONE,
      ),
    )
  );
}

function getFrameAncestorsProperty(directives: estree.Property): estree.Property | undefined {
  const propertyKeys = [FRAME_ANCESTORS_CAMEL, FRAME_ANCESTORS_HYPHEN];
  for (const propertyKey of propertyKeys) {
    const maybeProperty = getObjectExpressionProperty(directives.value, propertyKey);
    if (maybeProperty) {
      return maybeProperty;
    }
  }
  return undefined;
}
