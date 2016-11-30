package jcomposition.example;

import dagger.Module;
import dagger.Provides;
import jcomposition.example.interfaces.IMovable;

public class Movable implements IMovable {
    @Override
    public boolean moveTo(int x, int y) {
        System.out.println("I'm moving to (" + x + ", " + y + ")");
        return false;
    }

    @Module
    public static final class MovableModule {
        @Provides
        public Movable provideMovable() {
            return new Movable();
        }
    }
}
