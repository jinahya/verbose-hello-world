package com.github.jinahya.hello.api;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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

import org.junit.platform.commons.util.ReflectionUtils;

import java.util.Objects;

/**
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
abstract class HelloWorldGraphicsTest<T extends HelloWorldGraphics> {

    HelloWorldGraphicsTest(final Class<T> type) {
        super();
        this.type = Objects.requireNonNull(type, "type is null");
    }
    // ---------------------------------------------------------------------------------------- type

    /**
     * Returns a new instance of {@link #type}.
     *
     * @return a new instance of {@link #type}.
     */
    T newTypeInstance() {
        return ReflectionUtils.newInstance(type);
    }

    // ------------------------------------------------------------------------------------ instance

    /**
     * Returns an instance of {@link #type}.
     *
     * @return an instance of {@link #type}.
     */
    protected final HelloWorldGraphics service() {
        T result = _instance;
        if (result == null) {
            result = _instance = newTypeInstance();
        }
        return result;
    }

    // ---------------------------------------------------------------------------------------------
    final Class<T> type;

    private T _instance;
}
