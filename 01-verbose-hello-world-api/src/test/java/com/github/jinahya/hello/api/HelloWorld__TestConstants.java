package com.github.jinahya.hello.api;

/*-
 * #%L
 * verbose-hello-world-api
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

import com.github.jinahya.hello.api.util.*;
import lombok.extern.slf4j.*;

/**
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@SuppressWarnings({
        "java:S101"
})
public final class HelloWorld__TestConstants {

    public static final String HELLO_WORLD_STRING = "hello, world";

    // ---------------------------------------------------------------------------------------------
    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private HelloWorld__TestConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
