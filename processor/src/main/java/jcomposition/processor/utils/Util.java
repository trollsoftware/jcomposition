package jcomposition.processor.utils;

import com.google.auto.common.MoreElements;
import com.google.common.base.Predicate;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Util {
    public static Modifier[] modifiersToArray(Collection<Modifier> modifiers) {
        Modifier[] result = new Modifier[modifiers.size()];

        return modifiers.toArray(result);
    }

    /**
     * Adds a value to map's value list.
     * @param key
     * @param value pass null to just create empty list
     * @param map
     * @param <K>
     * @param <V>
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public static <K, V extends List<T>, T> void addValueToMapList(K key, T value, Map<K, V> map) {
        V list = map.get(key);

        if (list == null) {
            list = (V) new ArrayList<T>();

            map.put(key, list);
        }

        if (value != null) {
            list.add(value);
        }
    }

    public static boolean isAbstract(Element typeElement) {
        Predicate<Element> predicate = MoreElements.hasModifiers(Modifier.ABSTRACT);

        return predicate.apply(typeElement);
    }

    public static DeclaredType getDeclaredType(DeclaredType baseDt, TypeElement intf, TypeElement bind, ProcessingEnvironment env) {
        TypeMirror[] params = new TypeMirror[intf.getTypeParameters().size()];

        for (int i = 0; i < params.length; i++) {
            TypeParameterElement parameterElement = intf.getTypeParameters().get(i);

            params[i] = env.getTypeUtils().asMemberOf(baseDt, parameterElement);
        }

        return env.getTypeUtils().getDeclaredType(bind, params);
    }
}
