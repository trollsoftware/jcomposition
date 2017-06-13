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

package jcomposition.example;

import jcomposition.api.annotations.ShareProtected;
import jcomposition.example.interfaces.diamond.ICircleEvents;

public class CircleEvents implements ICircleEvents {
    @Override
    public void onUpdate() {
        System.out.println("Circle onUpdate");
    }

    @Override
    public void onVisibilityChanged(boolean visibility) {
        System.out.println("Visibility of circle has changed to " + visibility);
    }

    /**
     * Test of protected method
     */
    protected void someProtectedMethod() {

    }
}
