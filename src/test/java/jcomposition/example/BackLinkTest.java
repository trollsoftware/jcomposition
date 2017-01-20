package jcomposition.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import jcomposition.example.generics.Presenter;
import jcomposition.example.generics.base.ViewBase;

@RunWith(JUnit4.class)
public class BackLinkTest {
    @Test
    public void testTakeView() {
        Presenter presenter = new Presenter();
        presenter.takeView(new ViewBase());
    }
}