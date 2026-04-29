package com.github.jinahya.hello.api._java_nio_channels;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
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
 * A class for testing {@link HelloWorld#write(DatagramChannel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@畵蛇添足
@Slf4j
class HelloWorld_Write_DatagramChannel_畵蛇添足_Test
        extends HelloWorldTest {

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void beforeEach() throws IOException {
        HelloWorldTestUtils.write_datagramchannel_writes_hello_world_buffer(service());
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    class DatagramSocket_Server_Test {

        private CompletableFuture<SocketAddress> startServer() {
            final var address = new CompletableFuture<SocketAddress>();
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
            return address;
        }

        @Test
        void __blocking() throws IOException {
            try (var client = Mockito.spy(DatagramChannel.open())) {
                assert client.isBlocking();
                client.connect(startServer().join());
                service().write(client);
            }
        }

        @Test
        void __nonblocking() throws IOException {
            try (var client = Mockito.spy(DatagramChannel.open())) {
                client.configureBlocking(false);
                client.connect(startServer().join());
                service().write(client);
            }
        }
    }

    @Nested
    class DatagramChannel_Server_Blocking_Test {

        private CompletableFuture<SocketAddress> startServer() {
            final var address = new CompletableFuture<SocketAddress>();
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
            return address;
        }

        @Test
        void __blocking() throws IOException {
            try (var client = Mockito.spy(DatagramChannel.open())) {
                client.connect(startServer().join());
                service().write(client);
            }
        }

        @Test
        void __nonblocking() throws IOException {
            try (var client = Mockito.spy(DatagramChannel.open())) {
                client.configureBlocking(false);
                client.connect(startServer().join());
                service().write(client);
            }
        }
    }

    @Nested
    class DatagramChannel_Server_Nonblocking_Test {

        private CompletableFuture<SocketAddress> startServer() {
            final var address = new CompletableFuture<SocketAddress>();
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
            return address;
        }

        @Test
        void __blocking() throws IOException {
            try (var client = Mockito.spy(DatagramChannel.open())) {
                client.connect(startServer().join());
                service().write(client);
            }
        }

        @Test
        void __nonblocking() throws IOException {
            try (var client = Mockito.spy(DatagramChannel.open())) {
                client.configureBlocking(false);
                client.connect(startServer().join());
                service().write(client);
            }
        }
    }
}
