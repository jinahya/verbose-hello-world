package com.github.jinahya.hello.util.java.lang;

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

import java.util.Objects;
import java.util.Random;

public final class ArrayUtils {

    public static byte[] randomize(final byte[] array, final int offset, final int length,
                                   final Random random) {
        Objects.requireNonNull(array, "array is null");
        if (offset < 0) {
            throw new IllegalArgumentException("negative offset: " + offset);
        }
        if (length > array.length - offset) {
            throw new IllegalArgumentException(
                    "length(" + length + ")" +
                    " > " +
                    "array.length(" + array.length + ") - offset(" + offset + ")");
        }
        Objects.requireNonNull(random, "random is null");
        final var limit = offset + length;
        for (int i = offset; i < limit; i++) {
            array[i] = (byte) random.nextInt();
        }
        return array;
    }

    private ArrayUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
