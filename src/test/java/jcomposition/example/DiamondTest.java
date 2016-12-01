package jcomposition.example;

import jcomposition.example.diamond.GameObjectWithDiamond;
import jcomposition.example.diamond.IGameObjectWithDiamond;
import jcomposition.example.injection.GameObjectWithInjection;
import org.junit.Before;
import org.junit.Test;

public class DiamondTest extends InheritanceTest<IGameObjectWithDiamond> {
    @Before
    public void before() {
        go = new GameObjectWithDiamond();
    }

    @Test
    public void testVisible() {
        go.hide();
        go.show();
    }

    @Test
    public void testUpdatable() {
        go.update();
    }

    @Test
    public void testDrawable() {
        go.draw();
    }

    @Test
    public void testUpdateEvent() {
        go.onUpdate();
    }

    @Test
    public void testVisibilityEvent() {
        go.onVisibilityChanged(true);
    }
}
