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

import com.google.common.base.Objects;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

public class TypeElementPairContainer {
    public enum ExecutableRelationShip {
        /**
         * There is no hiding or overriding relationship between executable and type element.
         */
        Nothing,
        Same,
        Overriding,
        Hiding
    }

    private TypeElement intf;
    private TypeElement bind;
    private DeclaredType declaredType;
    private ExecutableRelationShip relationShip;
    private boolean useInjection;
    private boolean isAbstract;

    public TypeElementPairContainer(TypeElement intf, TypeElement bind, DeclaredType declaredType, boolean useInjection) {
        this.intf = intf;
        this.bind = bind;
        this.declaredType = declaredType;
        this.useInjection = useInjection;
    }

    public TypeElementPairContainer(TypeElement intf, TypeElement bind, DeclaredType declaredType, boolean useInjection,
                                    ExecutableRelationShip relationShip) {
        this(intf, bind, declaredType, useInjection);
        this.relationShip = relationShip;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean anAbstract) {
        isAbstract = anAbstract;
    }

    public boolean hasUseInjection() {
        return useInjection;
    }

    public void setUseInjection(boolean useInjection) {
        this.useInjection = useInjection;
    }

    public ExecutableRelationShip getRelationShip() {
        return relationShip;
    }

    public void setRelationShip(ExecutableRelationShip relationShip) {
        this.relationShip = relationShip;
    }

    public TypeElement getIntf() {
        return intf;
    }

    public void setIntf(TypeElement intf) {
        this.intf = intf;
    }

    public TypeElement getBind() {
        return bind;
    }

    public void setBind(TypeElement bind) {
        this.bind = bind;
    }

    public DeclaredType getDeclaredType() {
        return declaredType;
    }

    public void setDeclaredType(DeclaredType declaredType) {
        this.declaredType = declaredType;
    }

    @Override
    public String toString() {
        return "TypeElementPairContainer{" +
                "intf=" + intf +
                ", declaredType=" + declaredType +
                ", relationShip=" + relationShip +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeElementPairContainer that = (TypeElementPairContainer) o;
        return Objects.equal(intf, that.intf);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(intf);
    }
}
