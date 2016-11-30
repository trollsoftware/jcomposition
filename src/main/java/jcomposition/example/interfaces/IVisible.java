package jcomposition.example.interfaces;

import jcomposition.api.annotations.Bind;
import jcomposition.api.annotations.UseInjection;
import jcomposition.example.Visible;

@Bind(Visible.class)
@UseInjection
public interface IVisible {
    void hide();
    void show();

    boolean isVisible();
}
