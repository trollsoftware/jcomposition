package jcomposition.example.interfaces;

import jcomposition.api.annotations.Bind;
import jcomposition.example.Updatable;

@Bind(Updatable.class)
public interface IUpdatable {
    void update();
}
