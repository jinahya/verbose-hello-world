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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

/**
 * An abstract class for testing {@link AsynchronousHelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public abstract class AsynchronousHelloWorldTest {

    protected AsynchronousHelloWorldTest() {
        super();
        this.synchronousService = Mockito.mock(HelloWorld.class, Mockito.CALLS_REAL_METHODS);
        this.asynchronousService = Mockito.spy(
                new DefaultAsynchronousHelloWorld(synchronousService, Runnable::run)
        );
    }

    // -------------------------------------------------------------------------- synchronousService

    // ------------------------------------------------------------------------- asynchronousService

    // ---------------------------------------------------------------------------------------------
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private final HelloWorld synchronousService;

    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private final AsynchronousHelloWorld asynchronousService;
}
