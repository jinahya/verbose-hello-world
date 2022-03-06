package com.github.jinahya.hello.miscellaneous.m2_rfc863;

import com.github.jinahya.hello.miscellaneous.m1_rfc863.Rfc863TcpServer1;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// https://datatracker.ietf.org/doc/html/rfc863
@Slf4j
class Rfc863TcpServer2 {

    static final int PORT = 52009; // 9 + 52000

    public static void main(String... args) throws IOException, InterruptedException {
        var host = InetAddress.getLoopbackAddress();
        var endpoint = new InetSocketAddress(host, PORT);
        try (var server = new ServerSocket()) {
            server.bind(endpoint);
            log.info("[S] server bound to {}", server.getLocalSocketAddress());
            var executor = Executors.newCachedThreadPool();
            while (!server.isClosed()) {
                var client = server.accept();
                executor.submit(() -> {
                    Rfc863TcpServer1.readAndClose(client);
                    return null;
                });
            }
            executor.shutdown();
            {
                var timeout = 8L;
                var unit = TimeUnit.SECONDS;
                if (!executor.awaitTermination(timeout, unit)) {
                    log.error("executor not terminated in {} {}", timeout, unit);
                }
            }
        }
    }

    private Rfc863TcpServer2() {
        throw new AssertionError("instantiation is not allowed");
    }
}
