package jcomposition.example.interfaces;

import jcomposition.api.annotations.Bind;
import jcomposition.example.Visible;

@Bind(Visible.class)
public interface IVisible {
    void hide();
    void show();

    boolean isVisible();
}
