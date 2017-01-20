package jcomposition.example.generics;

import jcomposition.example.generics.base.IRouterBase;

public class Router<R extends IRouterBase> implements IRouter<R> {
    private R router;

    @Override
    public R getRouter() {
        return this.router;
    }

    @Override
    public void takeRouter(R router) {
        this.router = router;
    }

    @Override
    public void dropRouter() {
        this.router = null;
    }
}
