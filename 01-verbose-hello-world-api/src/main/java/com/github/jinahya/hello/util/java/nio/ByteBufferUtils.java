package com.github.jinahya.hello.util.java.nio;

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

import com.github.jinahya.hello.util.java.lang.ArrayUtils;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Random;

public final class ByteBufferUtils {

    public static ByteBuffer randomized(final ByteBuffer buffer, final Random random) {
        Objects.requireNonNull(buffer, "buffer is null");
        Objects.requireNonNull(random, "random is null");
        if (buffer.hasArray()) {
            ArrayUtils.randomize(
                    buffer.array(),
                    buffer.arrayOffset() + buffer.position(),
                    buffer.remaining(),
                    random
            );
            return buffer;
        }
        final var src = new byte[buffer.remaining()];
        ArrayUtils.randomize(
                src,
                0,
                src.length,
                random
        );
        return buffer.put(buffer.position(), src);
    }

    private ByteBufferUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
