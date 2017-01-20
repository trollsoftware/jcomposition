package jcomposition.example.injection;

import jcomposition.example.MovableModule;

public class GameObjectWithInjection extends GameObjectWithInjectionBase {
    private InjectionComponent injectionComponent;

    @Override
    protected void onInject(Composition composition) {
        injectionComponent = DaggerInjectionComponent
                .builder()
                .movableModule(new MovableModule(composition))
                .build();

        injectionComponent.inject(composition);
    }
}
