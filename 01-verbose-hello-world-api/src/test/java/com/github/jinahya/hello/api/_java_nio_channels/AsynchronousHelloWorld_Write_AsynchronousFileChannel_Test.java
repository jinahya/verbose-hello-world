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

import com.github.jinahya.hello.api.DefaultAsynchronousHelloWorldTest;
import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
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
 * {@link com.github.jinahya.hello.api.AsynchronousHelloWorld#write(AsynchronousFileChannel, long,
 * Object, CompletionHandler) write(channel, position, attachment, handler)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel, position)")
@Slf4j
class AsynchronousHelloWorld_Write_AsynchronousFileChannel_Test
        extends DefaultAsynchronousHelloWorldTest {

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
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        final var handler = Mockito.mock(CompletionHandler.class);
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel, position, attachment, handler)
        );
    }

    /**
     * Verifies that the
     * {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)} method
     * throws a {@link IllegalArgumentException} when the {@code position} argument is negative.
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
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        final var handler = Mockito.mock(CompletionHandler.class);
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.write(channel, position, attachment, handler)
        );
    }

    /**
     * Verifies that the
     * {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)} method
     * throws a {@link NullPointerException} when the {@code handler} argument is {@code null}.
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
        final var position = ThreadLocalRandom.current().nextLong() >>> 1;
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        final var handler = (CompletionHandler<AsynchronousFileChannel, Object>) null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel, position, attachment, handler)
        );
    }

    /**
     * Asserts {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)}
     * method invokes {@link HelloWorld#put(ByteBuffer) put(buffer)} method with a buffer of
     * {@value HelloWorld#BYTES} bytes, and writes the buffer to specified {@code channel} starting
     * at {@code position}.
     *
     * @throws InterruptedException if interrupted while testing.
     */
    @DisplayName("""
            should invoke <put(buffer[12])>
            and write the <buffer> to the <channel>"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void _PutBufferWriteBufferToChannel_() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = Mockito.mock(AsynchronousFileChannel.class);
        final var position = ThreadLocalRandom.current().nextLong(1024L);
        final var written = new LongAdder();
        Mockito.doAnswer(i -> {
            final var s = i.getArgument(0, ByteBuffer.class);
            final var p = i.getArgument(1, Long.class);
            final var a = i.getArgument(2);
            final var h = i.getArgument(3, CompletionHandler.class);
            Thread.ofPlatform().start(() -> {
                final var result = ThreadLocalRandom.current().nextInt(s.remaining()) + 1;
                s.position(s.position() + result);
                written.add(result);
                h.completed(result, a);
            });
            return null;
        }).when(channel).write(
                ArgumentMatchers.notNull(), // <src>
                ArgumentMatchers.anyLong(), // <position>
                ArgumentMatchers.any(),     // <attachment>
                ArgumentMatchers.notNull()  // <handler>
        );
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        final var handler = Mockito.mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ when
        service.write(channel, position, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(handler, Mockito.timeout(TimeUnit.SECONDS.toMillis(8L)).times(1))
                .completed(channel, attachment);
        final var srcCaptor = ArgumentCaptor.forClass(ByteBuffer.class);
        final var handlerCaptor = ArgumentCaptor.forClass(CompletionHandler.class);
        Mockito.verify(channel, Mockito.atLeastOnce()).write(
                srcCaptor.capture(),        // <src>
                ArgumentMatchers.anyLong(), // <position>
                ArgumentMatchers.any(),     // <attachment>
                handlerCaptor.capture()     // <handler>
        );
        final var srcs = srcCaptor.getAllValues();
        srcs.forEach(s -> Assertions.assertSame(srcs.getFirst(), s));
        final var handlers = handlerCaptor.getAllValues();
        handlers.forEach(h -> Assertions.assertSame(handlers.getFirst(), h));
        Assertions.assertEquals(HelloWorld.BYTES, written.intValue());
    }

    @畵蛇添足("testing with a real file doesn't add any value")
    @Test
    void _添足_畵蛇(@TempDir final Path dir) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var file = Files.createTempFile(dir, null, null);
        final var position = ThreadLocalRandom.current().nextLong(1024L);
        final var latch = new CountDownLatch(1);
        // ------------------------------------------------------------------------------------ when
        try (var channel = AsynchronousFileChannel.open(file, StandardOpenOption.WRITE)) {
            service.write(channel, position, null, new CompletionHandler<>() { // @formatter:off
                @Override public void completed(final AsynchronousFileChannel c, final Object a) {
                    log.debug("written to {}", file);
                    Assertions.assertSame(channel, c);
                    latch.countDown();
                }
                @Override public void failed(final Throwable exc, final Object a) {
                    log.error("failed to write", exc);
                    latch.countDown();
                } // @formatter:on
            });
            Assertions.assertTrue(
                    latch.await(8L, TimeUnit.SECONDS),
                    "write did not complete in time"
            );
        }
        // ------------------------------------------------------------------------------------ then
        Assertions.assertEquals(position + HelloWorld.BYTES, Files.size(file));
    }
}
