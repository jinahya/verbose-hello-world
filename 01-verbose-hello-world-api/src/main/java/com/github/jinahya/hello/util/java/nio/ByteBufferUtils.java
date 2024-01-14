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

    public static <T extends ByteBuffer> T printBuffer(final T buffer) {
        Objects.requireNonNull(buffer, "buffer is null");
        var padding = 11;
        System.out.println(
                "---------------------------------------------------------------------");
        System.out.printf("%1$" + padding + "s: %2$s%n", "buffer", buffer);
        System.out.printf("%1$" + padding + "s: %2$d%n", "remaining",
                          buffer.remaining());
        System.out.printf("%1$" + padding + "s: %2$b%n", "direct",
                          buffer.isDirect());
        System.out.printf("%1$" + padding + "s: %2$b%n", "hasArray",
                          buffer.hasArray());
        if (buffer.hasArray()) {
            System.out.printf("%1$" + padding + "s: %2$d%n", "arrayOffset",
                              buffer.arrayOffset());
        }
        System.out.println(
                "---------------------------------------------------------------------");
        var arrayOffset = buffer.hasArray() ? buffer.arrayOffset() : 0;
        var ppadding = padding + arrayOffset + buffer.position() + 3;
        System.out.printf("%1$" + ppadding + "c pos(%2$d)%n", '↓',
                          buffer.position()); //   ↓ pos(p)
        System.out.printf("%1$" + padding + "s: ", "buffer");
        for (int i = 0; i < arrayOffset; i++) {
            System.out.print(' ');
        }
        for (int i = 0; i < buffer.position(); i++) {
            System.out.print('-');
        }
        for (int i = 0; i < buffer.remaining(); i++) {
            System.out.print('*');
        }
        for (int i = buffer.position() + buffer.remaining();
             i < buffer.capacity(); i++) {
            System.out.print('-');
        }
        System.out.printf(" %1$c cap(%2$d)%n", '←',
                          buffer.capacity()); //                  ← cap(c)
        var lpadding = padding + arrayOffset + buffer.limit() + 3;
        System.out.printf("%1$" + lpadding + "c lim(%2$d)%n", '↑',
                          buffer.limit()); //      ↑ lim(l)
        if (buffer.hasArray()) {
            for (int i = 0; i < (padding + buffer.arrayOffset() + 2); i++) {
                System.out.print(' ');
            }
            System.out.printf("%1$c arrayOffset(%2$d)%n", '↓',
                              buffer.arrayOffset());
            System.out.printf("%1$" + padding + "s: ", "array");
            for (int i = 0; i < buffer.arrayOffset(); i++) {
                System.out.print('-');
            }
            var array = buffer.array();
            for (int i = buffer.arrayOffset();
                 i < buffer.arrayOffset() + buffer.capacity(); i++) {
                System.out.print('+');
            }
            for (int i = buffer.arrayOffset() + buffer.capacity();
                 i < array.length; i++) {
                System.out.print('-');
            }
            System.out.printf(" %1$c length(%2$d)%n", '←', array.length);
        }
        System.out.println(
                "---------------------------------------------------------------------");
        return buffer;
    }
}
