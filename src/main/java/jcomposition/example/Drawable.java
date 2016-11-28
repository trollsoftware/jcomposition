package jcomposition.example;

import jcomposition.example.interfaces.IDrawable;

public class Drawable implements IDrawable {
    @Override
    public void draw() {
        System.out.println("I'm drawing");
    }
}
