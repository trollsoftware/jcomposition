package jcomposition.example.injection;

import jcomposition.example.Movable;

public class GameObjectWithInjection extends GameObjectWithInjectionBase {
    private InjectionComponent injectionComponent;

    @Override
    protected void onInject(Composition composition) {
        injectionComponent = DaggerInjectionComponent
                .builder()
                .movableModule(new Movable.MovableModule())
                .build();

        injectionComponent.inject(composition);
    }
}
