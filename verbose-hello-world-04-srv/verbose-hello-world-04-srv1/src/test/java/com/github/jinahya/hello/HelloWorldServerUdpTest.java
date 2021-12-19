package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.EOFException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ServiceLoader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
class HelloWorldServerUdpTest {

    static void connect(final SocketAddress endpoint, final int count) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            new Thread(() -> {
                try (DatagramSocket socket = new DatagramSocket()) {
                    DatagramPacket packet = new DatagramPacket(new byte[0], 0, endpoint);
                    socket.send(packet);
                    log.debug("sent to {}", endpoint);
                    final byte[] array = new byte[HelloWorld.BYTES];
                    packet = new DatagramPacket(array, array.length);
                    socket.setSoTimeout(1000); // 1 sec
                    socket.receive(packet);
                    log.debug("received from {}", packet.getSocketAddress());
                    Assertions.assertArrayEquals("hello, world".getBytes(StandardCharsets.US_ASCII), array);
                } catch (final IOException ioe) {
                    log.error("failed to send/receive", ioe);
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        if (!latch.await(1L, TimeUnit.MINUTES)) {
            log.warn("latch is still not broken!");
        }
    }

    @Test
    void test() throws IOException, InterruptedException {
        final HelloWorld service = ServiceLoader.load(HelloWorld.class).iterator().next();
        final SocketAddress endpoint = new InetSocketAddress(InetAddress.getLoopbackAddress(), 0);
        final IHelloWorldServer server = new HelloWorldServerUdp(service, endpoint);
        server.open();
        connect(HelloWorldServerUdp.ENDPOINT.get(), 8);
        server.close();
    }
}
