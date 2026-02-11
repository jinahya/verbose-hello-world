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

@Slf4j
class HelloWorld_Write_DatagramChannel_Test extends HelloWorldTest {

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

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void beforeEach() {
        HelloWorldTestUtils.stub_put_buffer_will_put_actual_hello_world_bytes(service());
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
        void __blocking() throws IOException {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var address = new CompletableFuture<SocketAddress>();
            startServer(address);
            try (var client = Mockito.spy(DatagramChannel.open())) {
                client.connect(address.join());
                // ---------------------------------------------------------------------------- when
                final var result = service.write(client);
                // ---------------------------------------------------------------------------- then
                final var buffer = HelloWorldTestUtils.verify_put_buffer12_invoked_once(service);
                Mockito.verify(client, Mockito.times(1)).write(buffer);
                Assertions.assertSame(client, result);
            }
        }

        @Test
        void __nonblocking() throws IOException {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var address = new CompletableFuture<SocketAddress>();
            startServer(address);
            try (var client = Mockito.spy(DatagramChannel.open())) {
                client.configureBlocking(false);
                client.connect(address.join());
                // ---------------------------------------------------------------------------- when
                final var result = service.write(client);
                // ---------------------------------------------------------------------------- then
                final var buffer = HelloWorldTestUtils.verify_put_buffer12_invoked_once(service);
                Mockito.verify(client, Mockito.times(1)).write(buffer);
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
        void __blocking() throws IOException {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var address = new CompletableFuture<SocketAddress>();
            startServer(address);
            try (var client = Mockito.spy(DatagramChannel.open())) {
                client.connect(address.join());
                // ---------------------------------------------------------------------------- when
                final var result = service.write(client);
                // ---------------------------------------------------------------------------- then
                final var buffer = HelloWorldTestUtils.verify_put_buffer12_invoked_once(service);
                Mockito.verify(client, Mockito.times(1)).write(buffer);
                Assertions.assertSame(client, result);
            }
        }

        @Test
        void __nonblocking() throws IOException {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var address = new CompletableFuture<SocketAddress>();
            startServer(address);
            try (var client = Mockito.spy(DatagramChannel.open())) {
                client.configureBlocking(false);
                client.connect(address.join());
                // ---------------------------------------------------------------------------- when
                final var result = service.write(client);
                // ---------------------------------------------------------------------------- then
                final var buffer = HelloWorldTestUtils.verify_put_buffer12_invoked_once(service);
                Mockito.verify(client, Mockito.times(1)).write(buffer);
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
        void __blocking() throws IOException {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var address = new CompletableFuture<SocketAddress>();
            startServer(address);
            try (var client = Mockito.spy(DatagramChannel.open())) {
                client.connect(address.join());
                // ---------------------------------------------------------------------------- when
                final var result = service.write(client);
                // ---------------------------------------------------------------------------- then
                final var buffer = HelloWorldTestUtils.verify_put_buffer12_invoked_once(service);
                Mockito.verify(client, Mockito.times(1)).write(buffer);
                Assertions.assertSame(client, result);
            }
        }

        @Test
        void __nonblocking() throws IOException {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var address = new CompletableFuture<SocketAddress>();
            startServer(address);
            try (var client = Mockito.spy(DatagramChannel.open())) {
                client.configureBlocking(false);
                client.connect(address.join());
                // ---------------------------------------------------------------------------- when
                final var result = service.write(client);
                // ---------------------------------------------------------------------------- then
                final var buffer = HelloWorldTestUtils.verify_put_buffer12_invoked_once(service);
                Mockito.verify(client, Mockito.times(1)).write(buffer);
                Assertions.assertSame(client, result);
            }
        }
    }
}
