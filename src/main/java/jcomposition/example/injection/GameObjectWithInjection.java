package jcomposition.example.injection;

public class GameObjectWithInjection extends GameObjectWithInjectionBase {
    private InjectionComponent injectionComponent;

    @Override
    protected void onMove() {
        System.out.println("OnMove()");
    }

    @Override
    protected void onInject(Composition composition) {
        injectionComponent = DaggerInjectionComponent
                .builder()
                .movableModule(new MovableModule(composition))
                .build();

        injectionComponent.inject(composition);
    }
}
