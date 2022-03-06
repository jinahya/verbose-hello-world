package com.github.jinahya.hello.miscellaneous.m3_rfc862;

import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Rfc862TcpClient3 {

    public static void connectWriteAndRead(SocketAddress endpoint) throws IOException {
        try (var client = SocketChannel.open()) {
            client.connect(endpoint);
            log.debug("[C] connected to {}", client.getRemoteAddress());
            var buffer = ByteBuffer.allocate(1);
            var bytes = ThreadLocalRandom.current().nextInt(1, 9);
            for (int i = 0; i < bytes; i++) {
                var written = (byte) ThreadLocalRandom.current().nextInt(256);
                buffer.put(written);
                buffer.flip(); // limit -> position, position -> zero
                while (buffer.hasRemaining()) {
                    client.write(buffer);
                }
                buffer.clear(); // position -> zero, limit -> capacity
                while (buffer.hasRemaining()) {
                    if (client.read(buffer) == -1) {
                        throw new EOFException("unexpected eof");
                    }
                }
                buffer.flip(); // limit -> position, position -> zero
                var read = buffer.get();
                assert read == written;
                buffer.clear(); // position -> zero, limit -> capacity
            } // end-of-for
            log.debug("[C] {} bytes written/read to/from {}", bytes, client.getRemoteAddress());
        }
    }

    public static void main(String... args) throws IOException, InterruptedException,
                                                   ExecutionException {
        var host = InetAddress.getLoopbackAddress();
        var endpoint = new InetSocketAddress(host, Rfc862TcpServer3.PORT);
        var executor = Executors.newCachedThreadPool();
        for (int i = 0; i < 1; i++) {
            executor.submit(() -> {
                connectWriteAndRead(endpoint);
                return null;
            }).get();
        }
        executor.shutdown();
        var timeout = 8L;
        var unit = TimeUnit.SECONDS;
        if (!executor.awaitTermination(timeout, unit)) {
            log.error("executor not terminated in {} {}", timeout, unit);
        }
    }

    private Rfc862TcpClient3() {
        throw new AssertionError("instantiation is not allowed");
    }
}
