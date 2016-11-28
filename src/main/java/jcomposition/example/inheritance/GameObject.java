package jcomposition.example.inheritance;

public class GameObject extends GameObjectBase {
    @Override
    public void draw() {
        super.draw();

        System.out.println("Some custom action here...");

        // Also update GameObject on draw.
        getComposition().composition_Updatable.update();
    }
}
