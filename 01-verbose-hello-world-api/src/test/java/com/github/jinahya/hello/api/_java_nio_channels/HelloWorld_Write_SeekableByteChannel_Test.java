package com.github.jinahya.hello.api._java_nio_channels;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class for testing {@link HelloWorld#write(SeekableByteChannel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(SeekableByteChannel)")
@Slf4j
class HelloWorld_Write_SeekableByteChannel_Test
        extends HelloWorldTest {

    /**
     * Verifies that the {@link HelloWorld#write(SeekableByteChannel) write(channel)} method throws
     * a {@link NullPointerException} when the {@code channel} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <channel> argument is <null>""")
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final SeekableByteChannel channel = null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel)
        );
    }

    /**
     * Verifies that the {@link HelloWorld#write(SeekableByteChannel) write(channel)} method
     * delegates to {@link HelloWorld#write(WritableByteChannel) write(WritableByteChannel)}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("should delegate to <write(WritableByteChannel)>")
    @Test
    void __()
            throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = Mockito.mock(SeekableByteChannel.class);
        Mockito.when(channel.write(Mockito.any(ByteBuffer.class))).thenAnswer(i -> {
            final var buffer = i.getArgument(0, ByteBuffer.class);
            final var remaining = buffer.remaining();
            buffer.position(buffer.limit());
            return remaining;
        });
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(channel);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(service, Mockito.times(1)).write((WritableByteChannel) channel);
        Assertions.assertSame(channel, result);
    }

    /**
     * Writes the hello-world-bytes to a temporary file via a {@link SeekableByteChannel} and reads
     * them back to verify the content.
     *
     * @param tempDir the temporary directory.
     * @throws IOException if an I/O error occurs.
     */
    @畵蛇添足
    @Test
    void _添足_畵蛇(@TempDir final Path tempDir)
            throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        HelloWorldTestUtils.put_buffer_will_put_actual_hello_world_bytes(service);
        final var file = Files.createTempFile(tempDir, null, null);
        final var position = ThreadLocalRandom.current().nextLong(128L);
        // ----------------------------------------------------------------------------------- write
        try (var channel = Files.newByteChannel(file, StandardOpenOption.WRITE)) {
            channel.position(position);
            service.write(channel);
        }
        Assertions.assertEquals(position + HelloWorld.BYTES, Files.size(file));
        // ------------------------------------------------------------------------------------ read
        try (var channel = Files.newByteChannel(file, StandardOpenOption.READ)) {
            channel.position(position);
            final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
            while (buffer.hasRemaining()) {
                channel.read(buffer);
            }
            Assertions.assertArrayEquals(
                    HelloWorldTestUtils.getHelloWorldArray(),
                    buffer.array()
            );
        }
    }
}
