package jcomposition.example;

import jcomposition.example.interfaces.IUpdatable;

public class Updatable implements IUpdatable {
    @Override
    public void update() {
        System.out.println("I'm updating");
    }
}
