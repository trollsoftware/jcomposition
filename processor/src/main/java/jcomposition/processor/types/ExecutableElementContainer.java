/*
 * Copyright 2017 TrollSoftware (a.shitikov73@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jcomposition.processor.types;

import jcomposition.api.types.IExecutableElementContainer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import java.util.Objects;

public class ExecutableElementContainer implements IExecutableElementContainer {
    private ExecutableElement executableElement;
    private DeclaredType declaredType;
    private ProcessingEnvironment env;
    private boolean hasSuperMethod;

    public ExecutableElementContainer(ExecutableElement executableElement, DeclaredType declaredType, ProcessingEnvironment env) {
        this.executableElement = executableElement;
        this.declaredType = declaredType;
        this.env = env;
    }

    @Override
    public ExecutableElement getExecutableElement() {
        return executableElement;
    }

    @Override
    public DeclaredType getDeclaredType() {
        return declaredType;
    }

    public boolean hasSuperMethod() {
        return hasSuperMethod;
    }

    public void setHasSuperMethod(boolean hasSuperMethod) {
        this.hasSuperMethod = hasSuperMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExecutableElementContainer that = (ExecutableElementContainer) o;

        if (Objects.equals(executableElement.getSimpleName(), that.executableElement.getSimpleName())) {
            return true;
        }

        Types types = env.getTypeUtils();
        TypeMirror t1 = types.erasure(executableElement.getReturnType());
        TypeMirror t2 = types.erasure(that.executableElement.getReturnType());

        return types.isAssignable(t1, t2) || types.isAssignable(t2, t1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(executableElement.getSimpleName(), executableElement.getParameters().size());
    }

    @Override
    public String toString() {
        return "ExecutableElementContainer{" +
                "executableElement=" + executableElement +
                ", declaredType=" + declaredType +
                '}';
    }
}
