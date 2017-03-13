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
import com.squareup.javapoet.*;
import jcomposition.api.IComposition;
import jcomposition.processor.types.ExecutableElementContainer;
import jcomposition.processor.types.TypeElementPairContainer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Inject;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CompositionUtils {

     public static TypeSpec getCompositionTypeSpec(Map<ExecutableElementContainer,
             List<TypeElementPairContainer>> methodsMap, TypeElement typeElement, ProcessingEnvironment env) {
         TypeSpec.Builder builder = TypeSpec.classBuilder("Composition");
         builder.addModifiers(Modifier.FINAL, Modifier.PUBLIC);

         HashMap<FieldSpec, TypeSpec> fieldSpecs = getFieldsSpecs(methodsMap, typeElement, env);
         if (fieldSpecs.isEmpty()) return builder.build();

         if (AnnotationUtils.hasInheritedInjectionAnnotation(typeElement)) {
             builder.addMethod(MethodSpec.constructorBuilder()
                     .addStatement(AnnotationUtils.getCompositionName(typeElement, env) + ".this.onInject(this)")
                     .addModifiers(Modifier.PRIVATE)
                     .build());
         }
         for (Map.Entry<FieldSpec, TypeSpec> entry : fieldSpecs.entrySet()) {
             builder.addField(entry.getKey());
             builder.addType(entry.getValue());
         }
         return builder.build();
     }

    private static HashMap<FieldSpec, TypeSpec> getFieldsSpecs(Map<ExecutableElementContainer,
            List<TypeElementPairContainer>> methodsMap, TypeElement typeElement, ProcessingEnvironment env) {
        String compositionName = AnnotationUtils.getCompositionName(typeElement, env);
        HashMap<TypeElementPairContainer, TypeSpec.Builder> typeBuilders = new HashMap<TypeElementPairContainer, TypeSpec.Builder>();
        HashMap<FieldSpec, TypeSpec> specs = new HashMap<FieldSpec, TypeSpec>();

        for (Map.Entry<ExecutableElementContainer, List<TypeElementPairContainer>> entry : methodsMap.entrySet()) {
            if (entry.getValue().isEmpty()) continue;

            for (TypeElementPairContainer eContainer : entry.getValue()) {
                TypeSpec.Builder tBuilder = typeBuilders.get(eContainer);
                if (tBuilder == null) {
                    tBuilder = getFieldTypeBuilder(eContainer, env);
                }
                tBuilder.addMethods(getShareMethodSpecs(eContainer, entry, compositionName, env));
                typeBuilders.put(eContainer, tBuilder);
            }
        }
        for (Map.Entry<TypeElementPairContainer, TypeSpec.Builder> entry : typeBuilders.entrySet()) {
            TypeSpec typeSpec = entry.getValue().build();
            specs.put(getFieldSpec(entry.getKey(), typeSpec), typeSpec);
        }
        return specs;
    }

    private static FieldSpec getFieldSpec(TypeElementPairContainer elementContainer, TypeSpec typeSpec) {
        String initializer = elementContainer.hasUseInjection() ? "null" : "new " + typeSpec.name + "()";
        FieldSpec.Builder specBuilder = FieldSpec.builder(ClassName.bestGuess(typeSpec.name),
                "composition_" + elementContainer.getBind().getSimpleName())
                .addModifiers(Modifier.PROTECTED)
                .initializer(initializer);
        if (elementContainer.hasUseInjection()) {
            specBuilder.addAnnotation(ClassName.get(Inject.class));
        }
        return specBuilder.build();
    }

    private static TypeSpec.Builder getFieldTypeBuilder(TypeElementPairContainer container, ProcessingEnvironment env) {
        DeclaredType baseDt = container.getDeclaredType();
        TypeElement bindClassType = container.getBind();
        TypeElement concreteIntf = container.getIntf();
        boolean isInjected = container.hasUseInjection();

        DeclaredType dt = Util.getDeclaredType(baseDt, concreteIntf, bindClassType, env);
        return TypeSpec.classBuilder("Composition_" + bindClassType.getSimpleName())
                .addModifiers(Modifier.FINAL, isInjected ? Modifier.PUBLIC : Modifier.PROTECTED)
                .superclass(TypeName.get(dt));
    }

    private static List<MethodSpec> getShareMethodSpecs(TypeElementPairContainer tContainer, Map.Entry<ExecutableElementContainer, List<TypeElementPairContainer>> entry, String compositionName, ProcessingEnvironment env) {
        List<MethodSpec> result = new ArrayList<MethodSpec>();

        ExecutableElement executableElement = entry.getKey().getExecutableElement();
        DeclaredType declaredType = entry.getKey().getDeclaredType();

        MethodSpec.Builder builder = MethodSpec.overriding(executableElement, declaredType, env.getTypeUtils());
        String statement = getShareExecutableStatement(executableElement, compositionName + ".this");

        builder.addStatement(statement);
        MethodSpec spec = builder.build();

        result.add(spec);

        if (tContainer.isAbstract()) {
            return result;
        }

        MethodSpec.Builder _builder = MethodSpec.methodBuilder("_super_" + executableElement.getSimpleName().toString())
                .addModifiers(Modifier.PROTECTED)
                .addParameters(spec.parameters)
                .returns(spec.returnType);
        String _statement = getShareExecutableStatement(executableElement);

        _builder.addStatement(_statement);
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
