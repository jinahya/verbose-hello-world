package com.github.jinahya.hello;

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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.hk2.api.AnnotationLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import java.io.Serial;

/**
 * A binder for injecting {@link HelloWorld} instances.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldDiHk2Binder
        extends AbstractBinder {

    private static class _QualifiedDemo_
            extends AnnotationLiteral<_QualifiedDemo>
            implements _QualifiedDemo {

        @Serial
        private static final long serialVersionUID = 8947668889394516822L;
    }

    private static class _QualifiedImpl_
            extends AnnotationLiteral<_QualifiedImpl>
            implements _QualifiedImpl {

        @Serial
        private static final long serialVersionUID = 9084623087464727990L;
    }

    @Override
    protected void configure() {
        bind(HelloWorldDemo.class)
                .named(HelloWorldDiConstants._NAME_DEMO)
                .to(HelloWorld.class);
        bind(HelloWorldImpl.class)
                .named(HelloWorldDiConstants._NAME_IMPL)
                .to(HelloWorld.class);
        bind(HelloWorldDemo.class)
                .qualifiedBy(new _QualifiedDemo_())
                .to(HelloWorld.class);
        bind(HelloWorldImpl.class)
                .qualifiedBy(new _QualifiedImpl_())
                .to(HelloWorld.class);
    }
}
