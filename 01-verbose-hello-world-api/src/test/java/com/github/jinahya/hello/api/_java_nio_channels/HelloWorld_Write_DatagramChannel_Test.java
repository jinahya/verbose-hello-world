package com.github.jinahya.hello.api._java_nio_channels;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.DatagramSocket;
import java.nio.channels.DatagramChannel;
import java.nio.channels.WritableByteChannel;

/**
 * A class for testing {@link HelloWorld#write(DatagramChannel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorld_Write_DatagramChannel_Test
        extends HelloWorldTest {

    // ---------------------------------------------------------------------------------------------
    @DisplayName("(null)NullPointerException")
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = (DatagramChannel) null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(channel)
        );
    }

    @DisplayName("(!connected)IllegalArgumentException")
    @Test
    void _ThrowIllegalArgumentException_ChannelIsNotConnected() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = Mockito.mock(DatagramChannel.class);
        assert !channel.isConnected();
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.write(channel)
        );
    }

    @Test
    void __ChannelIsBlocking() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = Mockito.mock(DatagramChannel.class);
        Mockito.when(channel.isConnected()).thenReturn(true);
        Mockito.when(channel.isBlocking()).thenReturn(true);
        final var socket = Mockito.mock(DatagramSocket.class);
        Mockito.when(channel.socket()).thenReturn(socket);
        Mockito.when(socket.getChannel()).thenReturn(channel);
        Mockito.doReturn(socket).when(service).send(
                ArgumentMatchers.<DatagramSocket>same(socket)
        );
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(channel);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(service, Mockito.times(1)).send(
                ArgumentMatchers.<DatagramSocket>same(socket)
        );
        Assertions.assertSame(channel, result);
    }

    @Test
    void __ChannelIsNotBlocking() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = Mockito.mock(DatagramChannel.class);
        Mockito.when(channel.isConnected()).thenReturn(true);
        Mockito.when(channel.isBlocking()).thenReturn(false);
        Mockito.doReturn(channel).when(service).write(
                ArgumentMatchers.<WritableByteChannel>same(channel)
        );
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(channel);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(service, Mockito.times(1)).write(
                ArgumentMatchers.<WritableByteChannel>same(channel)
        );
        Assertions.assertSame(channel, result);
    }
}
