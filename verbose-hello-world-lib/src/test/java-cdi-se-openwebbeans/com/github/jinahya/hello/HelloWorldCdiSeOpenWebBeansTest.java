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

import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.bridge.SLF4JBridgeHandler;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * A class extends {@link HelloWorldCdiSeTest} for Apache OpenWebBeans.
 *
 * @see <a href="<a href="https://openwebbeans.apache.org/">Apache OpenWebBeans</a>
 */
class HelloWorldCdiSeOpenWebBeansTest extends HelloWorldCdiSeTest {

    private static final Logger logger = getLogger(lookup().lookupClass());

    /**
     * Removes handlers from the root logger and installs SLF4J bridge handler.
     *
     * @see SLF4JBridgeHandler#removeHandlersForRootLogger()
     * @see SLF4JBridgeHandler#install()
     */
    @BeforeAll
    static void bridgetSlf4j() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        logger.debug("handlers removed from the root logger");
        SLF4JBridgeHandler.install();
        logger.debug("bridget handler installed");
    }
}
