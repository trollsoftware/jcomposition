package jcomposition.processor.types;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.Types;
import java.util.Objects;

public class ExecutableElementContainer {
    private ExecutableElement executableElement;
    private Types types;

    public ExecutableElementContainer(ExecutableElement executableElement, Types types) {
        this.executableElement = executableElement;
        this.types = types;
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
        return Objects.equals(executableElement.getSimpleName(), that.executableElement.getSimpleName())
                && types.isSameType(executableElement.getReturnType(), that.executableElement.getReturnType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(executableElement.getSimpleName(), executableElement.getReturnType());
    }
}
