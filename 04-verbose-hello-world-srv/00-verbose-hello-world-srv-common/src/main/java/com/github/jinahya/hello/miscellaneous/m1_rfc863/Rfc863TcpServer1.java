package com.github.jinahya.hello.miscellaneous.m1_rfc863;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.ThreadLocalRandom;

// https://datatracker.ietf.org/doc/html/rfc863
@Slf4j
class Rfc863TcpServer1 {

    static final int PORT = 51009; // 9 + 51000

    public static void main(String... args) throws IOException {
        var host = InetAddress.getLoopbackAddress();
        var endpoint = new InetSocketAddress(host, PORT);
        try (var server = new ServerSocket()) {
            server.bind(endpoint);
            log.info("[S] server bound to {}", server.getLocalSocketAddress());
            while (!server.isClosed()) {
                try (var client = server.accept()) {
                    log.debug("[S] accepted from {}", client.getRemoteSocketAddress());
                    var bytes = 0L;
                    for (; true; bytes++) {
                        var read = client.getInputStream().read();
                        if (read == -1) {
                            break;
                        }
                    }
                    if (ThreadLocalRandom.current().nextBoolean()) {
                        client.shutdownInput();
                    }
                    log.debug("[S] byte(s) read: {}", bytes);
                }
            }
        }
    }

    private Rfc863TcpServer1() {
        throw new AssertionError("instantiation is not allowed");
    }
}
