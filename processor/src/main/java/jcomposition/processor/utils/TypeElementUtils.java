package jcomposition.processor.utils;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.auto.common.Visibility;
import jcomposition.processor.types.ExecutableElementContainer;
import jcomposition.processor.types.RelationShipResult;
import jcomposition.processor.types.TypeElementPairContainer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import java.util.*;

import static jcomposition.processor.utils.Util.addValueToMapList;

public final class TypeElementUtils {

    /**
     * Get Map of public and protected methods with @ShareProtected annotation.
     * Map entry represent format like [{@link Visibility#PUBLIC} - {@link List&lt;ExecutableElement&gt;}]
     * @see {@link jcomposition.api.annotations.ShareProtected}
     * @param element element from methods will be extracted
     * @param env {@link ProcessingEnvironment}
     * @return Map
     */
    private static Map<Visibility, ? extends List<ExecutableElement>> getVisibleExecutableElements(TypeElement element, ProcessingEnvironment env) {
        HashMap<Visibility, List<ExecutableElement>> map = new HashMap<Visibility, List<ExecutableElement>>();

        // Initialize
        for (Visibility v : Visibility.values()) {
            addValueToMapList(v, null, map);
        }

        List<? extends Element> allMembers = env
                .getElementUtils()
                .getAllMembers(element);

        boolean shareProtected = AnnotationUtils.hasShareProtectedAnnotation(element);

        // Get only public and protected methods with @ShareProtected annotation.
        for (Element member : allMembers) {
            if (member.getKind() != ElementKind.METHOD)
                continue;

            // Skip methods from link java.lang.Object
            if (member.getEnclosingElement().equals(env.getElementUtils().getTypeElement(Object.class.getName()))) {
                continue;
            }

            ExecutableElement executableMember = MoreElements.asExecutable(member);
            Set<Modifier> modifiers = executableMember.getModifiers();

            // skip non valid methods
            if (modifiers.contains(Modifier.FINAL) || modifiers.contains(Modifier.STATIC))
                continue;

            if (modifiers.contains(Modifier.PROTECTED)) {
                if (shareProtected || AnnotationUtils.hasShareProtectedAnnotation(executableMember)) {
                    addValueToMapList(Visibility.PROTECTED, executableMember, map);
                }
            } else if (modifiers.contains(Modifier.PUBLIC)) {
                addValueToMapList(Visibility.PUBLIC, executableMember, map);
            }
        }

        return map;
    }


    /**
     * Finds method in executableElements and gets a relationship between them
     * @param method
     * @param executableElements
     * @param containing
     * @param env
     * @return relationship
     */
    private static RelationShipResult findRelation(ExecutableElement method,
                                                   List<ExecutableElement> executableElements,
                                                   TypeElement containing, ProcessingEnvironment env) {
        boolean found = false;
        TypeElementPairContainer.ExecutableRelationShip relationShip = TypeElementPairContainer.ExecutableRelationShip.Nothing;

        for (ExecutableElement element : executableElements) {
            if (env.getElementUtils().overrides(element, method, containing)) {
                relationShip = TypeElementPairContainer.ExecutableRelationShip.Overriding;
                found = true;
            } else if (env.getElementUtils().hides(element, method)) {
                relationShip = TypeElementPairContainer.ExecutableRelationShip.Hiding;
                found = true;
            }

            if (found) {
                break;
            }
        }

        return new RelationShipResult(found, relationShip);
    }

    private static void processElements(List<ExecutableElement> elementsFromInterface,
                                        List<ExecutableElement> elementsFromBind,
                                        TypeElement baseIntf,
                                        TypeElement concreteIntf,
                                        TypeElement bind,
                                        Map<ExecutableElementContainer, List<TypeElementPairContainer>> map,
                                        boolean intfInjected,
                                        ProcessingEnvironment env) {
        DeclaredType baseDt = MoreTypes.asDeclared(baseIntf.asType());
        for (ExecutableElement method : elementsFromInterface) {
            ExecutableElementContainer container = new ExecutableElementContainer(method, env.getTypeUtils());
            RelationShipResult result = findRelation(method, elementsFromBind, bind, env);

            TypeElementPairContainer typeElementPairContainer = new TypeElementPairContainer(concreteIntf, bind, baseDt, intfInjected, result.getRelationShip());

            addValueToMapList(container, null, map);

            if (result.isDuplicateFound()) {
                addValueToMapList(container, typeElementPairContainer, map);
            }
        }

        // Priority to interface's methods.
        if (elementsFromInterface.size() > 0)
            return;

        DeclaredType concreteDt = Util.getDeclaredType(baseDt, concreteIntf, bind, env);
        for (ExecutableElement method : elementsFromBind) {
            ExecutableElementContainer container = new ExecutableElementContainer(method, env.getTypeUtils());
            TypeElementPairContainer typeElementPairContainer = new TypeElementPairContainer(concreteIntf, bind, concreteDt, intfInjected, TypeElementPairContainer.ExecutableRelationShip.Nothing);

            addValueToMapList(container, typeElementPairContainer, map);
        }
    }

    /**
     * Get list of executable elements and list of overriders
     * @param element
     * @param env
     * @return map
     */
    public static Map<ExecutableElementContainer, List<TypeElementPairContainer>> getMethodsMap(TypeElement element, ProcessingEnvironment env) {
        HashMap<ExecutableElementContainer, List<TypeElementPairContainer>> map = new HashMap<ExecutableElementContainer, List<TypeElementPairContainer>>();

        Map<Visibility, ? extends List<ExecutableElement>> executableElements = getVisibleExecutableElements(element, env);
        for (TypeMirror mirror : element.getInterfaces()) {
            TypeElement typeInterfaceElement = MoreTypes.asTypeElement(mirror);
            TypeElement bindClassType = AnnotationUtils.getBindClassType(typeInterfaceElement, env);

            if (bindClassType == null)
                continue;

            boolean hasInjectAnnotation = AnnotationUtils.hasUseInjectionAnnotation(typeInterfaceElement);

            Map<Visibility, ? extends List<ExecutableElement>> executableElementBind = getVisibleExecutableElements(bindClassType, env);

            processElements(executableElements.get(Visibility.PUBLIC),
                    executableElementBind.get(Visibility.PUBLIC),
                    element, typeInterfaceElement, bindClassType, map, hasInjectAnnotation, env);

            processElements(Collections.<ExecutableElement>emptyList(),
                    executableElementBind.get(Visibility.PROTECTED),
                    element, typeInterfaceElement, bindClassType, map, hasInjectAnnotation, env);

        }

        return map;
    }
}
