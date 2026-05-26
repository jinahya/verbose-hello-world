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

import com.github.jinahya.hello.api.*;

import java.nio.charset.*;

/**
 * A class, for demonstration purposes only, implements the {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class HelloWorldDemo implements HelloWorld {

    private static final String STRING = "hello, world";

    HelloWorldDemo() {
        super();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '@' + String.format("%08x", hashCode());
    }

    @Override
    public byte[] set(final byte[] array, final int index) {
        final var src = STRING.getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(
                src,       // <src>
                0,         // <srcPos>
                array,     // <dest>
                index,     // <destPos>
                src.length // <length>
        );
        return array;
    }
}
