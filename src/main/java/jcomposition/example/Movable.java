package jcomposition.example;

import jcomposition.example.interfaces.IMovable;

public class Movable implements IMovable {
    @Override
    public boolean moveTo(int x, int y) {
        System.out.println("I'm moving to (" + x + ", " + y + ")");
        return false;
    }


}
