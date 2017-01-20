package jcomposition.example.generics;

import jcomposition.api.annotations.Bind;
import jcomposition.example.generics.base.IViewBase;

@Bind(View.class)
public interface IView<VB extends IViewBase> {
    VB getView();
    void takeView(VB view);
    void dropView();
}