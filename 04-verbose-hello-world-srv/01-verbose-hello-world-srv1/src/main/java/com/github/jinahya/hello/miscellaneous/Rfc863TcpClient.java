package com.github.jinahya.hello.miscellaneous;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc863TcpClient {

    public static void main(String... args) throws IOException {
        var host = InetAddress.getLoopbackAddress();
        SocketAddress endpoint = new InetSocketAddress(host, Rfc863TcpServer.PORT);
        try (var client = new Socket()) {
            client.connect(endpoint);
            log.debug("[C] connected to " + client.getRemoteSocketAddress());
            var bytes = ThreadLocalRandom.current().nextInt(128);
            for (int i = 0; i < bytes; i++) {
                client.getOutputStream().write(ThreadLocalRandom.current().nextInt(256));
            }
            client.getOutputStream().flush();
            log.debug("number of bytes sent: {}", bytes);
        }
    }
}
