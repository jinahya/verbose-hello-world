package com.github.jinahya.hello.miscellaneous.m2_rfc863;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

// https://datatracker.ietf.org/doc/html/rfc863
@Slf4j
class Rfc863TcpServer2 {

    static final int PORT = 52009; // 9 + 52000

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
                    log.debug("[S] byte(s) read: {}", bytes);
                } catch (IOException ioe) {
                    if (server.isClosed()) {
                        break;
                    }
                    log.error("failed to accept/discard", ioe);
                }
            }
        }
    }

    private Rfc863TcpServer2() {
        throw new AssertionError("instantiation is not allowed");
    }
}
