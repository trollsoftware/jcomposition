package jcomposition.processor.types;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

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

    public TypeElementContainer(TypeElement typeElement, DeclaredType declaredType) {
        this.typeElement = typeElement;
        this.declaredType = declaredType;
    }

    public TypeElementContainer(TypeElement typeElement, DeclaredType declaredType, ExecutableRelationShip relationShip) {
        this(typeElement, declaredType);
        this.relationShip = relationShip;
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
}
