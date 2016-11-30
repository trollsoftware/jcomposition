package jcomposition.example.interfaces;

import jcomposition.api.annotations.Bind;
import jcomposition.api.annotations.UseInjection;
import jcomposition.example.Movable;

@Bind(Movable.class)
@UseInjection
public interface IMovable {
    boolean moveTo(int x, int y);
}
