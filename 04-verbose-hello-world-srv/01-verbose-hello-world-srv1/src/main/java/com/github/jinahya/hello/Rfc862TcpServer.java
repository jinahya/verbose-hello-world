package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;

@Slf4j
class Rfc862TcpServer {

    static final int PORT = 51007; // 7 + 51000

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
                        client.getOutputStream().write(b);
                        bytes++;
                    }
                    log.debug("number of bytes echoed: {}", bytes);
                } catch (IOException ioe) {
                    if (server.isClosed()) {
                        break;
                    }
                    log.error("failed to accept/read/write", ioe);
                }
            }
        }
    }
}
