/*
 * Copyright (c) 2019, Fraunhofer AISEC. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *                    $$$$$$\  $$$$$$$\   $$$$$$\
 *                   $$  __$$\ $$  __$$\ $$  __$$\
 *                   $$ /  \__|$$ |  $$ |$$ /  \__|
 *                   $$ |      $$$$$$$  |$$ |$$$$\
 *                   $$ |      $$  ____/ $$ |\_$$ |
 *                   $$ |  $$\ $$ |      $$ |  $$ |
 *                   \$$$$$   |$$ |      \$$$$$   |
 *                    \______/ \__|       \______/
 *
 */

package de.fraunhofer.aisec.cpg.graph;

import java.util.Objects;

public class ForStatement extends Statement {

  @SubGraph("AST")
  private Statement statement;

  @SubGraph("AST")
  private Statement initializerStatement;

  @SubGraph("AST")
  private Declaration conditionDeclaration;

  @SubGraph("AST")
  private Expression condition;

  @SubGraph("AST")
  private Expression iterationExpression;

  public Statement getStatement() {
    return statement;
  }

  public void setStatement(Statement statement) {
    this.statement = statement;
  }

  public Statement getInitializerStatement() {
    return initializerStatement;
  }

  public void setInitializerStatement(Statement initializerStatement) {
    this.initializerStatement = initializerStatement;
  }

  public Declaration getConditionDeclaration() {
    return conditionDeclaration;
  }

  public void setConditionDeclaration(Declaration conditionDeclaration) {
    this.conditionDeclaration = conditionDeclaration;
  }

  public Expression getCondition() {
    return condition;
  }

  public void setCondition(Expression condition) {
    this.condition = condition;
  }

  public Expression getIterationExpression() {
    return iterationExpression;
  }

  public void setIterationExpression(Expression iterationExpression) {
    this.iterationExpression = iterationExpression;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ForStatement)) {
      return false;
    }
    ForStatement that = (ForStatement) o;
    return super.equals(that)
        && Objects.equals(statement, that.statement)
        && Objects.equals(initializerStatement, that.initializerStatement)
        && Objects.equals(conditionDeclaration, that.conditionDeclaration)
        && Objects.equals(condition, that.condition)
        && Objects.equals(iterationExpression, that.iterationExpression);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
}
