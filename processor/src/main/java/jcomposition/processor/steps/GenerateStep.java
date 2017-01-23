/*
 * Copyright 2016 Alexander Shitikov (a.shitikov73@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information visit: http://github.com/trollsoftware/trollcore
 */

package jcomposition.processor.steps;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.auto.common.SuperficialValidation;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.squareup.javapoet.*;
import jcomposition.api.annotations.Composition;
import jcomposition.processor.types.ExecutableElementContainer;
import jcomposition.processor.types.TypeElementPairContainer;
import jcomposition.processor.utils.AnnotationUtils;
import jcomposition.processor.utils.CompositionUtils;
import jcomposition.processor.utils.TypeElementUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;

public class GenerateStep extends AbstractStep {
    private boolean isAbstract = false;

    public GenerateStep(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public Set<? extends Class<? extends Annotation>> annotations() {
        return Sets.newHashSet(Composition.class);
    }

    @Override
    public Set<Element> process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
        ImmutableSet.Builder<Element> elementsForLaterProcessing = ImmutableSet.builder();
        for (Map.Entry<Class<? extends Annotation>, Element> entry : elementsByAnnotation.entries()) {
            if (!entry.getValue().getKind().isInterface()) {
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, entry.getKey().getName()
                        + " annotation can be used only with interfaces", entry.getValue());
            }

            Element value = entry.getValue();

            if (validate(value)) {
                generate(MoreElements.asType(value));
            } else {
                elementsForLaterProcessing.add(value);
            }
        }

