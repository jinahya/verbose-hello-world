package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-srv-common
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
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

import java.util.ServiceLoader;

/**
 * A class for helping {@link HelloWorldServer} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class HelloWorldServerHelper {

    private static volatile HelloWorld service;

    /**
     * Loads an instance of {@link HelloWorld} interface.
     *
     * @return an instance of {@link HelloWorld} interface.
     */
    static HelloWorld service() {
        var result = service;
        if (result == null) {
            service = result = ServiceLoader.load(HelloWorld.class).iterator().next();
        }
        return result;
    }

    private HelloWorldServerHelper() {
        throw new AssertionError("instantiation is not allowed");
    }
}
