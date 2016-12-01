package jcomposition.example;

import jcomposition.example.interfaces.diamond.ISquareEvents;

public class SquareEvents implements ISquareEvents {
    @Override
    public void onUpdate() {
        System.out.println("Square onUpdate");
    }

    @Override
    public void onVisibilityChanged(boolean visibility) {
        System.out.println("Visibility of Square has changed to " + visibility);
    }
}
