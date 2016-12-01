package jcomposition.example;

import jcomposition.example.inheritance.GameObject;
import jcomposition.example.inheritance.IGameObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class InheritanceTest<T extends IGameObject> {
    T go;

    @Before
    @SuppressWarnings("unchecked")
    public void before() {
        go = (T) new GameObject();
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
}
