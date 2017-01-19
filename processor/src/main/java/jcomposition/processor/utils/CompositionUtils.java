package jcomposition.processor.utils;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.squareup.javapoet.*;
import jcomposition.api.IComposition;
import jcomposition.processor.types.ExecutableElementContainer;
import jcomposition.processor.types.TypeElementContainer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Inject;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class CompositionUtils {

     public static TypeSpec getCompositionTypeSpec(Map<ExecutableElementContainer,
             List<TypeElementContainer>> methodsMap, TypeElement typeElement, ProcessingEnvironment env) {
         TypeSpec.Builder builder = TypeSpec.classBuilder("Composition");
         HashMap<FieldSpec, TypeSpec> fieldSpecs = getFieldsSpecs(methodsMap, typeElement, env);

         builder.addModifiers(Modifier.FINAL, Modifier.PUBLIC);

         if (fieldSpecs.size() > 0) {
             if (AnnotationUtils.hasInheritedInjectionAnnotation(typeElement)) {
                    builder.addMethod(MethodSpec.constructorBuilder()
                         .addStatement(AnnotationUtils.getCompositionName(typeElement, env) + ".this.onInject(this)")
                         .addModifiers(Modifier.PRIVATE)
                         .build());
             }
             for (Map.Entry<FieldSpec, TypeSpec> entry : fieldSpecs.entrySet()) {
                 builder.addField(entry.getKey());
                 if (entry.getValue() != null) {
                     builder.addType(entry.getValue());
                 }
             }
         }

         return builder.build();
     }

    private static HashMap<FieldSpec, TypeSpec> getFieldsSpecs(Map<ExecutableElementContainer,
            List<TypeElementContainer>> methodsMap, TypeElement typeElement, ProcessingEnvironment env) {
        String compositionName = AnnotationUtils.getCompositionName(typeElement, env);

        HashMap<TypeElementContainer, TypeSpec.Builder> typeBuilders = new HashMap<TypeElementContainer, TypeSpec.Builder>();
        for (Map.Entry<ExecutableElementContainer, List<TypeElementContainer>> entry : methodsMap.entrySet()) {
            if (entry.getValue().isEmpty()) continue;

            for (TypeElementContainer eContainer : entry.getValue()) {
                TypeSpec.Builder tBuilder = typeBuilders.get(eContainer);
                if (tBuilder == null) {
                    tBuilder = getFieldTypeBuilder(eContainer.getTypeElement(), eContainer.hasUseInjection());
                }
                tBuilder.addMethods(getShareMethodSpecs(entry, compositionName, env));
                typeBuilders.put(eContainer, tBuilder);
            }
        }

        HashMap<FieldSpec, TypeSpec> specs = new HashMap<FieldSpec, TypeSpec>();

        for (Map.Entry<TypeElementContainer, TypeSpec.Builder> entry : typeBuilders.entrySet()) {
            TypeSpec typeSpec = entry.getValue().build();
            String initializer = entry.getKey().hasUseInjection() ? "null" : "new " + typeSpec.name + "()";
            FieldSpec.Builder specBuilder = FieldSpec.builder(ClassName.bestGuess(typeSpec.name),
                    "composition_" + entry.getKey().getTypeElement().getSimpleName())
                    .addModifiers(Modifier.PROTECTED)
                    .initializer(initializer);
            if (entry.getKey().hasUseInjection()) {
                specBuilder.addAnnotation(ClassName.get(Inject.class));
            }
            specs.put(specBuilder.build(), typeSpec);
        }

        return specs;
    }

    private static TypeSpec.Builder getFieldTypeBuilder(TypeElement bindClassType, boolean isInjected) {
        return TypeSpec.classBuilder("Composition_" + bindClassType.getSimpleName())
                .addModifiers(Modifier.FINAL, isInjected ? Modifier.PUBLIC : Modifier.PROTECTED)
                .superclass(TypeName.get(bindClassType.asType()));
    }

    private static List<MethodSpec> getShareMethodSpecs(Map.Entry<ExecutableElementContainer, List<TypeElementContainer>> entry, String compositionName, ProcessingEnvironment env) {
        List<MethodSpec> result = new ArrayList<MethodSpec>();

        ExecutableElement executableElement = entry.getKey().getExecutableElement();
        DeclaredType declaredType = entry.getValue().get(0).getDeclaredType();

        MethodSpec.Builder builder = MethodSpec.overriding(executableElement, declaredType, env.getTypeUtils());
        String statement = getShareExecutableStatement(executableElement, compositionName + ".this");

        builder.addStatement(statement);
        MethodSpec spec = builder.build();

        MethodSpec.Builder _builder = MethodSpec.methodBuilder("_super_" + executableElement.getSimpleName().toString())
                .addModifiers(Modifier.PROTECTED)
                .addParameters(spec.parameters)
                .returns(spec.returnType);
        String _statement = getShareExecutableStatement(executableElement);

        _builder.addStatement(_statement);
        result.add(spec);
        result.add(_builder.build());
        return result;
    }

    private static String getShareExecutableStatement(ExecutableElement executableElement) {
        return getShareExecutableStatement(executableElement, "super");
    }

    private static String getShareExecutableStatement(ExecutableElement executableElement, String className) {
        StringBuilder builder = new StringBuilder();

        if (executableElement.getReturnType().getKind() != TypeKind.VOID) {
            builder.append("return ");
        }
        builder.append(String.format("%s.%s(%s)", className, executableElement.getSimpleName(),
                getParametersScope(executableElement)));

        return builder.toString();
    }

    private static String getParametersScope(ExecutableElement element) {
        StringBuilder paramBuilder = new StringBuilder();
        List<? extends VariableElement> parameters = element.getParameters();

        for (int i = 0; i < parameters.size(); i++) {
            VariableElement variableElement = parameters.get(i);

            paramBuilder.append(variableElement.getSimpleName());

            if (i < parameters.size() - 1) {
                paramBuilder.append(", ");
            }
        }

        return paramBuilder.toString();
    }

    public static ClassName getNestedCompositionClassName(TypeElement typeElement, ProcessingEnvironment env) {
        String name = AnnotationUtils.getCompositionName(typeElement, env);
        ClassName nested = ClassName.get(MoreElements.getPackage(typeElement).toString(), name, "Composition");

        return nested;
    }

    public static TypeName getInheritedCompositionInterface(TypeElement typeElement, ProcessingEnvironment env) {
        ClassName composition = ClassName.get(IComposition.class);
        ClassName nested = getNestedCompositionClassName(typeElement, env);

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
