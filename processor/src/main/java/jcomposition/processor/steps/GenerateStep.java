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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.squareup.javapoet.*;
import jcomposition.api.annotations.Composition;
import jcomposition.processor.utils.CompositionUtils;
import jcomposition.processor.utils.TypeElementUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
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
        for (Map.Entry<Class<? extends Annotation>, Element> entry : elementsByAnnotation.entries()) {
            if (!entry.getValue().getKind().isInterface()) {
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, entry.getKey().getName()
                        + " annotation can be used only with interfaces", entry.getValue());
            }

            generate(MoreElements.asType(entry.getValue()));
        }

        return Collections.emptySet();
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
        ImmutableSet<ExecutableElement> methods = MoreElements.getLocalAndInheritedMethods(typeElement,
                getProcessingEnv().getElementUtils());

        String compositionName = TypeElementUtils.getCompositionName(typeElement, getProcessingEnv().getElementUtils());

        TypeSpec.Builder specBuilder = TypeSpec.classBuilder(compositionName)
                .addSuperinterface(TypeName.get(typeElement.asType()))
                .addSuperinterface(CompositionUtils.getInheritedCompositionInterface(typeElement, getProcessingEnv()))
                .addTypeVariables(getTypeParameters(typeElement))
                .addModifiers(Modifier.PUBLIC)
                .addType(CompositionUtils.getCompositionTypeSpec(typeElement, getProcessingEnv()))
                .addMethods(getMethodSpecs(methods, typeElement))
                .addMethod(CompositionUtils.getCompositeMethodSpec(typeElement, getProcessingEnv()))
                .addField(CompositionUtils.getCompositeFieldSpec(typeElement));

        if (TypeElementUtils.hasInheritedInjectionAnnotation(typeElement)) {
            isAbstract = true;

            ClassName nestedCompositionClassName = CompositionUtils.getNestedCompositionClassName(typeElement, getProcessingEnv().getElementUtils());
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

    private MethodSpec getMethodSpec(ExecutableElement executableElement, TypeElement typeElement) {
        DeclaredType declaredType = MoreTypes.asDeclared(typeElement.asType());

        MethodSpec.Builder builder = MethodSpec.overriding(executableElement, declaredType, getProcessingEnv().getTypeUtils());

        List<TypeElement> overriders = getListOverridersExecutable(executableElement, typeElement);

        if (overriders.size() == 0)
            return null;

        boolean returnVoid = executableElement.getReturnType().getKind() == TypeKind.VOID;

        for (TypeElement overrider : overriders) {
            String statement = getExecutableStatement(executableElement, overrider);

            if (statement != null) {
                builder.addStatement(statement);
            }
            if (!returnVoid) {
                break;
            }
        }

        return builder.build();
    }

    private List<MethodSpec> getMethodSpecs(Iterable<ExecutableElement> elements, TypeElement typeElement) {
        List<MethodSpec> result = new ArrayList<MethodSpec>();

        for (ExecutableElement element : elements) {
            MethodSpec spec = getMethodSpec(element, typeElement);

            if (spec != null) {
                result.add(spec);
            } else {
                isAbstract = true;
            }
        }

        return result;
    }

    private List<TypeElement> getListOverridersExecutable(ExecutableElement executableElement, TypeElement typeElement) {
        List<TypeElement> overriders = new ArrayList<TypeElement>();

        for (TypeMirror typeMirror : typeElement.getInterfaces()) {
            TypeElement currentTypeElement = MoreTypes.asTypeElement(typeMirror);
            List<? extends Element> allMembers = getProcessingEnv()
                    .getElementUtils()
                    .getAllMembers(currentTypeElement);
            List<? extends ExecutableElement> methods = ElementFilter.methodsIn(allMembers);

            if (methods.contains(executableElement)) {
                overriders.add(currentTypeElement);
            }
        }

        return overriders;
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
        TypeElement bindClassType = TypeElementUtils.getBindClassType(overrider, getProcessingEnv());

        if (bindClassType == null) {
            return null;
        }

        if (executableElement.getReturnType().getKind() != TypeKind.VOID) {
            builder.append("return ");
        }

        builder.append("getComposition().composition_" + bindClassType.getSimpleName()
                + "." + executableElement.getSimpleName() + "(" + getParametersScope(executableElement) + ")");

        return builder.toString();
    }
}
