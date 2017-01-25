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

package jcomposition.api.annotations;

import jcomposition.api.Const;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Composition {
    public enum MergeConflictPolicy {
        /**
         * Use first overriding method in case of returning non-void value.
         * Mix methods call that returns nothing (void)
         */
        MixVoid,
        /**
         * Use first overriding method in case of conflict
         */
        UseFirst,
        /**
         * Make method abstract in case of conflict.
         */
        MakeAbstract
    }

    String name() default Const.UNDEFINED;
    MergeConflictPolicy onConflict() default MergeConflictPolicy.MixVoid;
}
