package jcomposition.example.generics;

import jcomposition.api.annotations.Bind;
import jcomposition.example.generics.base.IRouterBase;

@Bind(Router.class)
public interface IRouter<RB extends IRouterBase> {
    RB getRouter();
    void takeRouter(RB router);
    void dropRouter();
}