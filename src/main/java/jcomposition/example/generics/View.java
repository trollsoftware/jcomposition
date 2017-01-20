package jcomposition.example.generics;

import jcomposition.api.annotations.ShareProtected;
import jcomposition.example.generics.base.IViewBase;

public class View<V extends IViewBase> implements IView<V>{
    private V view;

    @Override
    public V getView() {
        return this.view;
    }

    @Override
    public void takeView(V view) {
        this.view = view;
        onTakeView(view);
    }

    @Override
    public void dropView() {
        this.view = null;
        onDropView();
    }

    @ShareProtected
    protected void onTakeView(V view) {
        System.out.println("View.onTakeView()");
    }

    protected void onDropView() {}
}
