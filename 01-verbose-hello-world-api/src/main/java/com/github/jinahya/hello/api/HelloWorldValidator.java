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

import java.util.*;

/**
 * Internal validation helpers for byte arrays passed to {@link HelloWorld} methods — verifies that
 * the given array has room for the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>
 * starting at the given index.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class HelloWorldValidator {

    static byte[] requireValid(final byte[] array) {
        Objects.requireNonNull(array, "array is null");
        if (array.length < HelloWorld.BYTES) {
            throw new IllegalArgumentException(
                    "array.length(" + array.length + ") < " + HelloWorld.BYTES
            );
        }
        return array;
    }

    static void requireValid(final byte[] array, final int index) {
        requireValid(array);
        if (index < 0) {
            throw new IllegalArgumentException("index(" + index + ") < 0");
        }
        if (index + HelloWorld.BYTES > array.length) {
            throw new IllegalArgumentException(
                    "index(" + index + ") + " + HelloWorld.BYTES +
                    " > array.length(" + array.length
                    + ")"
            );
        }
    }

    private HelloWorldValidator() {
        throw new AssertionError("instantiation is not allowed");
    }
}
