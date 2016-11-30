package jcomposition.processor.utils;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.common.base.Optional;
import com.sun.tools.javac.code.Type;
import jcomposition.api.Const;
import jcomposition.api.annotations.Bind;
import jcomposition.api.annotations.Composition;
import jcomposition.api.annotations.UseInjection;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.lang.annotation.Annotation;

public class TypeElementUtils {

    public static String getCompositionName(TypeElement element, Elements utils) {
        Optional<AnnotationValue> value = getParameterFrom(element, Composition.class, "name", utils);
        String defaultName = element.getSimpleName() + "_Generated";

        if (value.isPresent()) {
            String name = (String) value.get().getValue();

            if (!Const.UNDEFINED.equals(name)) {
                return name;
            }
        }

        return defaultName;
    }

    public static TypeElement getBindClassType(TypeElement element, Elements utils) {
        Optional<AnnotationValue> value = getParameterFrom(element, Bind.class, "value", utils);

        if (value.isPresent()) {
            TypeElement typeElement = MoreTypes.asTypeElement((Type) value.get().getValue());

            return typeElement;
        }

        return null;
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
            , Elements utils) {
        Optional<AnnotationMirror> annotationMirror = MoreElements.getAnnotationMirror(typeElement, annotationClass);

        if (annotationMirror.isPresent()) {
            AnnotationValue value = AnnotationUtils.getAnnotationValue(annotationMirror.get(), paramName, utils);

            return Optional.fromNullable(value);
        }

        return Optional.absent();
    }
}
