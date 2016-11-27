/*
 * Copyright 2016 Alexander Shitikov (a.shitikov73@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information visit: http://github.com/trollsoftware/trollcore
 */

package jcomposition.processor;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.service.AutoService;
import jcomposition.processor.steps.GenerateStep;

import javax.annotation.processing.Processor;
import javax.lang.model.SourceVersion;
import java.util.Arrays;

@AutoService(Processor.class)
public class AnnotationProcessor extends BasicAnnotationProcessor {

    @Override
    protected Iterable<? extends ProcessingStep> initSteps() {
        return Arrays.asList(
                new GenerateStep(processingEnv)
        );
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_6;
    }
}
