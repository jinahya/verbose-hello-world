package com.github.jinahya.hello.miscellaneous.m1_rfc862;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

// https://datatracker.ietf.org/doc/html/rfc862
@Slf4j
public class Rfc862TcpServer1 {

    static final int PORT = 51007; // 7 + 51000

    public static void readAndWrite(Socket client) throws IOException {
        log.debug("[S] accepted from {}, through {}", client.getRemoteSocketAddress(),
                  client.getLocalSocketAddress());
        var bytes = 0L;
        for (; true; bytes++) {
            var b = client.getInputStream().read();
            if (b == -1) {
                break;
            }
            client.getOutputStream().write(b);
            client.getOutputStream().flush();
        }
        log.debug("[S] {} byte(s) read/written", bytes);
    }

    public static void main(String... args) throws IOException {
        var host = InetAddress.getLoopbackAddress();
        var endpoint = new InetSocketAddress(host, PORT);
        try (var server = new ServerSocket()) {
            server.bind(endpoint);
            log.info("[S] server bound to {}", server.getLocalSocketAddress());
            while (!server.isClosed()) {
                try (var client = server.accept()) {
                    readAndWrite(client);
                }
            }
        }
    }

    private Rfc862TcpServer1() {
        throw new AssertionError("instantiation is not allowed");
    }
}
