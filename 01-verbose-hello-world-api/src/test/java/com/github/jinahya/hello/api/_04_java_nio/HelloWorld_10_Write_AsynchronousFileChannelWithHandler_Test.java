package com.github.jinahya.hello.api._04_java_nio;

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

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.畵蛇添足;
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
import java.util.HashSet;
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
            should throw a <NullPointerException>
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
            should throw an <IllegalArgumentException>
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
            should throw a <NullPointerException>
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
            should invoke <put(buffer[12])>
            and write the <buffer> to the <channel> starting at <position>""")
    @Test
    @SuppressWarnings({"unchecked"})
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub: <service.put(buffer)> will increase the <buffer>'s <position> by <12>
        stub_put_buffer_will_increase_buffer_position_by_12();
        final var written = new LongAdder(); // the number of bytes written to the <channel>
        // prepare: a mock object of <AsynchronousFileChannel>
        final var channel = Mockito.mock(AsynchronousFileChannel.class,
                                         Mockito.withSettings().verboseLogging());
        // stub: <channel.write(src, position, attachment, handler)>
        //       will increase the <src>'s <position> by a random value,
        //       and will invoke <handler.completed(result, attachment)>
        Mockito.doAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            assert src.hasRemaining();
            final var position = i.getArgument(1, Long.class);
            assert position >= 0L;
            final var attachment = i.getArgument(2);
            final var handler = i.getArgument(3, CompletionHandler.class);
            assert handler != null;
            final var result = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
            src.position(src.position() + result);
            written.add(result);
            handler.completed(result, attachment);
            return null;
        }).when(channel).write(
                ArgumentMatchers.argThat(s -> s != null && s.hasRemaining()), // <src>
                ArgumentMatchers.longThat(p -> p >= 0L),                      // <position>
                ArgumentMatchers.any(),                                       // <attachment>
                ArgumentMatchers.notNull()                                    // <handler>
        );
        // prepare: a random <position>
        final var position = ThreadLocalRandom.current().nextLong(128L);
        // prepare: a random <attachment>; null or non-null
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        // prepare: a mock object of <CompletionHandler>
        final var handler = Mockito.mock(CompletionHandler.class,
                                         Mockito.withSettings().verboseLogging());
        // ------------------------------------------------------------------------------------ when
        service.write(channel, position, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        // verify: <put(buffer[12])> invoked, once
        final var buffer = verify_put_buffer12_invoked_once();
        // await: <handler> to be <completed(channel, attachment)>
        Mockito.verify(handler, Mockito.timeout(TimeUnit.SECONDS.toMillis(64L)).times(1))
                .completed(channel, attachment);
        // verify: <channel.write(buffer, captured, any, captured)> invoked, at least once
        final var positionCaptor = ArgumentCaptor.forClass(long.class);
        final var handlerCaptor = ArgumentCaptor.forClass(CompletionHandler.class);
        Mockito.verify(channel, Mockito.atLeastOnce()).write(
                ArgumentMatchers.same(buffer), // <src>
                positionCaptor.capture(),      // <position>
                ArgumentMatchers.any(),        // <attachment>
                handlerCaptor.capture()        // <handler>
        );
        // verify: all <position>s are sorted and has no duplicates.
        final var positions = positionCaptor.getAllValues();
        final var last = positions.stream().reduce((p1, p2) -> {
            Assertions.assertTrue(p2 > p1); // why?
            return p2;
        });
        assert last.isPresent();
        Assertions.assertTrue(last.get() < position + HelloWorld.BYTES);
        // verify: <handler>s are all the same
        final var handlers = handlerCaptor.getAllValues();
        Assertions.assertEquals(1, new HashSet<>(handlers).size());
        // assert: <12> bytes have been written to the <channel>
        Assertions.assertEquals(
                HelloWorld.BYTES,
                written.intValue()
        );
    }

    @畵蛇添足
    @Test
    void _添足_畵蛇(@TempDir final Path dir) throws Exception { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub: <service.write(channel, position, attachment, handler)>
        //       will write 12 bytes,
        //       and will invoke <handler.completed(channel, attachment)>
        BDDMockito.willAnswer(i -> {
            final var channel = i.getArgument(0, AsynchronousFileChannel.class);
            final var position = i.getArgument(1, Long.class);
            final var attachment = i.getArgument(2, Void.class);
            @SuppressWarnings({"unchecked"})
            final var handler = (CompletionHandler<AsynchronousFileChannel, Void>) i.getArgument(3);
            final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
            final var accumulator = new LongAccumulator(Long::sum, position);
            channel.write(
                    buffer,                                  // <src>
                    accumulator.get(),                       // <position>
                    null,                                    // <attachment>
                    new CompletionHandler<Integer, Void>() { // <handler>
                        @Override public void completed(final Integer r, final Void a) {
                            log.debug("completed({}, {})", r, a);
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
                    channel,                    // <channel>
                    position,                   // <position>
                    null,                       // <attachment>
                    new CompletionHandler<>() { // <handler>
                        @Override
                        public void completed(final AsynchronousFileChannel r, final Object a) {
                            latch.countDown();
                        }
                        @Override public void failed(final Throwable t, final Object a) {
                            latch.countDown();
                            throw new RuntimeException("failed to write", t);
                        }
                    });
            final var broken = latch.await(64L, TimeUnit.SECONDS);
            assert broken : "the latch hasn't been broken";
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
