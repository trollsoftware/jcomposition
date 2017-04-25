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

package jcomposition.processor.utils;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.common.base.Optional;
import com.sun.tools.javac.code.Type;
import jcomposition.api.Const;
import jcomposition.api.IMergeConflictPolicy;
import jcomposition.api.annotations.Bind;
import jcomposition.api.annotations.Composition;
import jcomposition.api.annotations.ShareProtected;
import jcomposition.api.annotations.UseInjection;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.lang.annotation.IncompleteAnnotationException;
import java.util.Map;

public final class AnnotationUtils {
    public static AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String name, ProcessingEnvironment env) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> elementValuesWithDefaults =
                env.getElementUtils().getElementValuesWithDefaults(annotationMirror);

        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValuesWithDefaults.entrySet()) {
            if (name.contentEquals(entry.getKey().getSimpleName())) {
                return entry.getValue();
            }
        }

        return null;
    }

    public static String getCompositionName(TypeElement element, ProcessingEnvironment env) {
        Optional<AnnotationValue> value = getParameterFrom(element, Composition.class, "name", env);
        String defaultName = element.getSimpleName() + "_Generated";

        if (value.isPresent()) {
            String name = (String) value.get().getValue();

            if (!Const.UNDEFINED.equals(name)) {
                return name;
            }
        }

        return defaultName;
    }

    @SuppressWarnings("unchecked")
    public static IMergeConflictPolicy getCompositionMergeConflictPolicy(TypeElement element, ProcessingEnvironment env) {
        Optional<AnnotationValue> value = getParameterFrom(element, Composition.class, "onConflict", env);

        if (value.isPresent()) {
            TypeElement typeElement = MoreTypes.asTypeElement((Type) value.get().getValue());
            try {
                return (IMergeConflictPolicy) Class.forName(typeElement.getQualifiedName().toString()).newInstance();
            } catch (Exception ignore) { }
        }

        throw new IncompleteAnnotationException(Composition.class, "onConflict");
    }

    public static TypeElement getBindClassType(TypeElement element, ProcessingEnvironment env) {
        Optional<AnnotationValue> value = getParameterFrom(element
                , Bind.class
                , "value"
                , env);

        if (value.isPresent()) {
            TypeElement typeElement = MoreTypes.asTypeElement((Type) value.get().getValue());
            AnnotationMirror bindMirror = MoreElements.getAnnotationMirror(typeElement, Bind.class).orNull();

            if (!typeElement.getKind().isClass()) {
                env.getMessager().printMessage(Diagnostic.Kind.ERROR
                        , "Bind's annotation value must be class"
                        , element
                        , bindMirror
                        , value.get());

                return null;
            }

            /**
             * Bind annotation is not valid if Bind's class value isn't implements element interface
             */
            javax.lang.model.util.Types types = env.getTypeUtils();

            if (!types.isAssignable(types.getDeclaredType(typeElement), types.getDeclaredType(element))
                    && !types.isAssignable(typeElement.getSuperclass(), element.asType())) {
                env.getMessager().printMessage(Diagnostic.Kind.ERROR
                        , "Bind's annotation value class must implement " + element.getSimpleName() + " interface"
                        , element
                        , bindMirror
                        , value.get());

                return null;
            }

            return typeElement;
        }

        return null;
    }

    public static boolean hasShareProtectedAnnotation(Element element) {
        return MoreElements.isAnnotationPresent(element, ShareProtected.class);
    }

    public static boolean hasUseInjectionAnnotation(TypeElement element) {
        return MoreElements.isAnnotationPresent(element, UseInjection.class);
    }

    public static boolean hasInheritedInjectionAnnotation(TypeElement element) {
        if (hasUseInjectionAnnotation(element))
            return true;

        for (TypeMirror typeInterface : element.getInterfaces()) {
            TypeElement asElement = MoreTypes.asTypeElement(typeInterface);

            if (hasUseInjectionAnnotation(asElement))
                return true;
        }

        return false;
    }

    private static Optional<AnnotationValue> getParameterFrom(TypeElement typeElement
            , Class<? extends Annotation> annotationClass
            , String paramName
            , ProcessingEnvironment env) {
        Optional<AnnotationMirror> annotationMirror = MoreElements.getAnnotationMirror(typeElement, annotationClass);

        if (annotationMirror.isPresent()) {
            AnnotationValue value = getAnnotationValue(annotationMirror.get(), paramName, env);

            return Optional.fromNullable(value);
        }

        return Optional.absent();
    }
}
