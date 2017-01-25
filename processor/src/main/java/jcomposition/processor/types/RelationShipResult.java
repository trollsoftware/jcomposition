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
