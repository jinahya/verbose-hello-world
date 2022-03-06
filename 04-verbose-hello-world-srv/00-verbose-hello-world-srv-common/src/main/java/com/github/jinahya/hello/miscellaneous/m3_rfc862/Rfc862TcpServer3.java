package com.github.jinahya.hello.miscellaneous.m3_rfc862;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// https://datatracker.ietf.org/doc/html/rfc862
@Slf4j
class Rfc862TcpServer3 {

    static final int PORT = 53007; // 7 + 53000

    public static void readWriteAndClose(SocketChannel client) throws IOException {
        try (client) {
            long bytes = 0L;
            var buffer = ByteBuffer.allocate(2);
            while (true) {
                var read = client.read(buffer);
                if (read == -1) {
                    break;
                }
                bytes += read;
                buffer.flip(); // limit -> position, position -> zero
                client.write(buffer);
                buffer.compact(); // move remaining bytes to the front
            } // end-of-while
            for (buffer.flip(); buffer.hasRemaining(); ) {
                client.write(buffer);
            }
            log.debug("[S] {} bytes read/written to {}", bytes, client.getRemoteAddress());
        }
    }

    public static void main(String... args) throws IOException, InterruptedException {
        var host = InetAddress.getLoopbackAddress();
        var endpoint = new InetSocketAddress(host, PORT);
        try (var server = ServerSocketChannel.open()) {
            server.bind(endpoint);
            log.info("[S] server bound to {}", server.getLocalAddress());
            var executor = Executors.newCachedThreadPool();
            while (server.isOpen()) {
                var client = server.accept();
                executor.submit(() -> {
                    log.debug("[S] connected from {}, through {}", client.getRemoteAddress(),
                              client.getLocalAddress());
                    readWriteAndClose(client);
                    return null;
                });
            }
            executor.shutdown();
            var timeout = 4L;
            var unit = TimeUnit.SECONDS;
            if (!executor.awaitTermination(timeout, unit)) {
                log.error("executor not terminated in {} {}", timeout, unit);
            }
        }
    }

    private Rfc862TcpServer3() {
        throw new AssertionError("instantiation is not allowed");
    }
}
