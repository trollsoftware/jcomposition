package jcomposition.processor.utils;

import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import java.util.Map;

public class AnnotationUtils {
    public static AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String name, Elements utils) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> elementValuesWithDefaults =
                utils.getElementValuesWithDefaults(annotationMirror);

        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValuesWithDefaults.entrySet()) {
            if (name.contentEquals(entry.getKey().getSimpleName())) {
                return entry.getValue();
            }
        }

        return null;
    }
}
