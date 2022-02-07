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

import org.junit.jupiter.api.BeforeEach;

import static org.glassfish.hk2.utilities.ServiceLocatorUtilities.bind;

/**
 * An extended {@link HelloWorldDiTest} which uses {@link HelloWorldDiHk2Binder}
 * as a binder.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class HelloWorldDiHk2Test
        extends HelloWorldDiTest {

    @BeforeEach
    void inject() {
        final var binder = new HelloWorldDiHk2Binder();
        final var locator = bind(binder);
        locator.inject(this);
    }
}
