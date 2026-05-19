package com.github.jinahya.hello.lib;

/*-
 * #%L
 * verbose-hello-world-lib
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.github.jinahya.hello.api.HelloWorld;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.lang.annotation.Annotation;

/**
 * A binder for injecting {@link HelloWorld} instances.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://stackoverflow.com/q/29767581/330457">How can I bind a factory to a
 * annotation-qualified injection point?</a>
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldDiHk2Binder extends org.glassfish.hk2.utilities.binding.AbstractBinder {

    private static class __QualifiedDemo_Literal
            extends org.glassfish.hk2.api.AnnotationLiteral<__QualifiedDemo>
            implements __QualifiedDemo {

        @Serial
        private static final long serialVersionUID = 8947668889394516822L;
    }

    private static class __QualifiedImpl_Literal
            extends org.glassfish.hk2.api.AnnotationLiteral<__QualifiedImpl>
            implements __QualifiedImpl {

        @Serial
        private static final long serialVersionUID = 9084623087464727990L;
    }

    // ---------------------------------------------------------------------------------------------
    private void bindNamed(final Class<? extends HelloWorld> serviceClass, String name) {
        log.debug("binding {}, named as '{}', to {}", serviceClass.getSimpleName(), name,
                  HelloWorld.class.getSimpleName());
        bind(serviceClass)
                .named(name)
                .to(HelloWorld.class);
    }

    private void bindQualified(final Class<? extends HelloWorld> serviceClass,
                               final Annotation annotation) {
        log.debug("binding {}, qualified by @{}, to {}", serviceClass.getSimpleName(),
                  annotation.annotationType().getSimpleName(), HelloWorld.class.getSimpleName());
        bind(serviceClass)
                .qualifiedBy(annotation)
                .to(HelloWorld.class);
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    protected void configure() {
        bindNamed(HelloWorldDemo.class, HelloWorldDiConstants._NAME_DEMO);
        bindNamed(HelloWorldImpl.class, HelloWorldDiConstants._NAME_IMPL);
        // -----------------------------------------------------------------------------------------
        bindQualified(HelloWorldDemo.class, new __QualifiedDemo_Literal());
        bindQualified(HelloWorldImpl.class, new __QualifiedImpl_Literal());
    }
}
