package jcomposition.processor.utils;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.squareup.javapoet.*;
import jcomposition.api.IComposition;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

public class CompositionUtil {
     public static TypeSpec getCompositionTypeSpec(TypeElement typeElement, ProcessingEnvironment env) {
         TypeSpec.Builder builder = TypeSpec.classBuilder("Composition");
         List<FieldSpec> fieldSpecs = getFieldsSpecs(typeElement, env);

         builder.addModifiers(Modifier.FINAL, Modifier.PROTECTED);

         if (fieldSpecs.size() > 0) {
             builder.addFields(fieldSpecs);
         }

         return builder.build();
     }

    private static List<FieldSpec> getFieldsSpecs(TypeElement typeElement, ProcessingEnvironment env) {
        List<FieldSpec> specs = new ArrayList<FieldSpec>();

        for (TypeMirror typeInterface : typeElement.getInterfaces()) {
            TypeElement bindClassType = TypeElementUtils.getBindClassType(MoreTypes.asTypeElement(typeInterface), env.getElementUtils());

            if (bindClassType == null)
                continue;

            List<?> typeArguments = MoreTypes.asDeclared(typeInterface).getTypeArguments();
            TypeMirror[] typeMirrors = new TypeMirror[typeArguments.size()];

            DeclaredType declaredType = env.getTypeUtils().getDeclaredType(bindClassType, typeArguments.toArray(typeMirrors));

            TypeName typeName = TypeName.get(declaredType);

            FieldSpec spec = FieldSpec.builder(typeName, "composition_" + bindClassType.getSimpleName())
                    .addModifiers(Modifier.FINAL, Modifier.PROTECTED)
                    .initializer("new " + typeName.toString() + "()")
                    .build();

            specs.add(spec);
        }

        return specs;
    }

    private static ClassName getNestedCompositionClassName(TypeElement typeElement) {
        // TODO: name
        ClassName nested = ClassName.get(MoreElements.getPackage(typeElement).toString()
                , typeElement.getSimpleName().toString() + "_Generated", "Composition");

        return nested;
    }

    public static TypeName getInheritedCompositionInterface(TypeElement typeElement, ProcessingEnvironment env) {
        ClassName composition = ClassName.get(IComposition.class);
        ClassName nested = getNestedCompositionClassName(typeElement);

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
