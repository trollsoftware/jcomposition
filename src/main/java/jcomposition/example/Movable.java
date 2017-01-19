package jcomposition.example;

import dagger.Module;
import dagger.Provides;
import jcomposition.example.injection.GameObjectWithInjection;
import jcomposition.example.interfaces.IMovable;

public class Movable implements IMovable {
    @Override
    public boolean moveTo(int x, int y) {
        System.out.println("I'm moving to (" + x + ", " + y + ")");
        return false;
    }

    @Module
    public static final class MovableModule {
//        private GameObjectWithInjection.Composition composition;
//
//        public MovableModule(GameObjectWithInjection.Composition composition) {
//            this.composition = composition;
//        }
//
//        @Provides
//        public Movable provideMovable() {
//            return composition.new Composition_Movable();
//        }
    }
}
