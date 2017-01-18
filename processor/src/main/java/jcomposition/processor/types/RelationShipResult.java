package jcomposition.processor.types;

public class RelationShipResult {
    private boolean duplicateFound;
    private TypeElementContainer.ExecutableRelationShip relationShip;

    public RelationShipResult(boolean duplicateFound, TypeElementContainer.ExecutableRelationShip relationShip) {
        this.duplicateFound = duplicateFound;
        this.relationShip = relationShip;
    }

    public boolean isDuplicateFound() {
        return duplicateFound;
    }

    public void setDuplicateFound(boolean duplicateFound) {
        this.duplicateFound = duplicateFound;
    }

    public TypeElementContainer.ExecutableRelationShip getRelationShip() {
        return relationShip;
    }

    public void setRelationShip(TypeElementContainer.ExecutableRelationShip relationShip) {
        this.relationShip = relationShip;
    }
}
