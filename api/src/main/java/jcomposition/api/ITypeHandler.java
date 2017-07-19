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

package jcomposition.api;

import jcomposition.api.annotations.Composition;
import jcomposition.api.types.specs.TypeSpecModel;

/**
 * Subclasses must have an empty argument constructor
 */
public interface ITypeHandler {
    /**
     * Called when class specified by {@link Composition#name()} is ready to be generated
     * @param model you can use model to make an additional correct actions,
     *              for example to add custom annotation to generated class
     */
    void onCompositionGenerated(TypeSpecModel model);

    /**
     * Called when internal Composition class ({@link IComposition#getComposition()})
     * of outer class, specified by {@link Composition#name()}, is ready to be generated
     * @param model you can use model to make an additional correct actions
     */
    void onInternalCompositionGenerated(TypeSpecModel model);
}
