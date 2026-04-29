package com.github.jinahya.hello.api._java_nio_channels;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A class for testing {@link HelloWorld#send(DatagramChannel, SocketAddress) send(channel, target)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorld_Send_DatagramChannel_Target_Test
        extends HelloWorldTest {

    // ---------------------------------------------------------------------------------------------
    @DisplayName("(null, ?)NullPointerException")
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = (DatagramChannel) null;
        final var target = Mockito.mock(SocketAddress.class);
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.send(channel, target)
        );
    }

    @DisplayName("(?, null)NullPointerException")
    @Test
    void _ThrowNullPointerException_TargetIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = Mockito.mock(DatagramChannel.class);
        final var target = (SocketAddress) null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.send(channel, target)
        );
    }

    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var srcRef = new AtomicReference<byte[]>();
        HelloWorldTestUtils.put_buffer_will_put_12_random_bytes(service, srcRef::set);
        final var channel = Mockito.mock(DatagramChannel.class);
        final var target = Mockito.mock(SocketAddress.class);
        final var baos = new ByteArrayOutputStream(HelloWorld.BYTES);
        Mockito.when(channel.send(
                ArgumentMatchers.argThat(v -> v != null && v.remaining() == HelloWorld.BYTES),
                ArgumentMatchers.same(target)
        )).thenAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var dst = new byte[src.remaining()];
            src.get(dst);
            baos.write(dst);
            return dst.length;
        });
        // ------------------------------------------------------------------------------------ when
        final var result = service.send(channel, target);
        // ------------------------------------------------------------------------------------ then
        final var buffer = HelloWorldTestUtils.put_buffer12_invoked_once(service);
//        Assertions.assertArrayEquals(srcRef.get(), baos.toByteArray());
        Assertions.assertSame(channel, result);
    }
}
