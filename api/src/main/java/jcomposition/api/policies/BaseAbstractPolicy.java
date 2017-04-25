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

import jcomposition.api.IMergeConflictPolicy;
import jcomposition.api.types.ITypeElementPairContainer;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAbstractPolicy implements IMergeConflictPolicy {
    /**
     * Removes (filter) abstract methods from overriders list
     *
     * @param overriders list of overriders of executable element
     * @return not abstract methods to implement
     */
    public List<ITypeElementPairContainer> filterAbstract(List<ITypeElementPairContainer> overriders) {
        List<ITypeElementPairContainer> notAbstract = new ArrayList<ITypeElementPairContainer>(overriders.size());

        for (ITypeElementPairContainer overrider : overriders) {
            if (!overrider.isAbstract()) {
                notAbstract.add(overrider);
            }
        }

        return notAbstract;
    }
}
