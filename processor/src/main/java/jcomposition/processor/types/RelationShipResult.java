package jcomposition.processor.types;

public class RelationShipResult {
    private boolean duplicateFound;
    private TypeElementPairContainer.ExecutableRelationShip relationShip;

    public RelationShipResult(boolean duplicateFound, TypeElementPairContainer.ExecutableRelationShip relationShip) {
        this.duplicateFound = duplicateFound;
        this.relationShip = relationShip;
    }

    public boolean isDuplicateFound() {
        return duplicateFound;
    }

    public void setDuplicateFound(boolean duplicateFound) {
        this.duplicateFound = duplicateFound;
    }

    public TypeElementPairContainer.ExecutableRelationShip getRelationShip() {
        return relationShip;
    }

    public void setRelationShip(TypeElementPairContainer.ExecutableRelationShip relationShip) {
        this.relationShip = relationShip;
    }
}
