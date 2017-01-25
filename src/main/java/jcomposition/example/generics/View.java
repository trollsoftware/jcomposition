/*
 * Copyright 2017 TrollSoftware (a.shitikov73@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
