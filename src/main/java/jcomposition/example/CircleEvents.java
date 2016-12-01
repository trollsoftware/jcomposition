package jcomposition.example;

import jcomposition.example.interfaces.diamond.ICircleEvents;

public class CircleEvents implements ICircleEvents {
    @Override
    public void onUpdate() {
        System.out.println("Circle onUpdate");
    }

    @Override
    public void onVisibilityChanged(boolean visibility) {
        System.out.println("Visibility of circle has changed to " + visibility);
    }
}
