package jcomposition.example.generics;

import jcomposition.api.annotations.Composition;
import jcomposition.example.generics.base.IRouterBase;
import jcomposition.example.generics.base.IViewBase;

@Composition(name = "PresenterGenerated")
public interface IPresenter<V extends IViewBase, R extends IRouterBase>
    extends IView<V>, IRouter<R> {
}
