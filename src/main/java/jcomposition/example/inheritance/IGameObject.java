package jcomposition.example.inheritance;

import jcomposition.api.annotations.Bind;
import jcomposition.api.annotations.Composition;
import jcomposition.example.interfaces.IDrawable;

import jcomposition.example.interfaces.IUpdatable;
import jcomposition.example.interfaces.IVisible;

@Composition(name = "GameObjectBase")
public interface IGameObject extends IVisible, IUpdatable, IDrawable {

}
