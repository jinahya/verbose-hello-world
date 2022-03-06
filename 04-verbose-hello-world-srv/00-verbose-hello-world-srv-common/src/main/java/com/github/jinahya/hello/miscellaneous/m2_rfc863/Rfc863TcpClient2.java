package com.github.jinahya.hello.miscellaneous.m2_rfc863;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc863TcpClient2 {

    public static void main(String... args) throws IOException {
        var host = InetAddress.getLoopbackAddress();
        var endpoint = new InetSocketAddress(host, Rfc863TcpServer2.PORT);
        try (var client = new Socket()) {
            client.connect(endpoint);
            log.debug("[C] connected to {}, through {}", client.getRemoteSocketAddress(),
                      client.getLocalSocketAddress());
            var bytes = ThreadLocalRandom.current().nextInt(1, 9);
            for (int i = 0; i < bytes; i++) {
                var written = ThreadLocalRandom.current().nextInt();
                client.getOutputStream().write(written);
            }
            client.getOutputStream().flush();
            log.debug("[C] {} byte(s) written", bytes);
        }
    }

    private Rfc863TcpClient2() {
        throw new AssertionError("instantiation is not allowed");
    }
}
