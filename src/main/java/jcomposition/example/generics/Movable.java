package jcomposition.example.generics;


import jcomposition.api.annotations.ShareProtected;

public class Movable<T extends IMoveObject> implements IMovable<T> {
    @Override
    public T getMoveObject(T o) {
        return null;
    }

    @ShareProtected
    protected T shareMethod(String s) { return null; }
}
