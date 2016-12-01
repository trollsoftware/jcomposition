package jcomposition.example.diamond;

import jcomposition.api.annotations.Composition;
import jcomposition.example.inheritance.IGameObject;
import jcomposition.example.interfaces.diamond.ICircleEvents;
import jcomposition.example.interfaces.diamond.ISquareEvents;

@Composition(name = "GameObjectWithDiamondBase")
public interface IGameObjectWithDiamond extends IGameObject, ICircleEvents, ISquareEvents {
}
