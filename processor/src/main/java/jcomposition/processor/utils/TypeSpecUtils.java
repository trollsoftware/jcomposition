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

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeSpec;
import jcomposition.api.types.specs.AnnotationSpecModel;
import jcomposition.api.types.specs.TypeSpecModel;

import java.util.Map;

import static java.lang.Character.isISOControl;

public class TypeSpecUtils {
    public static void applyTypeSpecModel(TypeSpecModel model, TypeSpec.Builder builder) {
        for (AnnotationSpecModel specModel : model.getAnnotations()) {
            AnnotationSpec.Builder aBuilder = AnnotationSpec.builder(specModel.getAnnotation());
            for (Map.Entry<String, Object> member : specModel.getMembers().entrySet()) {
                addMember(member.getKey(), member.getValue(), aBuilder);
            }
            builder.addAnnotation(aBuilder.build());
        }
    }

    private static void addMember(String memberName, Object value, AnnotationSpec.Builder builder) {
        if (value instanceof Class<?>) {
            builder.addMember(memberName, "$T.class", value);
        } else if (value instanceof Enum) {
            builder.addMember(memberName, "$T.$L", value.getClass(), ((Enum<?>) value).name());
        } else if (value instanceof String) {
            builder.addMember(memberName, "$S", value);
        } else if (value instanceof Float) {
            builder.addMember(memberName, "$Lf", value);
        } else if (value instanceof Character) {
            builder.addMember(memberName, "'$L'", characterLiteralWithoutSingleQuotes((Character) value));
        } else {
            builder.addMember(memberName, "$L", value);
        }
    }

    private static String characterLiteralWithoutSingleQuotes(char c) {
        // see https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.10.6
        switch (c) {
            case '\b': return "\\b"; /* \u0008: backspace (BS) */
            case '\t': return "\\t"; /* \u0009: horizontal tab (HT) */
            case '\n': return "\\n"; /* \u000a: linefeed (LF) */
            case '\f': return "\\f"; /* \u000c: form feed (FF) */
            case '\r': return "\\r"; /* \u000d: carriage return (CR) */
            case '\"': return "\"";  /* \u0022: double quote (") */
            case '\'': return "\\'"; /* \u0027: single quote (') */
            case '\\': return "\\\\";  /* \u005c: backslash (\) */
            default:
                return isISOControl(c) ? String.format("\\u%04x", (int) c) : Character.toString(c);
        }
    }
}
