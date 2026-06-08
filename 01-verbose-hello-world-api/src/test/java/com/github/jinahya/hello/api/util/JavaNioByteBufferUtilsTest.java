package com.github.jinahya.hello.api.util;

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

import com.github.jinahya.hello.miscellaneous.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.nio.*;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * A class for testing {@link JavaNioByteBufferUtils}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("JavaNioByteBufferUtils")
@Slf4j
class JavaNioByteBufferUtilsTest {

    private static Stream<byte[]> randomByteArrayStream() {
        return Stream.concat(
                IntStream.range(0, ThreadLocalRandom.current().nextInt(8) + 1)
                        .mapToObj(i -> new byte[ThreadLocalRandom.current().nextInt(16) + 1]),
                Stream.of(new byte[0])
        );
    }

    static Stream<ByteBuffer> randomByteBufferStream() {
        return Stream.concat(
                randomByteArrayStream().map(ByteBuffer::wrap),
                IntStream.rangeClosed(0, ThreadLocalRandom.current().nextInt(8))
                        .mapToObj(i -> ByteBuffer.allocateDirect(
                                ThreadLocalRandom.current().nextInt(16)))
        ).peek(b -> {
            b.limit(ThreadLocalRandom.current().nextInt(b.capacity() + 1))
                    .position(ThreadLocalRandom.current().nextInt(b.remaining() + 1));
        });
    }

    @DisplayName("should print the random <ByteBuffer> without error")
    @MethodSource({"randomByteBufferStream"})
    @ParameterizedTest
    void print__(final ByteBuffer buffer) {
        _Java_Nio_ByteBuffer_TestUtils.print(buffer);
    }
}
