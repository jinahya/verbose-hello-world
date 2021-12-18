package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
class HelloWorldServerTest {

    @Disabled
    @Test
    void test() throws IOException, InterruptedException {
        final HelloWorldServer server = new HelloWorldServer(
                new HelloWorldImpl(),
                new InetSocketAddress(InetAddress.getLoopbackAddress(), 0),
                50
        );
        server.open();
        final SocketAddress endpoint = HelloWorldServer.ENDPOINT.get();
        final int count = 8;
        final CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            new Thread(() -> {
                try (Socket socket = new Socket()) {
                    socket.connect(endpoint);
                    log.debug("connected to {}", socket.getRemoteSocketAddress());
                    final byte[] b = new byte[HelloWorld.BYTES];
                    for (int off = 0; off < b.length; ) {
                        final int len = b.length - off;
                        final int r = socket.getInputStream().read(b, off, len);
                        off += r;
                    }
                    Assertions.assertArrayEquals("hello, world".getBytes(StandardCharsets.US_ASCII), b);
                } catch (final IOException ioe) {
                    log.error("failed to connect and read from  " + endpoint);
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        if (!latch.await(1L, TimeUnit.MINUTES)) {
            log.warn("latch is still not broken!");
        }
        server.close();
    }
}
