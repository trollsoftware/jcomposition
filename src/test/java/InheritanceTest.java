import jcomposition.example.inheritance.GameObject;
import jcomposition.example.inheritance.IGameObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class InheritanceTest {
    IGameObject go;

    @Before
    public void before() {
        go = new GameObject();
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
