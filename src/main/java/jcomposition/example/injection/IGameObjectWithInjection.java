package jcomposition.example.injection;

import jcomposition.api.annotations.Composition;
import jcomposition.example.interfaces.IDrawable;
import jcomposition.example.interfaces.IMovable;
import jcomposition.example.interfaces.IUpdatable;
import jcomposition.example.interfaces.IVisible;

@Composition(name = "GameObjectWithInjectionBase")
public interface IGameObjectWithInjection extends IDrawable, IMovable, IUpdatable, IVisible {
}
