package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-app3
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

import com.google.inject.AbstractModule;

/**
 * A module for injecting instances of {@link HelloWorld}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class HelloWorldModule extends AbstractModule {

    /**
     * The fully qualified name of the {@code HelloWorldImpl} class.
     */
    private static final String HELLO_WORLD_IMPL_FQCN = "com.github.jinahya.hello.HelloWorldImpl";

    /**
     * The class found with {@link #HELLO_WORLD_IMPL_FQCN}.
     */
    private static final Class<? extends HelloWorld> HELLO_WORLD_IMPL_CLASS;

    static {
        try {
            HELLO_WORLD_IMPL_CLASS = Class.forName(HELLO_WORLD_IMPL_FQCN).asSubclass(
                    HelloWorld.class);
        } catch (final ClassNotFoundException cnfe) {
            throw new InstantiationError(cnfe.getMessage());
        }
    }

    @Override
    protected void configure() {
        bind(HelloWorld.class).to(HELLO_WORLD_IMPL_CLASS);
    }
}
