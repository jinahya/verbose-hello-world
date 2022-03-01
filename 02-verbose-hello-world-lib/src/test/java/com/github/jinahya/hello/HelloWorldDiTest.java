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

import jakarta.inject.Inject;
import jakarta.inject.Named;

import static java.util.concurrent.ThreadLocalRandom.current;

/**
 * An abstract class for testing {@link HelloWorld} implementations using Dependency Injection.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
abstract class HelloWorldDiTest extends HelloWorldTest {

    /**
     * An injection qualifier for {@link HelloWorldDemo}.
     */
    static final String DEMO = "demo";

    /**
     * An injection qualifier for {@link HelloWorldImpl}.
     */
    static final String IMPL = "impl";

    @Override
    HelloWorld helloWorld() {
        switch (current().nextInt(4)) {
            case 0:
                return namedDemo;
            case 1:
                return namedImpl;
            case 2:
                return qualifiedDemo;
            default: // 3
                return qualifiedImpl;
        }
    }

    //    @javax.inject.Named(DEMO) // Guice
    @javax.inject.Inject // Guice
    @Named(DEMO)
    @Inject
    HelloWorld namedDemo;

    //    @javax.inject.Named(IMPL) // Guice
    @javax.inject.Inject // Guice
    @Named(IMPL)
    @Inject
    HelloWorld namedImpl;

    //    @javax.inject.Inject // Guice
    @QualifiedDemo
    @Inject
    HelloWorld qualifiedDemo;

    //    @javax.inject.Inject // Guice
    @QualifiedImpl
    @Inject
    HelloWorld qualifiedImpl;
}
