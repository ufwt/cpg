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

import de.fraunhofer.aisec.cpg.graph.HasType.TypeListener;
import de.fraunhofer.aisec.cpg.graph.type.Type;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.checkerframework.checker.nullness.qual.Nullable;

/** Represents the declaration of a variable. */
public class VariableDeclaration extends ValueDeclaration implements TypeListener {

  /** The (optional) initializer of the declaration. */
  @SubGraph("AST")
  @Nullable
  protected Expression initializer;

  /**
   * C++ uses implicit constructor calls for statements like <code>A a;</code> but this only applies
   * to types that are actually classes and not just primitive types or typedef aliases of
   * primitives. Thus, during AST construction, we can only suggest that an implicit constructor
   * call might be allowed by the language (so this is set to true for C++ but false for Java, as
   * such a statement in Java leads to an uninitialized variable). The final decision can then be
   * made after we have analyzed all classes present in the current scope.
   */
  private boolean implicitInitializerAllowed = false;

  public boolean isImplicitInitializerAllowed() {
    return implicitInitializerAllowed;
  }

  public void setImplicitInitializerAllowed(boolean implicitInitializerAllowed) {
    this.implicitInitializerAllowed = implicitInitializerAllowed;
  }

  private boolean isArray = false;

  public boolean isArray() {
    return isArray;
  }

  public void setIsArray(boolean isArray) {
    this.isArray = isArray;
  }

  @Nullable
  public Expression getInitializer() {
    return initializer;
  }

  @Nullable
  public <T> T getInitializerAs(Class<T> clazz) {
    return clazz.cast(getInitializer());
  }

  public void setInitializer(@Nullable Expression initializer) {
    if (this.initializer != null) {
      this.removePrevDFG(this.initializer);
      this.initializer.unregisterTypeListener(this);

      if (this.initializer instanceof TypeListener) {
        this.unregisterTypeListener((TypeListener) this.initializer);
      }
    }

    this.initializer = initializer;

    if (initializer != null) {
      this.addPrevDFG(initializer);
      initializer.registerTypeListener(this);

      // if the initializer implements a type listener, inform it about our type changes
      // since the type is tied to the declaration but it is convenient to have the type
      // information in the initializer, i.e. in a ConstructExpression.
      if (initializer instanceof TypeListener) {
        this.registerTypeListener((TypeListener) initializer);
      }
    }
  }

  @Override
  public void typeChanged(HasType src, HasType root, Type oldType) {
    if (!TypeManager.getInstance().isUnknown(this.type)
        && src.getPropagationType().equals(oldType)) {
      return;
    }

    Type previous = this.type;
    Type newType;
    if (src == initializer && initializer instanceof InitializerListExpression) {
      // Init list is seen as having an array type, but can be used ambiguously. It can be either
      // used to initialize an array, or to initialize some objects. If it is used as an
      // array initializer, we need to remove the array/pointer layer from the type, otherwise it
      // can be ignored once we have a type
      if (isArray) {
        newType = src.getType();
      } else if (!TypeManager.getInstance().isUnknown(this.type)) {
        return;
      } else {
        newType = src.getType().dereference();
      }
    } else {
      newType = src.getPropagationType();
    }

    setType(newType, root);
    if (!previous.equals(this.type)) {
      this.type.setTypeOrigin(Type.Origin.DATAFLOW);
    }
  }

  @Override
  public void possibleSubTypesChanged(HasType src, HasType root, Set<Type> oldSubTypes) {
    Set<Type> subTypes = new HashSet<>(getPossibleSubTypes());
    subTypes.addAll(src.getPossibleSubTypes());
    setPossibleSubTypes(subTypes, root);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, Node.TO_STRING_STYLE)
        .append("name", name)
        .append("location", location)
        .append("initializer", initializer)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof VariableDeclaration)) {
      return false;
    }
    VariableDeclaration that = (VariableDeclaration) o;
    return super.equals(that) && Objects.equals(initializer, that.initializer);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
}
