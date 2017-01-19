package jcomposition.example.generics;

import jcomposition.api.annotations.Bind;

@Bind(Movable.class)
public interface IMovable<V extends IMoveObject> {
    V getMoveObject(V o);
}
