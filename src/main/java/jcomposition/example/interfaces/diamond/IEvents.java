package jcomposition.example.interfaces.diamond;

import jcomposition.api.annotations.Bind;
import jcomposition.example.Events;

@Bind(Events.class)
public interface IEvents {
    void onUpdate();
    void onVisibilityChanged(boolean visibility);
}
