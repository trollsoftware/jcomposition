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

package jcomposition.api.handlers;

import jcomposition.api.ITypeHandler;
import jcomposition.api.types.specs.AnnotationSpecModel;
import jcomposition.api.types.specs.TypeSpecModel;

import javax.annotation.Generated;

public class DefaultTypeHandler implements ITypeHandler {
    @Override
    public void onCompositionGenerated(TypeSpecModel model) {
        applyGeneratedAnnotation(model);
    }

    @Override
    public void onInternalCompositionGenerated(TypeSpecModel model) {
        applyGeneratedAnnotation(model);
    }

    private void applyGeneratedAnnotation(TypeSpecModel model) {
        AnnotationSpecModel spec = new AnnotationSpecModel(Generated.class);
        spec.addMember("value", "jcomposition.processor.AnnotationProcessor");
        model.addAnnotation(spec);
    }
}
