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
    static final String _NAMED_DEMO = "demo";

    /**
     * An injection qualifier for {@link HelloWorldImpl}.
     */
    static final String _NAMED_IMPL = "impl";

    @Override
    HelloWorld serviceInstance() {
        return switch (current().nextInt(4)) {
            case 0 -> namedDemo;
            case 1 -> namedImpl;
            case 2 -> qualifiedDemo;
            default -> qualifiedImpl;
        };
    }

    @Named(_NAMED_DEMO)
    @Inject
    HelloWorld namedDemo;

    @Named(_NAMED_IMPL)
    @Inject
    HelloWorld namedImpl;

    @_QualifiedDemo
    @Inject
    HelloWorld qualifiedDemo;

    @_QualifiedImpl
    @Inject
    HelloWorld qualifiedImpl;
}
