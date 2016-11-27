package jcomposition.processor.utils;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.common.base.Optional;
import com.sun.tools.javac.code.Type;
import jcomposition.api.annotations.Bind;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class TypeElementUtils {
    public static TypeElement getBindClassType(TypeElement element, Elements utils) {
        Optional<AnnotationMirror> annotationMirror = MoreElements.getAnnotationMirror(element, Bind.class);

        if (annotationMirror.isPresent()) {
            AnnotationValue value = AnnotationUtils.getAnnotationValue(annotationMirror.get(), "value", utils);

            if (value != null) {
                TypeElement typeElement = MoreTypes.asTypeElement((Type) value.getValue());

                return typeElement;
            }
        }

        return null;
    }
}
