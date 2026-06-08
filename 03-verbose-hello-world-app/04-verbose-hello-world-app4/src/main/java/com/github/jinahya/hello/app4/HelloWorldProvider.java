package com.github.jinahya.hello.app4;

/*-
 * #%L
 * verbose-hello-world-app4
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

import com.github.jinahya.hello.api.*;
import jakarta.enterprise.inject.*;

import java.util.*;

/**
 * A CDI producer that produces (and disposes) instances of the {@link HelloWorld} interface — the
 * {@link #produce()} method loads the first {@link HelloWorld} provider registered through
 * {@link ServiceLoader} and returns it as a CDI bean, while {@link #dispose(HelloWorld)} is the
 * matching disposer (currently a no-op since the produced instance holds no resources).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class HelloWorldProvider {

    /**
     * Produces an instance of the {@link HelloWorld} interface by loading the first provider
     * registered through {@link ServiceLoader}.
     *
     * @return an instance of the {@link HelloWorld} interface; never {@code null}.
     * @see #dispose(HelloWorld)
     */
    @Produces
    HelloWorld produce() {
        return ServiceLoader.load(HelloWorld.class).iterator().next();
    }

    /**
     * Disposes the given instance of the {@link HelloWorld} interface. The implementation is a
     * no-op since the produced instance holds no resources that need releasing.
     *
     * @param bean the instance of the {@link HelloWorld} interface to dispose.
     * @see #produce()
     */
    void dispose(@Disposes final HelloWorld bean) {
        // empty
    }
}
