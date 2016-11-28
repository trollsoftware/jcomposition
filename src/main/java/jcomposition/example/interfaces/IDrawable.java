package jcomposition.example.interfaces;

import jcomposition.api.annotations.Bind;
import jcomposition.example.Drawable;

@Bind(Drawable.class)
public interface IDrawable {
    void draw();
}
