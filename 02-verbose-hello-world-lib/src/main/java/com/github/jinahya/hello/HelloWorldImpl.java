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

import java.util.Objects;

/**
 * A class implements the {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class HelloWorldImpl implements HelloWorld {

    @Override
    public byte[] set(byte[] array, int index) {
        Objects.requireNonNull(array, "array is null");
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(
                    "index(" + index + ") < 0");
        }
        if (index + BYTES > array.length) {
            throw new ArrayIndexOutOfBoundsException(
                    "index(" + index + ") + " + BYTES + " > array.length("
                    + array.length + ")");
        }
        // TODO: Set 'hello, world' on array starting at index
        return array;
    }
}
