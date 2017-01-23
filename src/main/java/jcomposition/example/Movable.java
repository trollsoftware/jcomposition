package jcomposition.example;

import jcomposition.api.annotations.ShareProtected;
import jcomposition.example.interfaces.IMovable;

@ShareProtected
public abstract class Movable implements IMovable {
    @Override
    public boolean moveTo(int x, int y) {
        System.out.println("I'm moving to (" + x + ", " + y + ")");
        onMove();

        return false;
    }

    protected abstract void onMove();
}
