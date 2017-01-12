package jcomposition.processor.utils;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;
import jcomposition.api.Const;
import jcomposition.api.annotations.Bind;
import jcomposition.api.annotations.Composition;
import jcomposition.api.annotations.UseInjection;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.lang.annotation.IncompleteAnnotationException;

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

    public static Composition.MergeConflictPolicy getCompositionMergeConflictPolicy(TypeElement element, Elements utils) {
        Optional<AnnotationValue> value = getParameterFrom(element, Composition.class, "onConflict", utils);

        if (value.isPresent()) {
            Symbol.VarSymbol vs = (Symbol.VarSymbol) value.get().getValue();

            return Enum.valueOf(Composition.MergeConflictPolicy.class, vs.getSimpleName().toString());
        }

        throw new IncompleteAnnotationException(Composition.class, "onConflict");
    }

    public static TypeElement getBindClassType(TypeElement element, ProcessingEnvironment environment) {
        Optional<AnnotationValue> value = getParameterFrom(element
                , Bind.class
                , "value"
                , environment.getElementUtils());

        if (value.isPresent()) {
            TypeElement typeElement = MoreTypes.asTypeElement((Type) value.get().getValue());
            AnnotationMirror bindMirror = MoreElements.getAnnotationMirror(typeElement, Bind.class).orNull();

            if (!typeElement.getKind().isClass() || isAbstract(typeElement)) {
                environment.getMessager().printMessage(Diagnostic.Kind.ERROR
                        , "Bind's annotation value must be kind of non-abstract class"
                        , element
                        , bindMirror
                        , value.get());

                return null;
            }

            /**
             * Bind annotation is not valid if Bind's class value isn't implements element interface
             */
            javax.lang.model.util.Types types = environment.getTypeUtils();

            if (!types.isAssignable(types.getDeclaredType(typeElement), types.getDeclaredType(element))
                    && !types.isAssignable(typeElement.getSuperclass(), element.asType())) {
                environment.getMessager().printMessage(Diagnostic.Kind.ERROR
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

    private static boolean isAbstract(TypeElement typeElement) {
        Predicate<Element> predicate = MoreElements.hasModifiers(Modifier.ABSTRACT);

        return predicate.apply(typeElement);
    }
}
