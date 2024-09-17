package com.github.jinahya.hello._04_java_nio;

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

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello.HelloWorldTest;
import com.github.jinahya.hello.畵蛇添足;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

/**
 * A class for testing
 * {@link HelloWorld#write(AsynchronousFileChannel, long, Object, CompletionHandler) write(channel,
 * position, attachment, handler)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel, position, handler, attachment)")
@Slf4j
class HelloWorld_10_Write_AsynchronousFileChannelWithHandler_Test extends HelloWorldTest {

    /**
     * Verifies that the
     * {@link HelloWorld#write(AsynchronousFileChannel, long, Object, CompletionHandler)
     * write(channel, position, attachment, handler)} method throws a {@link NullPointerException}
     * when the {@code channel} argument is {@code null}.
     */
    @DisplayName("""
            should throw a NullPointerException
            when the <channel> argument is <null>"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = (AsynchronousFileChannel) null;
        final var position = 0L;
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        final var handler = Mockito.mock(CompletionHandler.class);
        assert channel == null; // NOT O.K.
        assert position >= 0L;  // O.K.
        assert attachment != null || attachment == null; // don't care
        assert handler != null; // O.K.
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel, position, attachment, handler)
        );
    }

    /**
     * Verifies that the
     * {@link HelloWorld#write(AsynchronousFileChannel, long, Object, CompletionHandler)
     * write(channel, position, attachment, handler)} method throws an
     * {@link IllegalArgumentException} when the {@code position} argument is negative.
     */
    @DisplayName("""
            should throw an IllegalArgumentException
            when the <position> argument is <negative>"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void _ThrowIllegalArgumentException_PositionIsNegative() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = Mockito.mock(AsynchronousFileChannel.class);
        final var position = ThreadLocalRandom.current().nextLong() | Long.MIN_VALUE;
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        final var handler = Mockito.mock(CompletionHandler.class);
        assert channel != null; // O.K.
        assert position < 0L;   // NOT O.K.
        assert attachment != null || attachment == null; // don't care
        assert handler != null; // O.K.
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.write(channel, position, attachment, handler)
        );
    }

    /**
     * Verifies that the
     * {@link HelloWorld#write(AsynchronousFileChannel, long, Object, CompletionHandler)
     * write(channel, position, attachment, handler)} method throws a {@link NullPointerException}
     * when the {@code handler} argument is {@code null}.
     */
    @DisplayName("""
            should throw a NullPointerException
            when the <handler> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_HandlerIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = Mockito.mock(AsynchronousFileChannel.class);
        final var position = 0L;
        final var handler = (CompletionHandler<AsynchronousFileChannel, Object>) null;
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        assert channel != null; // O.K.
        assert position >= 0L;  // O.K.
        assert attachment != null || attachment == null; // don't care
        assert handler == null; // NOT O.K.
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel, position, attachment, handler)
        );
    }

    /**
     * Asserts
     * {@link HelloWorld#write(AsynchronousFileChannel, long, Object, CompletionHandler)
     * write(channel, position, handler, attachment)} method invokes
     * {@link CompletionHandler#completed(Object, Object) handler.completed(channel, attachment)}.
     */
    @DisplayName("""
            should invoke put(buffer[12])
            and write the <buffer> to the <channel> starting at <position>""")
    @Test
    @SuppressWarnings({"unchecked"})
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.put(buffer)> will increase the <buffer>'s <position> by <HelloWorld.BYTES>
        BDDMockito.willAnswer(i -> {
                    final var buffer = i.getArgument(0, ByteBuffer.class);
                    buffer.position(buffer.position() + HelloWorld.BYTES);
                    return buffer;
                })
                .given(service)
                .put(ArgumentMatchers.argThat(b -> b != null && b.remaining() >= HelloWorld.BYTES));
        final var channel = Mockito.mock(AsynchronousFileChannel.class);
        // stub, <channel.write(src, position, attachment, handler)>
        //         will increase <channel>'s <position> by <12>
        //         and will invoke <handler.completed>
        final var writtenSoFar = new LongAdder();
        BDDMockito.willAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var position = i.getArgument(1, Long.class);
            final var attachment = i.getArgument(2);
            final var handler = i.getArgument(3, CompletionHandler.class);
            final var result = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
            src.position(src.position() + result);
            writtenSoFar.add(result);
            handler.completed(result, attachment);
            return null;
        }).given(channel).write(
                ArgumentMatchers.argThat(s -> s != null && s.hasRemaining()), // <src>
                ArgumentMatchers.longThat(p -> p >= 0L),                      // <position>
                ArgumentMatchers.any(),                                       // <attachment>
                ArgumentMatchers.notNull()                                    // <handler>
        );
        final var position = ThreadLocalRandom.current().nextLong(128L);
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        final var handler = Mockito.mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ when
        service.write(channel, position, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        // verify, <put(buffer[12])> invoked, once
        final var buffer = verify_put_buffer12_invoked_once();
        // await, <handler> to be <completed(channel, attachment)>
        Mockito.verify(handler, Mockito.timeout(TimeUnit.SECONDS.toMillis(8L)).times(1))
                .completed(channel, attachment);
        // verify, <channel.write(buffer, captured, any, captured)> invoked, at least once
        final var positions = ArgumentCaptor.forClass(long.class);
        final var handlers = ArgumentCaptor.forClass(CompletionHandler.class);
        Mockito.verify(channel, Mockito.atLeastOnce()).write(
                ArgumentMatchers.same(buffer), // <src>
                positions.capture(),           // <position>
                ArgumentMatchers.any(),        // <attachment>
                handlers.capture()             // handler
        );
        // verify, <positions.values[0]> is equal to <position>,
        //         and <positions.values> has no duplicates
        //         and <positions.values> are sorted.

        // verify, <handlers> are all same

        // assert, writtenSoFar.intValue() equals to HelloWorld.BYTES
        Assertions.assertEquals(
                HelloWorld.BYTES,
                writtenSoFar.intValue()
        );
    }

    @畵蛇添足
    @Test
    void _添足_畵蛇(@TempDir final Path dir) throws Exception { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.write(channel, position, attachment, handler)>
        //         will write 12 bytes
        //         , and will invoke <handler.completed(channel, attachment)>
        BDDMockito.willAnswer(i -> {
            final var channel = i.getArgument(0, AsynchronousFileChannel.class);
            final var position = i.getArgument(1, Long.class);
            final var attachment = i.getArgument(2, Void.class);
            @SuppressWarnings({"unchecked"})
            final var handler = (CompletionHandler<AsynchronousFileChannel, Void>) i.getArgument(3);
            final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
            final var accumulator = new LongAccumulator(Long::sum, position);
            channel.write(
                    buffer,
                    accumulator.get(),
                    null,
                    new CompletionHandler<Integer, Void>() {
                        @Override public void completed(final Integer r, final Void a) {
                            log.debug("written: {}", r);
                            accumulator.accumulate(r);
                            if (!buffer.hasRemaining()) {
                                handler.completed(channel, attachment);
                                return;
                            }
                            channel.write(buffer, accumulator.get(), a, this);
                        }
                        @Override public void failed(final Throwable t, final Void a) {
                            handler.failed(t, attachment);
                        }
                    }
            );
            return null;
        }).given(service).write(
                ArgumentMatchers.notNull(AsynchronousFileChannel.class),
                ArgumentMatchers.longThat(p -> p >= 0L),
                ArgumentMatchers.<Void>any(),
                ArgumentMatchers.notNull()
        );
        final var path = Files.createTempFile(dir, null, null);
        final var position = ThreadLocalRandom.current().nextLong(128L);
        // ------------------------------------------------------------------------------------ when
        try (var channel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE)) {
            final var latch = new CountDownLatch(1);
            service.write(
                    channel,
                    position,
                    null,
                    new CompletionHandler<>() {
                        @Override
                        public void completed(final AsynchronousFileChannel r, final Object a) {
                            latch.countDown();
                        }
                        @Override public void failed(final Throwable t, final Object a) {
                            latch.countDown();
                            throw new RuntimeException("failed to write", t);
                        }
                    });
            final var broken = latch.await(1L, TimeUnit.SECONDS);
            assert broken : "not broken";
            channel.force(true);
        }
        // ------------------------------------------------------------------------------------ then
        Assertions.assertEquals(
                position + HelloWorld.BYTES,
                Files.size(path)
        );
    }
    // @formatter:on
}
