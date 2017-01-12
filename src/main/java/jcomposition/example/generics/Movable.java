package jcomposition.example.generics;


public class Movable<T extends IMoveObject> implements IMovable<T> {
    @Override
    public T getMoveObject() {
        return null;
    }
}
