package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;

@Slf4j
class Rfc863TcpServer {

    static final int PORT = 51009; // 9 + 51000

    public static void main(String... args) throws IOException {
        var host = InetAddress.getLoopbackAddress();
        SocketAddress endpoint = new InetSocketAddress(host, PORT);
        try (var server = new ServerSocket()) {
            server.bind(endpoint);
            log.info("server bound to {}", server.getLocalSocketAddress());
            while (!server.isClosed()) {
                try (var client = server.accept()) {
                    log.debug("[S] accepted from {}", client.getRemoteSocketAddress());
                    var bytes = 0L;
                    while (true) {
                        var b = client.getInputStream().read();
                        if (b == -1) {
                            break;
                        }
                        bytes++;
                    }
                    log.debug("number of bytes discarded: {}", bytes);
                } catch (IOException ioe) {
                    if (server.isClosed()) {
                        break;
                    }
                    log.error("failed to accept/read", ioe);
                }
            }
        }
    }
}
