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
import com.google.auto.common.MoreTypes;
import com.google.auto.common.Visibility;
import jcomposition.api.types.ExecutableRelationShip;
import jcomposition.api.types.IExecutableElementContainer;
import jcomposition.api.types.IRelationShipResult;
import jcomposition.api.types.ITypeElementPairContainer;
import jcomposition.processor.types.ExecutableElementContainer;
import jcomposition.processor.types.RelationShipResult;
import jcomposition.processor.types.TypeElementPairContainer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import java.util.*;

import static jcomposition.processor.utils.Util.addValueToMapList;
import static jcomposition.processor.utils.Util.isAbstract;
import static jcomposition.processor.utils.Util.isFinal;

public final class TypeElementUtils {

    /**
     * Get Map of public and protected methods.
     * Map entry represent format like [{@link Visibility#PUBLIC} - {@link List&lt;ExecutableElement&gt;}]
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
                addValueToMapList(Visibility.PROTECTED, executableMember, map);
            } else if (modifiers.contains(Modifier.PUBLIC)) {
                addValueToMapList(Visibility.PUBLIC, executableMember, map);
            }
        }

        return map;
    }

    private static ExecutableRelationShip findRelation(ExecutableElement overrider,
                                                       ExecutableElement overridden,
                                                       TypeElement containing, ProcessingEnvironment env) {
        Elements els = env.getElementUtils();

        // FIXME: Does Same really need?
        if (overrider.equals(overridden)) {
            return ExecutableRelationShip.Same;
        } else if (els.overrides(overrider, overridden, containing)) {
            if (isAbstract(overrider)) {
                return ExecutableRelationShip.OverridingAbstract;
            }
            return ExecutableRelationShip.Overriding;
        } else if (els.hides(overrider, overridden)) {
            return ExecutableRelationShip.Hiding;
        }

        return null;
    }


    /**
     * Finds method in executableElements and gets a relationship between them
     * @param method method
     * @param executableElements list of methods
     * @param containing type element that contains method
     * @param env annotation processing environment
     * @return relationship
     */
    private static IRelationShipResult findRelation(ExecutableElement method,
                                                    List<ExecutableElement> executableElements,
                                                    TypeElement containing,
                                                    boolean reverse,
                                                    ProcessingEnvironment env) {
        boolean found = false;
        ExecutableRelationShip relationShip = ExecutableRelationShip.Nothing;
        for (ExecutableElement element : executableElements) {
            ExecutableRelationShip foundRS = (reverse ?
                    findRelation(method, element, containing, env) :
                    findRelation(element, method, containing, env));
            found = (foundRS != null);

            if (found) {
                relationShip = foundRS;
                break;
            }
        }

        return new RelationShipResult(found, relationShip);
    }

    private static void processInterfaceElements(List<ExecutableElement> elementsFromInterface,
                                                 List<ExecutableElement> elementsFromBind,
                                                 TypeElement baseIntf,
                                                 TypeElement concreteIntf,
                                                 TypeElement bind,
                                                 Map<IExecutableElementContainer, List<ITypeElementPairContainer>> map,
                                                 boolean intfInjected,
                                                 ProcessingEnvironment env) {
        DeclaredType baseDt = MoreTypes.asDeclared(baseIntf.asType());
        for (ExecutableElement method : elementsFromInterface) {
            ExecutableElementContainer container = new ExecutableElementContainer(method, baseDt, env);
            IRelationShipResult result = findRelation(method, elementsFromBind, bind, false, env);

            if (result.isDuplicateFound()) {
                TypeElementPairContainer typeElementPairContainer = new TypeElementPairContainer(concreteIntf, bind, baseDt);
                typeElementPairContainer.setAbstract(result.getRelationShip() == ExecutableRelationShip.Same
                        || result.getRelationShip() == ExecutableRelationShip.OverridingAbstract);
                typeElementPairContainer.setFinal(isFinal(bind));
                typeElementPairContainer.setUseInjection(intfInjected);
                typeElementPairContainer.setRelationShip(result.getRelationShip());

                addValueToMapList(container, (ITypeElementPairContainer) typeElementPairContainer, map);
            } else {
                addValueToMapList(container, null, map);
            }

            // interface's methods should not be redefined
            container.setRedefinitionRequired(false);
        }
    }

    private static void processBindElements(List<ExecutableElement> elementsFromInterface,
                                            List<ExecutableElement> elementsFromBind,
                                            TypeElement baseIntf,
                                            TypeElement concreteIntf,
                                            TypeElement bind,
                                            Map<IExecutableElementContainer, List<ITypeElementPairContainer>> map,
                                            boolean intfInjected,
                                            ProcessingEnvironment env) {
        DeclaredType baseDt = MoreTypes.asDeclared(baseIntf.asType());
        DeclaredType concreteDt = Util.getDeclaredType(baseDt, concreteIntf, bind, env);

        for (ExecutableElement method : elementsFromBind) {
            ExecutableElementContainer container = new ExecutableElementContainer(method, concreteDt, env);
            IRelationShipResult result = findRelation(method, elementsFromInterface, baseIntf, true, env);

            if (!result.isDuplicateFound()) {
                TypeElementPairContainer typeElementPairContainer = new TypeElementPairContainer(concreteIntf, bind, concreteDt);
                typeElementPairContainer.setAbstract(isAbstract(method)
                        || result.getRelationShip() == ExecutableRelationShip.Same);
                typeElementPairContainer.setFinal(isFinal(bind));
                typeElementPairContainer.setUseInjection(intfInjected);
                typeElementPairContainer.setRelationShip(result.getRelationShip());

                addValueToMapList(container, (ITypeElementPairContainer) typeElementPairContainer, map);
            }

            // Redefine only if no duplicates found
            container.setRedefinitionRequired(!result.isDuplicateFound());
        }
    }

    /**
     * Get list of executable elements and list of overriders
     * @param element element that contains executable elements
     * @param env annotation processing environment
     * @return map
     */
    public static Map<IExecutableElementContainer, List<ITypeElementPairContainer>> getMethodsMap(TypeElement element, ProcessingEnvironment env) {
        HashMap<IExecutableElementContainer, List<ITypeElementPairContainer>> map = new HashMap<IExecutableElementContainer, List<ITypeElementPairContainer>>();

        Map<Visibility, ? extends List<ExecutableElement>> executableElements = getVisibleExecutableElements(element, env);
        for (TypeMirror mirror : element.getInterfaces()) {
            TypeElement typeInterfaceElement = MoreTypes.asTypeElement(mirror);
            TypeElement bindClassType = AnnotationUtils.getBindClassType(typeInterfaceElement, env);

            if (bindClassType == null)
                continue;

            boolean hasInjectAnnotation = AnnotationUtils.hasUseInjectionAnnotation(typeInterfaceElement);

            Map<Visibility, ? extends List<ExecutableElement>> executableElementBind = getVisibleExecutableElements(bindClassType, env);
            List<ExecutableElement> publicInterfaceElements = executableElements.get(Visibility.PUBLIC);

            processInterfaceElements(publicInterfaceElements,
                    executableElementBind.get(Visibility.PUBLIC),
                    element, typeInterfaceElement, bindClassType, map, hasInjectAnnotation, env);

            processBindElements(publicInterfaceElements,
                    executableElementBind.get(Visibility.PUBLIC),
                    element, typeInterfaceElement, bindClassType, map, hasInjectAnnotation, env);

            processBindElements(publicInterfaceElements,
                    executableElementBind.get(Visibility.PROTECTED),
                    element, typeInterfaceElement, bindClassType, map, hasInjectAnnotation, env);

        }

        return map;
    }
}
