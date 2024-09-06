package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-api
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

import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class for managing loggers.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class HelloWorldLoggers {

    private static Logger log;

    /**
     * Returns a logger for {@link HelloWorld} interface.
     *
     * @return a logger for {@link HelloWorld} interface.
     */
    static Logger log() {
        var result = log;
        if (result == null) {
            result = log = LoggerFactory.getLogger(HelloWorld.class);
        }
        return result;
    }

    private static System.Logger logger;

    /**
     * Returns a logger for {@link HelloWorld} interface.
     *
     * @return a logger for {@link HelloWorld} interface.
     */
    static System.Logger logger() {
        var result = logger;
        if (result == null) {
            result = logger = System.getLogger(HelloWorld.class.getName());
        }
        return result;
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private HelloWorldLoggers() {
        throw new AssertionError("instantiation is not allowed");
    }
}
