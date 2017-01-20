package jcomposition.example.injection;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        MovableModule.class
})
public interface InjectionComponent {
    void inject(GameObjectWithInjection.Composition composition);
}