        return elementsForLaterProcessing.build();
    }

    private boolean validate(Element element) {
        TypeElement typeElement = MoreElements.asType(element);

        for (TypeMirror superInterface : typeElement.getInterfaces()) {
            if (!validateInterface(superInterface)) {
                return false;
            }
        }

        return true;
    }

    private boolean validateInterface(TypeMirror typeMirror) {
        TypeElement bindClassType = AnnotationUtils.getBindClassType(MoreTypes.asTypeElement(typeMirror), getProcessingEnv());
        if (bindClassType == null) {
            return true;
        }

        if (!SuperficialValidation.validateElement(bindClassType)) {
            return false;
        }

        return true;
    }

    private void generate(TypeElement typeElement) {
        TypeSpec typeSpec = getTypeSpec(typeElement);

        JavaFile javaFile = JavaFile.builder(MoreElements.getPackage(typeElement).toString(), typeSpec).build();

        try {
            javaFile.writeTo(getProcessingEnv().getFiler());
        } catch (IOException e) {
            getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }

    private List<TypeVariableName> getTypeParameters(TypeElement typeElement) {
        List<TypeVariableName> typeSpecs = new ArrayList<TypeVariableName>();

        for (TypeParameterElement typeParameterElement : typeElement.getTypeParameters()) {
            typeSpecs.add(TypeVariableName.get(typeParameterElement));
        }

        return typeSpecs;
    }

    private TypeSpec getTypeSpec(TypeElement typeElement) {
        Map<ExecutableElementContainer, List<TypeElementPairContainer>> methodsMap = TypeElementUtils.getMethodsMap(typeElement, getProcessingEnv());

        String compositionName = AnnotationUtils.getCompositionName(typeElement, getProcessingEnv());
        Composition.MergeConflictPolicy mergeConflictPolicy = AnnotationUtils.getCompositionMergeConflictPolicy(
                typeElement, getProcessingEnv());

        TypeSpec.Builder specBuilder = TypeSpec.classBuilder(compositionName)
                .addSuperinterface(TypeName.get(typeElement.asType()))
                .addSuperinterface(CompositionUtils.getInheritedCompositionInterface(typeElement, getProcessingEnv()))
                .addTypeVariables(getTypeParameters(typeElement))
                .addModifiers(Modifier.PUBLIC)
                .addType(CompositionUtils.getCompositionTypeSpec(methodsMap, typeElement, getProcessingEnv()))
                .addMethods(getMethodSpecs(methodsMap, typeElement, mergeConflictPolicy))
                .addMethod(CompositionUtils.getCompositeMethodSpec(typeElement, getProcessingEnv()))
                .addField(CompositionUtils.getCompositeFieldSpec(typeElement));

        if (AnnotationUtils.hasInheritedInjectionAnnotation(typeElement)) {
            isAbstract = true;

            ClassName nestedCompositionClassName = CompositionUtils.getNestedCompositionClassName(typeElement, getProcessingEnv());
            TypeName nestedCompositionTypeClassName = TypeVariableName.get(nestedCompositionClassName.simpleName());

            specBuilder.addMethod(MethodSpec.methodBuilder("onInject")
                    .addModifiers(Modifier.ABSTRACT, Modifier.PROTECTED)
                    .addParameter(ParameterSpec.builder(nestedCompositionTypeClassName, "composition", Modifier.FINAL)
                            .build())
                    .build());
        }

        if (isAbstract) {
            specBuilder.addModifiers(Modifier.ABSTRACT);
        }

        return specBuilder.build();
    }

    private MethodSpec getMethodSpec(Map.Entry<ExecutableElementContainer, List<TypeElementPairContainer>> entry, TypeElement typeElement, Composition.MergeConflictPolicy policy) {
        ExecutableElementContainer executableContainer = entry.getKey();
        ExecutableElement executableElement = executableContainer.getExecutableElement();
        List<TypeElementPairContainer> overriders = entry.getValue();

        if (overriders.size() == 0)
            return null;

        boolean hasMergeConflict = (overriders.size() > 1);

        if (hasMergeConflict && policy == Composition.MergeConflictPolicy.MakeAbstract) {
            return null;
        }

        /**
         * FIXME: maybe take the first element is not exactly correct?
         */
        TypeElementPairContainer container = overriders.get(0);

        DeclaredType declaredType = container.getDeclaredType();
        MethodSpec.Builder builder = MethodSpecUtils.getBuilder(executableElement, declaredType, getProcessingEnv().getTypeUtils());

        if (container.getRelationShip() == TypeElementPairContainer.ExecutableRelationShip.Overriding
                || container.getRelationShip() == TypeElementPairContainer.ExecutableRelationShip.Same) {
            builder.addAnnotation(Override.class);
        }

        if (!executableContainer.isAbstract()) {
            boolean useFirst = executableElement.getReturnType().getKind() != TypeKind.VOID
                    || (hasMergeConflict && policy == Composition.MergeConflictPolicy.UseFirst);

            for (TypeElementPairContainer overrider : overriders) {
                String statement = getExecutableStatement(executableElement, overrider.getBind());

                if (statement != null) {
                    builder.addStatement(statement);
                }
                if (useFirst) {
                    break;
                }
            }
        } else {
            builder.addModifiers(Modifier.ABSTRACT);
        }

        return builder.build();
    }

    private List<MethodSpec> getMethodSpecs(Map<ExecutableElementContainer, List<TypeElementPairContainer>> methodsMap, TypeElement typeElement, Composition.MergeConflictPolicy policy) {
        List<MethodSpec> result = new ArrayList<MethodSpec>();

        for (Map.Entry<ExecutableElementContainer, List<TypeElementPairContainer>> entry : methodsMap.entrySet()) {
            MethodSpec spec = getMethodSpec(entry, typeElement, policy);

            if (spec != null) {
                result.add(spec);
            } else {
                isAbstract = true;
            }
        }

        return result;
    }

    private String getParametersScope(ExecutableElement element) {
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

    private String getExecutableStatement(ExecutableElement executableElement, TypeElement overrider) {
        StringBuilder builder = new StringBuilder();

        if (executableElement.getReturnType().getKind() != TypeKind.VOID) {
            builder.append("return ");
        }

        builder.append("getComposition().composition_" + overrider.getSimpleName()
                + "._super_" + executableElement.getSimpleName() + "(" + getParametersScope(executableElement) + ")");

        return builder.toString();
    }
}
