package jcomposition.example.generics;

import jcomposition.api.annotations.Bind;

@Bind(Movable.class)
public interface IMovable<T extends IMoveObject> {
    T getMoveObject();
}
