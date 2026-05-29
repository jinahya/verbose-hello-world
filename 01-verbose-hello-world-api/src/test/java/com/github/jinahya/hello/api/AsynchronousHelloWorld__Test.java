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

import lombok.*;
import lombok.experimental.*;

import java.util.function.*;

import static com.github.jinahya.hello.api.MockitoTestUtils.*;
import static org.mockito.Mockito.*;

/**
 * An abstract class for testing {@link AsynchronousHelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
//@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public abstract class AsynchronousHelloWorld__Test<T extends HelloWorld,
        U extends AsynchronousHelloWorld<T>> {

    protected AsynchronousHelloWorld__Test(
            final Class<T> synchronousServiceClass,
            final Function<? super T, ? extends U> asynchronousServiceInitializer) {
        super();
        synchronousService = loggingSpy(mock(synchronousServiceClass));
        asynchronousService = loggingSpiedInstance(
                asynchronousServiceInitializer.apply(synchronousService)
        );
    }

    // -------------------------------------------------------------------------- synchronousService

    // ------------------------------------------------------------------------- asynchronousService

    // ---------------------------------------------------------------------------------------------
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private final T synchronousService;

    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private final U asynchronousService;
}
