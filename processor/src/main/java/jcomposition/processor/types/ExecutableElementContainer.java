package jcomposition.processor.types;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import java.util.Objects;

public class ExecutableElementContainer {
    private ExecutableElement executableElement;
    private ProcessingEnvironment env;

    public ExecutableElementContainer(ExecutableElement executableElement, ProcessingEnvironment env) {
        this.executableElement = executableElement;
        this.env = env;
    }

    public ExecutableElement getExecutableElement() {
        return executableElement;
    }

    public void setExecutableElement(ExecutableElement executableElement) {
        this.executableElement = executableElement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExecutableElementContainer that = (ExecutableElementContainer) o;

        if (Objects.equals(executableElement.getSimpleName(), that.executableElement.getSimpleName())) {
            return true;
        }

        Types types = env.getTypeUtils();
        TypeMirror t1 = types.erasure(executableElement.getReturnType());
        TypeMirror t2 = types.erasure(that.executableElement.getReturnType());

        return types.isAssignable(t1, t2) || types.isAssignable(t2, t1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(executableElement.getSimpleName(), executableElement.getParameters().size());
    }
}
