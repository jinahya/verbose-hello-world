package com.github.jinahya.hello.api._java_nio_channels;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

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

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void beforeEach() {
        HelloWorldTestUtils.put_buffer_will_put_actual_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    class DatagramSocket_Server_Test {

        private void startServer(final CompletableFuture<SocketAddress> address) {
            Thread.ofVirtual().start(() -> {
                try (var server = new DatagramSocket(
                        new InetSocketAddress(InetAddress.getLocalHost(), 0))) {
                    assert server.isBound();
                    address.complete(server.getLocalSocketAddress());
                    final var packet = new DatagramPacket(new byte[HelloWorld.BYTES],
                                                          HelloWorld.BYTES);
                    server.receive(packet);
                    log.debug("received: {}",
                              new String(packet.getData(), StandardCharsets.US_ASCII));
                } catch (final IOException ioe) {
                    log.error("failed to start/receive server");
                    address.completeExceptionally(ioe);
                }
            });
        }

        @Test
        void __blocking()
                throws IOException {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var address = new CompletableFuture<SocketAddress>();
            startServer(address);
            try (var client = Mockito.spy(DatagramChannel.open())) {
                final var target = address.join();
                // ---------------------------------------------------------------------------- when
                final var result = service.send(client, target);
                // ---------------------------------------------------------------------------- then
                final var buffer = HelloWorldTestUtils.put_buffer12_invoked_once(service);
                Mockito.verify(client, Mockito.times(1)).send(buffer, target);
                Assertions.assertSame(client, result);
            }
        }

        @Test
        void __nonblocking()
                throws IOException {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var address = new CompletableFuture<SocketAddress>();
            startServer(address);
            try (var client = Mockito.spy(DatagramChannel.open())) {
                client.configureBlocking(false);
                final var target = address.join();
                // ---------------------------------------------------------------------------- when
                final var result = service.send(client, target);
                // ---------------------------------------------------------------------------- then
                final var buffer = HelloWorldTestUtils.put_buffer12_invoked_once(service);
                Mockito.verify(client, Mockito.times(1)).send(buffer, target);
                Assertions.assertSame(client, result);
            }
        }
    }

    @Nested
    class DatagramChannel_Server_Blocking_Test {

        private void startServer(final CompletableFuture<SocketAddress> address) {
            Thread.ofVirtual().start(() -> {
                try (var server = DatagramChannel.open()) {
                    server.bind(new InetSocketAddress(InetAddress.getLocalHost(), 0));
                    address.complete(server.getLocalAddress());
                    final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
                    server.receive(buffer);
                    log.debug("received: {}", StandardCharsets.US_ASCII.decode(buffer.flip()));
                } catch (final IOException ioe) {
                    log.error("failed to start/receive server");
                    address.completeExceptionally(ioe);
                }
            });
        }

        @Test
        void __blocking()
                throws IOException {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var address = new CompletableFuture<SocketAddress>();
            startServer(address);
            try (var client = Mockito.spy(DatagramChannel.open())) {
                final var target = address.join();
                // ---------------------------------------------------------------------------- when
                final var result = service.send(client, target);
                // ---------------------------------------------------------------------------- then
                final var buffer = HelloWorldTestUtils.put_buffer12_invoked_once(service);
                Mockito.verify(client, Mockito.times(1)).send(buffer, target);
                Assertions.assertSame(client, result);
            }
        }

        @Test
        void __nonblocking()
                throws IOException {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var address = new CompletableFuture<SocketAddress>();
            startServer(address);
            try (var client = Mockito.spy(DatagramChannel.open())) {
                client.configureBlocking(false);
                final var target = address.join();
                // ---------------------------------------------------------------------------- when
                final var result = service.send(client, target);
                // ---------------------------------------------------------------------------- then
                final var buffer = HelloWorldTestUtils.put_buffer12_invoked_once(service);
                Mockito.verify(client, Mockito.times(1)).send(buffer, target);
                Assertions.assertSame(client, result);
            }
        }
    }

    @Nested
    class DatagramChannel_Server_Nonblocking_Test {

        private void startServer(final CompletableFuture<SocketAddress> address) {
            Thread.ofVirtual().start(() -> {
                try (var selector = Selector.open();
                     var server = DatagramChannel.open()) {
                    server.bind(new InetSocketAddress(InetAddress.getLocalHost(), 0));
                    address.complete(server.getLocalAddress());
                    server.configureBlocking(false);
                    final var key = server.register(selector, SelectionKey.OP_READ);
                    selector.select();
                    assert selector.selectedKeys().iterator().next() == key;
                    final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
                    server.receive(buffer);
                    log.debug("received: {}", StandardCharsets.US_ASCII.decode(buffer.flip()));
                } catch (final IOException ioe) {
                    log.error("failed to start/receive server");
                    address.completeExceptionally(ioe);
                }
            });
        }

        @Test
        void __blocking()
                throws IOException {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var address = new CompletableFuture<SocketAddress>();
            startServer(address);
            try (var client = Mockito.spy(DatagramChannel.open())) {
                final var target = address.join();
                // ---------------------------------------------------------------------------- when
                final var result = service.send(client, target);
                // ---------------------------------------------------------------------------- then
                final var buffer = HelloWorldTestUtils.put_buffer12_invoked_once(service);
                Mockito.verify(client, Mockito.times(1)).send(buffer, target);
                Assertions.assertSame(client, result);
            }
        }

        @Test
        void __nonblocking()
                throws IOException {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var address = new CompletableFuture<SocketAddress>();
            startServer(address);
            try (var client = Mockito.spy(DatagramChannel.open())) {
                client.configureBlocking(false);
                final var target = address.join();
                // ---------------------------------------------------------------------------- when
                final var result = service.send(client, target);
                // ---------------------------------------------------------------------------- then
                final var buffer = HelloWorldTestUtils.put_buffer12_invoked_once(service);
                Mockito.verify(client, Mockito.times(1)).send(buffer, target);
                Assertions.assertSame(client, result);
            }
        }
    }
}
