package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-app2
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

import java.lang.reflect.Constructor;

public class HelloWorldImplProvider implements HelloWorldProvider {

    private static final String HELLO_WORLD_IMPL_FQCN = "com.github.jinahya.hello.HelloWorldImpl";

    private static final Class<? extends HelloWorld> HELLO_WORLD_IMPL_CLASS;

    static {
        try {
            HELLO_WORLD_IMPL_CLASS = Class.forName(HELLO_WORLD_IMPL_FQCN).asSubclass(HelloWorld.class);
        } catch (final ClassNotFoundException cnfe) {
            throw new InstantiationError(cnfe.getMessage());
        }
    }

    private static final Constructor<? extends HelloWorld> HELLO_WORLD_IMPL_CONSTRUCTOR;

    static {
        try {
            HELLO_WORLD_IMPL_CONSTRUCTOR = HELLO_WORLD_IMPL_CLASS.getConstructor();
        } catch (final NoSuchMethodException nsme) {
            throw new InstantiationError(nsme.getMessage());
        }
    }

    @Override
    public HelloWorld getInstance() {
        try {
            return HELLO_WORLD_IMPL_CONSTRUCTOR.newInstance();
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException("failed to instantiate", roe);
        }
    }
}
