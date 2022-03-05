package com.github.jinahya.hello.miscellaneous;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

// https://datatracker.ietf.org/doc/html/rfc862
@Slf4j
class Rfc862TcpServer {

    static final int PORT = 51007; // 7 + 51000

    public static void main(String... args) throws IOException {
        var host = InetAddress.getLoopbackAddress();
        var endpoint = new InetSocketAddress(host, PORT);
        try (var server = new ServerSocket()) {
            server.bind(endpoint);
            log.info("server bound to {}", server.getLocalSocketAddress());
            while (!server.isClosed()) {
                try (var client = server.accept()) {
                    log.debug("[S] accepted from {}", client.getRemoteSocketAddress());
                    var bytes = 0L;
                    for (; true; bytes++) {
                        var b = client.getInputStream().read();
                        if (b == -1) {
                            break;
                        }
                        client.getOutputStream().write(b);
                        client.getOutputStream().flush();
                    }
                    log.debug("number of bytes echoed: {}", bytes);
                } catch (IOException ioe) {
                    if (server.isClosed()) {
                        break;
                    }
                    log.error("failed to accept/echo", ioe);
                }
            }
        }
    }
}
