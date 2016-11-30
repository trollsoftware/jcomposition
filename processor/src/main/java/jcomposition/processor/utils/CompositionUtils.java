package jcomposition.processor.utils;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.squareup.javapoet.*;
import jcomposition.api.IComposition;

import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Inject;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.ArrayList;
import java.util.List;

public class CompositionUtils {

     public static TypeSpec getCompositionTypeSpec(TypeElement typeElement, ProcessingEnvironment env) {
         TypeSpec.Builder builder = TypeSpec.classBuilder("Composition");
         List<FieldSpec> fieldSpecs = getFieldsSpecs(typeElement, env);

         builder.addModifiers(Modifier.FINAL, Modifier.PROTECTED);

         if (fieldSpecs.size() > 0) {
             if (TypeElementUtils.hasInheritedInjectionAnnotation(typeElement)) {
                 String compositionName = TypeElementUtils.getCompositionName(typeElement, env.getElementUtils());

                 builder.addMethod(MethodSpec.constructorBuilder()
                         .addStatement(compositionName + ".this.onInject(this)")
                         .addModifiers(Modifier.PRIVATE)
                         .build());
             }

             builder.addFields(fieldSpecs);
         }

         return builder.build();
     }

    private static List<FieldSpec> getFieldsSpecs(TypeElement typeElement, ProcessingEnvironment env) {
        List<FieldSpec> specs = new ArrayList<FieldSpec>();

        for (TypeMirror typeInterface : typeElement.getInterfaces()) {
            TypeElement typeInterfaceElement = MoreTypes.asTypeElement(typeInterface);
            TypeElement bindClassType = TypeElementUtils.getBindClassType(typeInterfaceElement, env.getElementUtils());

            if (bindClassType == null)
                continue;

            boolean useInjection = TypeElementUtils.hasUseInjectionAnnotation(typeInterfaceElement)
                    || TypeElementUtils.hasUseInjectionAnnotation(bindClassType);

            List<?> typeArguments = MoreTypes.asDeclared(typeInterface).getTypeArguments();
            TypeMirror[] typeMirrors = new TypeMirror[typeArguments.size()];

            DeclaredType declaredType = env.getTypeUtils().getDeclaredType(bindClassType, typeArguments.toArray(typeMirrors));

            TypeName typeName = TypeName.get(declaredType);
            String initializer = useInjection ? "null" : "new " + typeName.toString() + "()";

            FieldSpec.Builder specBuilder = FieldSpec.builder(typeName, "composition_" + bindClassType.getSimpleName())
                    .addModifiers(Modifier.PROTECTED)
                    .initializer(initializer);

            if (useInjection) {
                specBuilder.addAnnotation(Inject.class);
            } else {
                specBuilder.addModifiers(Modifier.FINAL);
            }

            specs.add(specBuilder.build());
        }

        return specs;
    }

    public static ClassName getNestedCompositionClassName(TypeElement typeElement, Elements utils) {
        String name = TypeElementUtils.getCompositionName(typeElement, utils);
        ClassName nested = ClassName.get(MoreElements.getPackage(typeElement).toString(), name, "Composition");

        return nested;
    }

    public static TypeName getInheritedCompositionInterface(TypeElement typeElement, ProcessingEnvironment env) {
        ClassName composition = ClassName.get(IComposition.class);
        ClassName nested = getNestedCompositionClassName(typeElement, env.getElementUtils());

        return ParameterizedTypeName.get(composition, nested);
    }

    public static FieldSpec getCompositeFieldSpec(TypeElement typeElement) {
        TypeName compositionTypeName = TypeVariableName.get("Composition");

        return FieldSpec.builder(compositionTypeName, "_composition")
                .addModifiers(Modifier.FINAL, Modifier.PRIVATE)
                .initializer("new " + compositionTypeName.toString()  +"()")
                .build();
    }

    public static MethodSpec getCompositeMethodSpec(TypeElement typeElement, ProcessingEnvironment env) {
        return MethodSpec.methodBuilder("getComposition")
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .returns(TypeVariableName.get("Composition"))
                .addAnnotation(Override.class)
                .addStatement("return this._composition")
                .build();
    }
}
