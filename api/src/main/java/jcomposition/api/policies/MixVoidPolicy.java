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

package jcomposition.api.policies;

import jcomposition.api.types.IExecutableElementContainer;
import jcomposition.api.types.ITypeElementPairContainer;

import javax.lang.model.type.TypeKind;
import java.util.List;

/**
 * Use first overriding method in case of returning non-void value.
 * Mix methods call that returns nothing (void)
 */
public class MixVoidPolicy extends UseFirstPolicy {
    @Override
    public List<ITypeElementPairContainer> merge(IExecutableElementContainer elementContainer, List<ITypeElementPairContainer> overriders) {
        if (elementContainer.getExecutableElement().getReturnType().getKind() != TypeKind.VOID) {
            return super.merge(elementContainer, overriders);
        }

        return filterAbstract(overriders);
    }
}
