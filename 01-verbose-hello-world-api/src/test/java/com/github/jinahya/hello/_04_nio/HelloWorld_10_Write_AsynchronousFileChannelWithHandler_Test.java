package com.github.jinahya.hello._04_nio;

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
import com.github.jinahya.hello._HelloWorldTest;
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
import java.util.concurrent.atomic.LongAdder;

/**
 * A class for testing
 * {@link HelloWorld#write(AsynchronousFileChannel, long, CompletionHandler, Object) write(channel,
 * position, handler, attachment)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel, position, handler, attachment)")
@Slf4j
class HelloWorld_10_Write_AsynchronousFileChannelWithHandler_Test extends _HelloWorldTest {

    /**
     * Verifies that the
     * {@link HelloWorld#write(AsynchronousFileChannel, long, CompletionHandler, Object)
     * write(channel, position, handler, attachment)} method throws a {@link NullPointerException}
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
        var service = service();
        var channel = (AsynchronousFileChannel) null;
        var position = 0L;
        var handler = Mockito.mock(CompletionHandler.class);
        var attachment = (Object) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel, position, handler, attachment)
        );
    }

    /**
     * Verifies that the
     * {@link HelloWorld#write(AsynchronousFileChannel, long, CompletionHandler, Object)
     * write(channel, position, handler, attachment)} method throws an
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
        var service = service();
        var channel = Mockito.mock(AsynchronousFileChannel.class);
        var position = ThreadLocalRandom.current().nextLong() | Long.MIN_VALUE;
        var handler = Mockito.mock(CompletionHandler.class);
        var attachment = (Object) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.write(channel, position, handler, attachment)
        );
    }

    /**
     * Verifies that the
     * {@link HelloWorld#write(AsynchronousFileChannel, long, CompletionHandler, Object)
     * write(channel, position, handler, attachment)} method throws a {@link NullPointerException}
     * when the {@code handler} argument is {@code null}.
     */
    @DisplayName("""
            should throw a NullPointerException
            when the <handler> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_HandlerIsNull() {
        // ----------------------------------------------------------------------------------- given
        var service = service();
        var channel = Mockito.mock(AsynchronousFileChannel.class);
        var position = 0L;
        var handler = (CompletionHandler<AsynchronousFileChannel, Object>) null;
        var attachment = (Object) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel, position, handler, attachment)
        );
    }

    /**
     * Asserts
     * {@link HelloWorld#write(AsynchronousFileChannel, long, CompletionHandler, Object)
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
        // DONE: service.put(buffer) will increase the buffer's position by HelloWorld.BYTES
        BDDMockito.willAnswer(i -> {
                    final var buffer = i.getArgument(0, ByteBuffer.class);
                    buffer.position(buffer.position() + HelloWorld.BYTES);
                    return buffer;
                })
                .given(service)
                .put(ArgumentMatchers.argThat(b -> b != null && b.remaining() >= HelloWorld.BYTES));
        final var writtenSoFar = new LongAdder();
        final var channel = Mockito.mock(AsynchronousFileChannel.class);
        // DONE: channel.write(src, position, attachment, handler) will invoke handler.completed
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
        final var position = 0L;
        final var handler = Mockito.mock(CompletionHandler.class);
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        // ------------------------------------------------------------------------------------ when
        service.write(channel, position, handler, attachment);
        // ------------------------------------------------------------------------------------ then
        // DONE: verify, put(buffer[12]) invoked
        final var bufferCaptor = ArgumentCaptor.forClass(ByteBuffer.class);
        Mockito.verify(service, Mockito.times(1)).put(bufferCaptor.capture());
        final var buffer = bufferCaptor.getValue();
        Assertions.assertNotNull(buffer);
        Assertions.assertEquals(HelloWorld.BYTES, buffer.capacity());
        // DONE: await, handler to be completed(channel, attachment)
        Mockito.verify(handler, Mockito.timeout(TimeUnit.SECONDS.toMillis(8L)).times(1))
                .completed(channel, attachment);
        // DONE: assert, writtenSoFar.intValue() equals to HelloWorld.BYTES
        Assertions.assertEquals(HelloWorld.BYTES, writtenSoFar.intValue());
    }

    /**
     * Verifies that the
     * {@link HelloWorld#write(AsynchronousFileChannel, long, CompletionHandler, Object)
     * write(channel, position, handler, attachment)} method writes {@value HelloWorld#BYTES} bytes
     * to the {@code channel} starting at {@code position}.
     *
     * @throws Exception if any thrown.
     */
    @DisplayName("should write 12 bytes to <path> starting at <position>")
    @畵蛇添足
    @Test
    void __(@TempDir final Path tempDir) throws Exception { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // DONE: service.put(buffer) will increase the buffer's position by HelloWorld.BYTES
        BDDMockito.willAnswer(i -> {
                    log.debug("\tservice.put({})", (Object) i.getArgument(0));
                    final var buffer = i.getArgument(0, ByteBuffer.class);
                    buffer.position(buffer.position() + HelloWorld.BYTES);
                    return buffer;
                })
                .given(service)
                .put(ArgumentMatchers.argThat(b -> b != null && b.remaining() >= HelloWorld.BYTES));
        // DONE: service.write(channel, position, handler, attachment) will call real method
        BDDMockito.willAnswer(i -> {
            log.debug("service.write({}, {}, {}, {})", i.getArgument(0), i.getArgument(1),
                      i.getArgument(2), i.getArgument(3));
            return i.callRealMethod();
        }).given(service).write(
                ArgumentMatchers.notNull(), // <channel>
                ArgumentMatchers.anyLong(), // <position>
                ArgumentMatchers.notNull(), // <handler>
                ArgumentMatchers.any()      // <attachment>
        );
        final var path = Files.createTempFile(tempDir, null, null);
        log.debug("path: {} ({})", path, Files.size(path));
        final var position = ThreadLocalRandom.current().nextLong(8L);
        log.debug("position: {}", position);
        // ------------------------------------------------------------------------------------ when
        try (final var channel =
                     Mockito.spy(AsynchronousFileChannel.open(path, StandardOpenOption.WRITE))) {
            BDDMockito.willAnswer(i -> String.format("channel@%08x", hashCode()))
                    .given(channel).toString();
            // DONE: channel.write(src, position, attachment, handler) will log
            BDDMockito.willAnswer(i -> {
                @SuppressWarnings({"unchecked"})
                final var handler = (CompletionHandler<Integer, Object>)
                        Mockito.spy(i.getArgument(3, CompletionHandler.class));
                BDDMockito.willReturn(String.format("handler@%08x", handler.hashCode()))
                        .given(handler).toString();
                // DONE: handler.completed(result, attachment) will log
                BDDMockito.willAnswer(j -> {
                            log.debug("{}.onComplete({}, {})", handler,
                                      j.getArgument(0), j.getArgument(1));
                            return j.callRealMethod();
                        }).given(handler)
                        .completed(ArgumentMatchers.any(), ArgumentMatchers.any());
                i.getArguments()[3] = handler;
                log.debug("\t{}.write({}, {}, {}, {})", channel,
                          i.getArgument(0), i.getArgument(1), i.getArgument(2), i.getArgument(3));
                return i.callRealMethod();
            }).given(channel).write(
                    ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()), // <src>
                    ArgumentMatchers.longThat(p -> p >= 0L),                      // <position>
                    ArgumentMatchers.any(),                                       // <attachment>
                    ArgumentMatchers.notNull()                                    // <handler>
            );
            final var handled = new CountDownLatch(1);
            final var handler = Mockito.spy(
                    new CompletionHandler<AsynchronousFileChannel, Object>() {
                        @Override public String toString() {
                            return String.format("handler@%08x", hashCode());
                        }
                        @Override public void completed(final AsynchronousFileChannel result,
                                                        final Object attachment) {
                            log.debug("\t{}.completed({}, {})", this, result,
                                      attachment);
                            handled.countDown();
                        }
                        @Override public void failed(final Throwable exc, final Object attachment) {
                            log.error("failed to write", exc);
                            handled.countDown();
                        }
                    }
            );
            service.write(
                    channel,  // <channel>
                    position, // <position>
                    handler,  // <handler>
                    null      // <attachment>
            );
            handled.await();
        }
        // ------------------------------------------------------------------------------------ then
        // verify, file.size is equal to <position> + HelloWorld.BYTES
        Assertions.assertEquals(position + HelloWorld.BYTES, Files.size(path));
    } // @formatter:on
}
