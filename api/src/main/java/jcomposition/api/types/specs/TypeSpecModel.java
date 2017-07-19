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

package jcomposition.api.types.specs;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class is a super-stripped version of TypeSpec.Builder from Java Poet.
 */
public class TypeSpecModel {
    private List<AnnotationSpecModel> annotations = new ArrayList<AnnotationSpecModel>();

    public TypeSpecModel addAnnotation(Class<? extends Annotation> clazz) {
        addAnnotation(new AnnotationSpecModel(clazz));
        return this;
    }

    public TypeSpecModel addAnnotation(AnnotationSpecModel model) {
        annotations.add(model);
        return this;
    }

    public List<AnnotationSpecModel> getAnnotations() {
        return annotations;
    }
}
