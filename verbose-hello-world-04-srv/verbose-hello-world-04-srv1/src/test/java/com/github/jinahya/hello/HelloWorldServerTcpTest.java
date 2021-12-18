package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ServiceLoader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
class HelloWorldServerTcpTest {

    static void connect(final int count) throws InterruptedException {
        final SocketAddress endpoint = HelloWorldServerTcp.ENDPOINT.get();
        if (endpoint == null) {
            throw new IllegalStateException("no endpoint; is the server running?");
        }
        final CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            new Thread(() -> {
                try (Socket socket = new Socket()) {
                    socket.connect(endpoint);
                    log.debug("connected to {}", socket.getRemoteSocketAddress());
                    final byte[] array = new byte[HelloWorld.BYTES];
                    for (int offset = 0; offset < array.length; ) {
                        final int length = array.length - offset;
                        final int bytes = socket.getInputStream().read(array, offset, length);
                        if (bytes == -1) {
                            throw new EOFException("unexpected end-of-file");
                        }
                        offset += bytes;
                    }
                    Assertions.assertArrayEquals("hello, world".getBytes(StandardCharsets.US_ASCII), array);
                } catch (final IOException ioe) {
                    log.error("failed to connect and/or read", ioe);
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        if (!latch.await(1L, TimeUnit.MINUTES)) {
            log.warn("latch is still not broken!");
        }
    }

    @Disabled
    @Test
    void test() throws IOException, InterruptedException {
        final HelloWorld service = ServiceLoader.load(HelloWorld.class).iterator().next();
        final SocketAddress endpoint = new InetSocketAddress(InetAddress.getLoopbackAddress(), 0);
        final int backlog = 50;
        final IHelloWorldServer server = new HelloWorldServerTcp(service, endpoint, backlog);
        server.open();
        connect(8);
        server.close();
    }
}
