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

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.BeforeEach;

/**
 * An injection test for Google Guice.
 *
 * @see <a href="https://github.com/google/guice">Guice</a>
 */
class HelloWorldDiGuiceTest extends HelloWorldDiTest {

    // -----------------------------------------------------------------------------------------------------------------
    @BeforeEach
    private void inject() {
        final Injector injector = Guice.createInjector(new HelloWorldDiGuiceModule());
        injector.injectMembers(this);
    }
}
