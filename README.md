[![](https://jitpack.io/v/trollsoftware/jcomposition.svg)](https://jitpack.io/#trollsoftware/jcomposition) [![Build Status](https://travis-ci.org/trollsoftware/jcomposition.svg?branch=master)](https://travis-ci.org/trollsoftware/jcomposition)
# JComposition
JComposition is a lightweight Java API based on annotations for creating compositions at compile-time. Composition over inheritance.

## Introduction
Say you have 2 interfaces:
```java
public interface IDrawable {
    void draw();
}
public interface IUpdatable {
    void update();
}
```
And 2 classes that implements them:
```java
public class Drawable implements IDrawable {
    @Override
    public void draw() {
        System.out.println("I'm drawing");
    }
}
public class Updatable implements IUpdatable {
    @Override
    public void update() {
        System.out.println("I'm updating");
    }
}
```
And perhaps you want a `GameObject` class that have `Drawable` and `Updatable` behaviour:
```java
public class GameObject extends Drawable, Updatable {
}
GameObject gameObject = new GameObject();
gameObject.update();
gameObject.draw();
```
But Java does not allow you to have more than one superclass. So the code above will not compile.

JComposition is tool that you can use to mix such logic in one class without duplicated code. Also it support dependency injection and generics.

## Using JComposition
JComposition uses interfaces as base for code generation. 
### Basic usage
Define your interfaces for each module you want to extend:
```java
@Bind(Drawable.class)
public interface IDrawable {
    void draw();
}
@Bind(Updatable.class)
public interface IUpdatable {
    void update();
}
```
`@Bind` annotation binds your interface to class that implements logic.
Implementation of `IDrawable` and `IUpdatable` you can find in *Introduction* section

Then create a interface for `GameObject` class:
```java
@Composition(name = "GameObjectBase")
@Bind(GameObject.class)
public interface IGameObject extends IUpdatable, IDrawable {
}
```
`@Composition` annotation marks this interface for annotation processor to generate composite class with `name = "GameObjectBase"`.

Then just extend `GameObject` from `GameObjectBase`:
```java
public class GameObject extends GameObjectBase {
}
// Now this will work
GameObject gameObject = new GameObject();
gameObject.update();
gameObject.draw();
```
### Overriding
By default `gameObject.draw()` will call `getComposition().composition_Drawable.draw()`, but you can override behaviour this way:
```java
public class GameObject extends GameObjectBase {
    @Override
    public void draw() {
        super.draw(); // will call getComposition().composition_Drawable.draw()
        
        // Some custom action
        getComposition().composition_Updatable.update();
    }
}
gameObject.draw();
// Output
I'm drawing
I'm updating
```

### Dependency injection
Use `@UseInjection` annotation to let processor mark composition's fields `@Inject` annotation.
```java
@Bind(Movable.class)
@UseInjection
public interface IMovable {
    boolean moveTo(int x, int y);
}
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
```
When `Movable` composition will ready for injection abstract method `onInject(Composition)` will be called:
```java
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
```

## Constraints
If you are not using dependency injection, binded class must have an empty argument constructor.

## Examples
You can find more examples  [here](./src/main/java/jcomposition/example "JComposition examples")

## Download
We are using JitPack for publishing our libraries. 
Add jitpack.io to your repositories first to build.gradle:
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
And add the dependency:
```
dependencies {
    // Use apt for processor instead of compile if you have it
    compile 'com.github.trollsoftware.jcomposition:processor:1.0'
    compile 'com.github.trollsoftware.jcomposition:api:1.0'
}
```

## License
This library is distributed under the Apache 2.0 license found in the [LICENSE](./LICENSE) file.
