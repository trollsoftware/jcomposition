package jcomposition.example.generics;

import jcomposition.example.generics.base.RouterBase;
import jcomposition.example.generics.base.ViewBase;

public class Presenter extends PresenterGenerated<ViewBase, RouterBase>{
    @Override
    public void takeView(ViewBase view) {
        System.out.println("Presenter.takeView()");
        super.takeView(view);
    }

    @Override
    protected void onTakeView(ViewBase view) {
        System.out.println("Presenter.onTakeView()");
        super.onTakeView(view);
    }
}
