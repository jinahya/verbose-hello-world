package com.github.jinahya.hello.api._java_nio_channels;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.Pipe;
import java.nio.channels.ScatteringByteChannel;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class for testing {@link HelloWorld#write(GatheringByteChannel)} method.
 */
@DisplayName("write(GatheringByteChannel)")
@Slf4j
class HelloWorld_Write_GatheringByteChannel_Test extends HelloWorldTest {

    /**
     * Verifies that the {@link HelloWorld#write(GatheringByteChannel) write(channel)} method throws
     * a {@link NullPointerException} when the {@code channel} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <channel> argument is <null>""")
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final GatheringByteChannel channel = null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel)
        );
    }

    /**
     * Verifies that the {@link HelloWorld#write(GatheringByteChannel) write(channel)} method
     * invokes {@link HelloWorld#put(ByteBuffer) put(buffer)} method and
     * {@link GatheringByteChannel#write(ByteBuffer[]) channel.write(srcs)} with the buffer.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("should invoke <put(buffer)> and <channel.write(srcs)>")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        HelloWorldTestUtils.stub_put_buffer_will_put_actual_hello_world_bytes(service);
        final var channel = Mockito.mock(GatheringByteChannel.class);
        Mockito.when(channel.write(Mockito.any(ByteBuffer[].class)))
                .thenAnswer(i -> {
                    final var srcs = i.getArgument(0, ByteBuffer[].class);
                    for (final var src : srcs) {
                        if (!src.hasRemaining()) {
                            continue;
                        }
                        final var drain = ThreadLocalRandom.current()
                                .nextInt(1, src.remaining() + 1);
                        src.position(src.position() + drain);
                        return (long) drain;
                    }
                    return Assertions.fail("no remaining bytes in any source buffer");
                });
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(channel);
        // ------------------------------------------------------------------------------------ then
        final var buffer = HelloWorldTestUtils.verify_put_buffer12_invoked_once(service);
        final var captor = ArgumentCaptor.forClass(ByteBuffer[].class);
        Mockito.verify(channel, Mockito.atLeastOnce()).write(captor.capture());
        for (final var srcs : captor.getAllValues()) {
            Assertions.assertEquals(1, srcs.length);
            Assertions.assertSame(buffer, srcs[0]);
        }
        Assertions.assertSame(channel, result);
    }

    /**
     * Writes the hello-world-bytes through a {@link Pipe} using a {@link GatheringByteChannel}
     * (sink) and reads them back using a {@link ScatteringByteChannel} (source).
     *
     * @throws IOException if an I/O error occurs.
     */
    @畵蛇添足
    @Test
    void _添足_畵蛇() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        HelloWorldTestUtils.stub_put_buffer_will_put_actual_hello_world_bytes(service);
        // ----------------------------------------------------------------------------- when / then
        final var pipe = Pipe.open();
        // ----------------------------------------------------------------------------------- write
        Thread.ofVirtual().start(() -> {
            try (var sink = pipe.sink()) {
                final var result = service.write(sink);
                Assertions.assertSame(sink, result);
            } catch (final IOException ioe) {
                log.error("failed to write", ioe);
            }
        });
        // ------------------------------------------------------------------------------------ read
        try (var source = pipe.source()) {
            final var dsts = new ByteBuffer[] {
                    ByteBuffer.allocate(5),  // "hello"
                    ByteBuffer.allocate(2),  // ", "
                    ByteBuffer.allocate(5)   // "world"
            };
            for (var r = (long) HelloWorld.BYTES; r > 0; ) {
                r -= source.read(dsts);
            }
            for (final var dst : dsts) {
                dst.flip();
            }
            final var result = new byte[HelloWorld.BYTES];
            var offset = 0;
            for (final var dst : dsts) {
                final var length = dst.remaining();
                dst.get(result, offset, length);
                offset += length;
            }
            Assertions.assertArrayEquals(
                    HelloWorldTestUtils.getHelloWorldBytes(),
                    result
            );
        }
    }
}
