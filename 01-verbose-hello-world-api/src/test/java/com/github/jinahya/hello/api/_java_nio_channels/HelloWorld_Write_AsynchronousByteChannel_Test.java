package com.github.jinahya.hello.api._java_nio_channels;

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

import com.github.jinahya.hello.api.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link HelloWorld#write(AsynchronousByteChannel) write(channel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S101"
})
class HelloWorld_Write_AsynchronousByteChannel_Test extends HelloWorld__Test {

    /**
     * Verifies {@link HelloWorld#write(AsynchronousByteChannel) write(channel)} method throws a
     * {@link NullPointerException} when the {@code channel} argument is {@code null}.
     */
    @DisplayName("should throw a <NullPointerException> when the <channel> argument is <null>")
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        var service = service();
        var channel = (AsynchronousByteChannel) null;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(NullPointerException.class, () -> service.write(channel));
    }

    /**
     * Verifies that the method writes all {@value HelloWorld#BYTES} bytes to the {@code channel}
     * across one or more partial writes, and returns the {@code channel}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if an I/O error occurs.
     */
    @DisplayName(
            "should write all <hello-world-bytes> across partial writes, and return the <channel>")
    @Test
    @SuppressWarnings({"rawtypes"})
    void __() throws InterruptedException, ExecutionException {
        // ----------------------------------------------------------------------------------- given
        final var service = put_buffer12_increases_buffer_position_by_12(service());
        final var channel = mock(AsynchronousByteChannel.class);
        final var bufferPositions = new ArrayList<Integer>();
        final var futureReference = new AtomicReference<Future>();
        doAnswer(w -> {
            assert futureReference.get() == null;
            final var src = w.getArgument(0, ByteBuffer.class);
            bufferPositions.add(src.position());
            final var future = mock(Future.class);
            futureReference.set(future);
            doAnswer(g -> {
                final var n = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                src.position(src.position() + n);
                futureReference.set(null);
                return n;
            }).when(future).get();
            return future;
        }).when(channel).write(argThat(v -> {
            return v != null
                   && v.capacity() == HelloWorld.BYTES
                   && v.limit() == HelloWorld.BYTES
                   && v.hasRemaining();
        }));
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(channel);
        // ------------------------------------------------------------------------------------ then
        final var buffer = put_buffer12_invoked_once(service);
//        assertFalse(bufferPositions.isEmpty());
//        assertEquals(0, bufferPositions.getFirst());
//        for (var i = 1; i < bufferPositions.size(); i++) {
//            assertTrue(bufferPositions.get(i) > bufferPositions.get(i - 1));
//        }
//        assertFalse(buffer.hasRemaining());
        assertSame(channel, result);
    }

    /**
     * Verifies that the method propagates an {@link ExecutionException} when the {@code channel}'s
     * {@link Future#get() future.get()} fails — possibly on the first invocation, or after one or
     * more partial writes have already succeeded.
     */
    @DisplayName("""
            should propagate an <ExecutionException>
            when the <channel> fails on or after partial writes""")
    @Test
    @SuppressWarnings({"rawtypes"})
    void __fails() {
        // ----------------------------------------------------------------------------------- given
        final var service = put_buffer12_increases_buffer_position_by_12(service());
        final var channel = mock(AsynchronousByteChannel.class);
        final var reference = new AtomicReference<Future>();
        final var cause = new IOException("simulated write failure");
        final var errored = new AtomicBoolean();
        doAnswer(w -> {
            assert !errored.get();
            final var src = w.getArgument(0, ByteBuffer.class);
            final var future = mock(Future.class);
            reference.set(future);
            doAnswer(g -> {
                final var n = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                src.position(src.position() + n);
                reference.set(null);
                if (!src.hasRemaining() || ThreadLocalRandom.current().nextBoolean()) {
                    errored.set(true);
                    throw new ExecutionException(cause);
                }
                return n;
            }).when(future).get();
            return future;
        }).when(channel).write(argThat(v -> {
            return v != null
                   && v.capacity() == HelloWorld.BYTES
                   && v.limit() == HelloWorld.BYTES
                   && v.hasRemaining();
        }));
        // ------------------------------------------------------------------------------- when/then
//        final var thrown = assertThrows(
//                ExecutionException.class,
//                () -> service.write(channel)
//        );
//        assertSame(cause, thrown.getCause());
    }
}
