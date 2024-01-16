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
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

/**
 * A class for testing
 * {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel, position)")
@Slf4j
class HelloWorld_08_Write_AsynchronousFileChannel_Test extends _HelloWorldTest {

    /**
     * Verifies that the
     * {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)} method
     * throws a {@link NullPointerException} when the {@code channel} argument is {@code null}.
     */
    @DisplayName("""
            should throw a NullPointerException
            when the <channel> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = (AsynchronousFileChannel) null;
        final var position = 0L;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel, position)
        );
    }

    /**
     * Verifies that the
     * {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)} method
     * throws a {@link IllegalArgumentException} when the {@code position} argument is negative.
     */
    @DisplayName("""
            should throw an IllegalArgumentException
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
     * Asserts {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)}
     * method invokes {@link HelloWorld#put(ByteBuffer) put(buffer)} method with a buffer of
     * {@value HelloWorld#BYTES} bytes, and writes the buffer to specified {@code channel} starting
     * at {@code position}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("""
            should invoke put(buffer[12])
            and write the <buffer> to the <channel>"""
    )
    @Test
    void _PutBufferWriteBufferToChannel_() throws InterruptedException, ExecutionException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        BDDMockito.willAnswer(i -> {
                    final var buffer = i.getArgument(0, ByteBuffer.class);
                    buffer.position(buffer.position() + HelloWorld.BYTES);
                    return buffer;
                })
                .given(service)
                .put(ArgumentMatchers.argThat(b -> b != null && b.remaining() >= HelloWorld.BYTES));
        final var writtenSoFar = new LongAdder();
        final var channel = Mockito.mock(AsynchronousFileChannel.class);
        final var previousFutureRef = new AtomicReference<Future<?>>();
        BDDMockito.willAnswer(w -> { // invocation of channel.write
            final var previousFuture = previousFutureRef.get();
            if (previousFuture != null) {
                Mockito.verify(previousFuture, Mockito.times(1)).get();
            }
            final var future = Mockito.mock(Future.class);
            BDDMockito.willAnswer(g -> {
                final var src = w.getArgument(0, ByteBuffer.class);
                final var position = w.getArgument(1, Long.class);
                final var result = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                src.position(src.position() + result);
                writtenSoFar.add(result);
                return result;
            }).given(future).get();
            previousFutureRef.set(future);
            return future;
        }).given(channel).write(
                ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()), // <src>
                ArgumentMatchers.longThat(v -> v >= 0L)                       // <position>
        );
        final var position = ThreadLocalRandom.current().nextLong(8L);
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(channel, position);
        // ------------------------------------------------------------------------------------ then
        final var bufferCaptor = ArgumentCaptor.forClass(ByteBuffer.class);
        Mockito.verify(service, Mockito.times(1)).put(bufferCaptor.capture());
        final var buffer = bufferCaptor.getValue();
        Assertions.assertNotNull(buffer);
        Assertions.assertEquals(HelloWorld.BYTES, buffer.capacity());
        final var positionCaptor = ArgumentCaptor.forClass(long.class);
        Mockito.verify(channel, Mockito.atLeastOnce())
                .write(ArgumentMatchers.same(buffer), positionCaptor.capture()); // <1>
        final var positionArguments = positionCaptor.getAllValues();             // <2>
        Assertions.assertEquals(position, positionArguments.getFirst());         // <3>
        final var lastPosition = positionArguments.stream().reduce((p1, p2) -> { // <4>
            Assertions.assertTrue(p1 < p2);
            return p2;
        });
        Assertions.assertTrue(lastPosition.isPresent());                           // <5>
        Assertions.assertTrue(lastPosition.get() < (position + HelloWorld.BYTES)); // <6>
        Assertions.assertEquals(HelloWorld.BYTES, writtenSoFar.intValue());        // <7>
        Assertions.assertSame(channel, result);
    }

    /**
     * Verifies that the
     * {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)} method
     * writes {@value HelloWorld#BYTES} bytes to the {@code channel} starting at {@code position}.
     *
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("should write 12 bytes to <channel>")
    @Test
    void __(@TempDir final Path tempDir)
            throws IOException, InterruptedException, ExecutionException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        BDDMockito.willAnswer(i -> {
                    final var buffer = i.getArgument(0, ByteBuffer.class);
                    buffer.put("hello, world".getBytes(StandardCharsets.US_ASCII));
                    return buffer;
                })
                .given(service)
                .put(ArgumentMatchers.argThat(b -> b != null && b.remaining() >= HelloWorld.BYTES));
        final var file = Files.createTempFile(tempDir, null, null);
        var position = ThreadLocalRandom.current().nextLong(8L);
        // ------------------------------------------------------------------------------------ when
        try (final var channel = AsynchronousFileChannel.open(file, StandardOpenOption.WRITE)) {
            service.write(channel, position);
            channel.force(true);
        }
        // ------------------------------------------------------------------------------------ then
        Assertions.assertEquals(position + HelloWorld.BYTES, Files.size(file));
        try (final var channel = AsynchronousFileChannel.open(file, StandardOpenOption.READ)) {
            final var dst = ByteBuffer.allocate(HelloWorld.BYTES);
            while (dst.hasRemaining()) {
                position += channel.read(dst, position).get();
            }
            Assertions.assertEquals(Files.size(file), position);
            dst.flip();
            Assertions.assertEquals('h', dst.get());
            Assertions.assertEquals('e', dst.get());
            Assertions.assertEquals('l', dst.get());
            Assertions.assertEquals('l', dst.get());
            Assertions.assertEquals('o', dst.get());
            Assertions.assertEquals(',', dst.get());
            Assertions.assertEquals(' ', dst.get());
            Assertions.assertEquals('w', dst.get());
            Assertions.assertEquals('o', dst.get());
            Assertions.assertEquals('r', dst.get());
            Assertions.assertEquals('l', dst.get());
            Assertions.assertEquals('d', dst.get());
        }
    }
}
