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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * A class extends {@link HelloWorldCdiSeTest} for Apache OpenWebBeans.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="<a href="https://openwebbeans.apache.org/">Apache OpenWebBeans</a>
 * @see <a href="https://openwebbeans.apache.org/owbsetup_se.html">OpenWebBeans and JavaSE</a>
 */
@ExtendWith({HelloWorldCdiSeTestInstanceFactory.class})
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldCdiSeOpenWebBeansTest extends HelloWorldCdiSeTest {

    /**
     * Removes handlers from the root logger and installs SLF4J bridge handler.
     *
     * @see SLF4JBridgeHandler#removeHandlersForRootLogger()
     * @see SLF4JBridgeHandler#install()
     */
    @BeforeAll
    static void _beforeAll() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }
}
