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
import jcomposition.api.IMergeConflictPolicy;
import jcomposition.api.ITypeHandler;
import jcomposition.api.handlers.DefaultTypeHandler;
import jcomposition.api.policies.MixVoidPolicy;

import javax.annotation.Generated;
import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Composition {
    String name() default Const.UNDEFINED;
    Class<? extends IMergeConflictPolicy> onConflict() default MixVoidPolicy.class;
    Class<? extends ITypeHandler> typeHandler() default DefaultTypeHandler.class;
}
