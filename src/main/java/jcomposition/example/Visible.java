package jcomposition.example;

import jcomposition.example.interfaces.IVisible;

public class Visible implements IVisible {
    private boolean visible;

    @Override
    public void hide() {
        this.visible = false;

        System.out.println("I'm hidden");
    }

    @Override
    public void show() {
        this.visible = true;

        System.out.println("I'm showing");
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }
}
