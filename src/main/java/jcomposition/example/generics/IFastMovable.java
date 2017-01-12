package jcomposition.example.generics;

import jcomposition.api.annotations.Bind;
import jcomposition.api.annotations.Composition;

@Bind(FastMovable.class)
@Composition(name = "FastMovableGenerated")
public interface IFastMovable<T extends IMoveObject> extends IMovable<T> {
    void moveFast();
}
