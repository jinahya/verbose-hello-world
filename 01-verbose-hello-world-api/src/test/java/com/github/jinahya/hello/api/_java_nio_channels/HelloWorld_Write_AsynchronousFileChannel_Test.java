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

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.LongAdder;

/**
 * A class for testing
 * {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel, position)")
@Slf4j
class HelloWorld_Write_AsynchronousFileChannel_Test
        extends HelloWorldTest {

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
        // assert: <service.write(channel, position)> throws a <NullPointerException>
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
        // assert: <service.write(channel, position)> throws an <IllegalArgumentException>
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
            should invoke <put(buffer[12])>
            and write the <buffer> to the <channel>"""
    )
    @Test
    void __()
            throws InterruptedException, IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.put_buffer_will_increase_buffer_position_by_12(
                service()
        );
        final var channel = Mockito.mock(AsynchronousFileChannel.class,
                                         Mockito.withSettings().verboseLogging());
        final var written = new LongAdder(); // total number of bytes written to the <channel>
        Mockito.doAnswer(w -> {
            final var future = Mockito.mock(Future.class);
            Mockito.doAnswer(g -> {
                final var src = w.getArgument(0, ByteBuffer.class);
                assert src.hasRemaining();
                final var position = w.getArgument(1, Long.class);
                assert position >= 0L;
                final var result = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                assert result > 0;
                assert result <= src.remaining();
                src.position(src.position() + result);
                written.add(result);
                return result;
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
        final var captor = ArgumentCaptor.forClass(long.class);
        Mockito.verify(channel, Mockito.atLeastOnce())
                .write(ArgumentMatchers.same(buffer), captor.capture());  // <1>
        final var positions = captor.getAllValues();                      // <2>
        Assertions.assertEquals(position, positions.getFirst());          // <3>
        final var last = positions.stream().reduce((p1, p2) -> {          // <4>
            Assertions.assertTrue(p2 > p1);
            return p2;
        });
        Assertions.assertTrue(last.isPresent());                           // <5>
        Assertions.assertTrue(last.get() < (position + HelloWorld.BYTES)); // <6>
        Assertions.assertEquals(HelloWorld.BYTES, written.intValue());
        Assertions.assertFalse(buffer.hasRemaining());
        Assertions.assertSame(channel, result);
    }

    @畵蛇添足("testing with a real file doesn't add any value")
    @Test
    void _添足_畵蛇(@TempDir final Path dir)
            throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.write(channel, position)> will write <12> bytes starting at <position>
        Mockito.doAnswer(i -> {
            final var channel = i.getArgument(0, AsynchronousFileChannel.class);
            var position = i.getArgument(1, Long.class);
            log.debug("write({}, {})", channel, position);
            for (final var b = new_hello_world_buffer(); b.hasRemaining(); ) {
                final var future = channel.write(b, position);
                final var written = future.get();
                log.debug("written: {}", written);
                position += written;
            }
            return channel;
        }).when(service).write(
                ArgumentMatchers.notNull(),             // <channel>
                ArgumentMatchers.longThat(p -> p >= 0L) // <position>
        );
        final var path = Files.createTempFile(dir, null, null);
        final var position = ThreadLocalRandom.current().nextLong(8L);
        // ------------------------------------------------------------------------------------ when
        try (var channel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE)) {
            final var result = service.write(channel, position);
            Assertions.assertSame(channel, result);
            result.force(false);
        }
        // ------------------------------------------------------------------------------------ then
        // assert, <12> bytes written starting at <position>
        final var size = Files.size(path);
        log.debug("path.size: {}", size);
        Assertions.assertEquals(position + HelloWorld.BYTES, size);
    }
}
