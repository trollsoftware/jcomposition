package jcomposition.example;

import jcomposition.example.interfaces.diamond.IEvents;

public class Events implements IEvents {
    @Override
    public void onUpdate() {
        System.out.println("onUpdate in events");
    }

    @Override
    public void onVisibilityChanged(boolean visibility) {
        System.out.println("Visibility changed to : " + visibility);
    }
}
