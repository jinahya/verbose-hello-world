package com.github.jinahya.hello.api;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2024 Jinahya, Inc.
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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * (Internal) Constants for the {@link HelloWorld}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class HelloWorldConstants {

    static final String HELLO_WORLD_STRING = "hello, world";

    static final Charset HELLO_WORLD_CHARSET = StandardCharsets.US_ASCII;

    static final byte[] HELLO_WORLD_BYTES = HELLO_WORLD_STRING.getBytes(HELLO_WORLD_CHARSET);

    private HelloWorldConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
