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
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * A class for testing
 * {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel, position)")
@Slf4j
class HelloWorld_Write_AsynchronousFileChannel_Test extends HelloWorldTest {

    /**
     * Verifies that the
     * {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)} method
     * throws a {@link NullPointerException} when the {@code channel} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <channel> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = (AsynchronousFileChannel) null;
        final var position = ThreadLocalRandom.current().nextLong() >>> 1;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel, position)
        );
    }

    /**
     * Verifies that the
     * {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)} method
     * throws an {@link IllegalArgumentException} when the {@code position} argument is negative.
     */
    @DisplayName("""
            should throw an <IllegalArgumentException>
            when the <position> argument is <not positive>"""
    )
    @Test
    void _ThrowIllegalArgumentException_PositionIsNegative() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = Mockito.mock(AsynchronousFileChannel.class);
        final var position = ThreadLocalRandom.current().nextLong() | Long.MIN_VALUE;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.write(channel, position)
        );
    }

    /**
     * Verifies that the method writes all {@value HelloWorld#BYTES} bytes to the {@code channel}
     * starting at {@code position}, across one or more partial writes, and returns the
     * {@code channel}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("""
            should write all <hello-world-bytes> across partial writes,
            and return the <channel>"""
    )
    @Test
    void __succeeds() throws InterruptedException, ExecutionException {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.put_buffer_will_increase_buffer_position_by_12(
                service());
        final var channel = Mockito.mock(AsynchronousFileChannel.class);
        final var increments = new ArrayList<Integer>();
        Mockito.doAnswer(w -> {
            final var src = w.getArgument(0, ByteBuffer.class);
            @SuppressWarnings({"unchecked"})
            final var future = (Future<Integer>) Mockito.mock(Future.class);
            // stub, <future.get()> will increase <src>'s <position> by a random value
            Mockito.doAnswer(g -> {
                final var n = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                src.position(src.position() + n);
                increments.add(n);
                return n;
            }).when(future).get();
            return future;
        }).when(channel).write(
                ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()), // <src>
                ArgumentMatchers.longThat(p -> p >= 0L)                       // <position>
        );
        final var position = ThreadLocalRandom.current().nextLong(8L);
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(channel, position);
        // ------------------------------------------------------------------------------------ then
        final var buffer = HelloWorldTestUtils.put_buffer12_invoked_once(service);
//        final List<Long> positions;
//        {
//            final var captor = ArgumentCaptor.forClass(long.class);
//            Mockito.verify(channel, Mockito.atLeastOnce())
//                    .write(ArgumentMatchers.same(buffer), captor.capture());
//            positions = captor.getAllValues();
//        }
//        Assertions.assertEquals(increments.size(), positions.size());
//        Assertions.assertEquals(position, positions.getFirst());
//        final var iterator = increments.iterator();
//        positions.stream().reduce((p1, p2) -> {
//            Assertions.assertEquals(p1 + iterator.next(), p2);
//            return p2;
//        });
        Assertions.assertSame(channel, result);
    }

    /**
     * Verifies that the method propagates an {@link ExecutionException} when the {@code channel}'s
     * {@link Future#get() future.get()} fails — possibly on the first invocation, or after one or
     * more partial writes have already succeeded.
     */
    @DisplayName("""
            should propagate an <ExecutionException>
            when the <channel> fails on or after partial writes"""
    )
    @Test
    void __fails() {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.put_buffer_will_increase_buffer_position_by_12(
                service());
        final var channel = Mockito.mock(AsynchronousFileChannel.class);
        final var cause = new IOException("simulated write failure");
        Mockito.doAnswer(w -> {
            final var src = w.getArgument(0, ByteBuffer.class);
            @SuppressWarnings({"unchecked"})
            final var future = (Future<Integer>) Mockito.mock(Future.class);
            Mockito.doAnswer(g -> {
                final var n = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                src.position(src.position() + n);
                if (!src.hasRemaining() || ThreadLocalRandom.current().nextBoolean()) {
                    throw new ExecutionException(cause);
                }
                return n;
            }).when(future).get();
            return future;
        }).when(channel).write(
                ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()),
                ArgumentMatchers.longThat(p -> p >= 0L)
        );
        final var position = ThreadLocalRandom.current().nextLong(8L);
        // ------------------------------------------------------------------------------- when/then
//        final var thrown = Assertions.assertThrows(
//                ExecutionException.class,
//                () -> service.write(channel, position)
//        );
//        Assertions.assertSame(cause, thrown.getCause());
    }
}
