package jcomposition.example;

import dagger.Module;
import dagger.Provides;
import jcomposition.example.injection.GameObjectWithInjection;
import jcomposition.example.injection.GameObjectWithInjectionBase;

@Module
public final class MovableModule {
    private GameObjectWithInjection.Composition composition;

    public MovableModule(GameObjectWithInjection.Composition composition) {
        this.composition = composition;
    }

    @Provides
    public GameObjectWithInjectionBase.Composition.Composition_Movable provideMovable()  {
        return composition.new Composition_Movable();
    }
}
