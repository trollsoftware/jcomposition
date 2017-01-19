package jcomposition.processor.types;

import com.google.common.base.Objects;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

import jcomposition.processor.utils.AnnotationUtils;

public class TypeElementContainer {
    public enum ExecutableRelationShip {
        /**
         * There is no hiding or overriding relationship between executable and type element.
         */
        Nothing,
        Overriding,
        Hiding
    }

    private TypeElement typeElement;
    private DeclaredType declaredType;
    private ExecutableRelationShip relationShip;
    private boolean useInjection;

    public TypeElementContainer(TypeElement typeElement, DeclaredType declaredType, boolean useInjection) {
        this.typeElement = typeElement;
        this.declaredType = declaredType;
        this.useInjection = useInjection;
    }

    public TypeElementContainer(TypeElement typeElement, DeclaredType declaredType, boolean useInjection,
                                ExecutableRelationShip relationShip) {
        this(typeElement, declaredType, useInjection);
        this.relationShip = relationShip;
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

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public void setTypeElement(TypeElement typeElement) {
        this.typeElement = typeElement;
    }

    public DeclaredType getDeclaredType() {
        return declaredType;
    }

    public void setDeclaredType(DeclaredType declaredType) {
        this.declaredType = declaredType;
    }

    @Override
    public String toString() {
        return "TypeElementContainer{" +
                "typeElement=" + typeElement +
                ", declaredType=" + declaredType +
                ", relationShip=" + relationShip +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeElementContainer that = (TypeElementContainer) o;
        return Objects.equal(typeElement, that.typeElement);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(typeElement);
    }
}
