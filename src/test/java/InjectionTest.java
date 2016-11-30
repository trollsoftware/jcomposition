import jcomposition.example.injection.GameObjectWithInjection;
import jcomposition.example.injection.IGameObjectWithInjection;
import org.junit.Before;
import org.junit.Test;

public class InjectionTest {
    IGameObjectWithInjection go;

    @Before
    public void before() {
        go = new GameObjectWithInjection();
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
    public void testMovable() {
        go.moveTo(1, 2);
    }
}
