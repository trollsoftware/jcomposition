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
import java.util.HashMap;
import java.util.Map;

/**
 * This class is super-stripped version of AnnotationSpec.Builder from Java Poet
 */
public class AnnotationSpecModel {
    private Class<? extends Annotation> annotation;
    private Map<String, Object> members = new HashMap<String, Object>();

    public AnnotationSpecModel(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
    }

    public AnnotationSpecModel addMember(String member, Object value) {
        members.put(member, value);
        return this;
    }

    public Class<? extends Annotation> getAnnotation() {
        return annotation;
    }

    public Map<String, Object> getMembers() {
        return members;
    }
}
