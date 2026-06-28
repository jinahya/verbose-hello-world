package com.github.jinahya.hello.lib;

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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

/**
 * An extended {@link HelloWorldDi__Test} which uses {@link HelloWorldDi_Hk2_Binder} as a binder.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("HelloWorldDi / HK2")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldDi_Hk2_Test extends HelloWorldDi__Test {

    @BeforeEach
    void _beforeEach() {
        final var binder = new HelloWorldDi_Hk2_Binder();
        final var locator = org.glassfish.hk2.utilities.ServiceLocatorUtilities.bind(binder);
        locator.inject(this);
    }
}
