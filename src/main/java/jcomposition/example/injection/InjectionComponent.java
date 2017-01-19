package jcomposition.example.injection;

import dagger.Component;
import jcomposition.api.IComposition;
import jcomposition.example.Movable;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        Movable.MovableModule.class
})
public interface InjectionComponent {
    void inject(GameObjectWithInjection.Composition composition);
}
