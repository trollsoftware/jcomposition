package com.squareup.javapoet;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Types;
import java.util.*;

import static com.squareup.javapoet.Util.checkNotNull;
import static jcomposition.processor.utils.Util.modifiersToArray;

public final class MethodSpecUtils {

    /**
     * This is a copy of {@link MethodSpec#overriding(ExecutableElement, DeclaredType, Types)}
     * but without adding an {@link Override} annotation
     * @param method
     * @param enclosing
     * @param types
     * @return Builder
     */
    public static MethodSpec.Builder getBuilder(ExecutableElement method, DeclaredType enclosing, Types types) {
        checkNotNull(method, "method == null");

        ExecutableType executableType = (ExecutableType) types.asMemberOf(enclosing, method);
        List<? extends TypeMirror> resolvedParameterTypes = executableType.getParameterTypes();
        TypeMirror resolvedReturnType = executableType.getReturnType();

        Set<Modifier> modifiers = method.getModifiers();
        if (modifiers.contains(Modifier.PRIVATE)
                || modifiers.contains(Modifier.FINAL)
                || modifiers.contains(Modifier.STATIC)) {
            throw new IllegalArgumentException("cannot override method with modifiers: " + modifiers);
        }

        String methodName = method.getSimpleName().toString();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName);

        modifiers = new LinkedHashSet<Modifier>(modifiers);
        modifiers.remove(Modifier.ABSTRACT);
        modifiers.remove(Util.DEFAULT); // LinkedHashSet permits null as element for Java 7
        methodBuilder.addModifiers(modifiers);

        for (TypeParameterElement typeParameterElement : method.getTypeParameters()) {
            TypeVariable var = (TypeVariable) typeParameterElement.asType();
            methodBuilder.addTypeVariable(TypeVariableName.get(var));
        }

        methodBuilder.returns(TypeName.get(resolvedReturnType));

        for (int i = 0; i < resolvedParameterTypes.size(); i++) {
            TypeMirror paramTypeMirror = resolvedParameterTypes.get(i);
            VariableElement parameter = method.getParameters().get(i);

            TypeName name = TypeName.get(paramTypeMirror);
            ParameterSpec spec = ParameterSpec.builder(name, parameter.getSimpleName().toString())
                    // Compiler clashes with addModifiers(Modifiers...) and addModifiers(Iterable<Modifiers>)
                    // Convert it to array
                    .addModifiers(modifiersToArray(parameter.getModifiers()))
                    .build();

            methodBuilder.addParameter(spec);
        }
        methodBuilder.varargs(method.isVarArgs());

        for (TypeMirror thrownType : method.getThrownTypes()) {
            methodBuilder.addException(TypeName.get(thrownType));
        }

        return methodBuilder;
    }
}
