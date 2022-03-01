package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc862TcpClient {

    public static void main(String... args) throws IOException {
        var host = InetAddress.getLoopbackAddress();
        SocketAddress endpoint = new InetSocketAddress(host, Rfc862TcpServer.PORT);
        try (var client = new Socket()) {
            client.connect(endpoint);
            log.debug("[C] connected to " + client.getRemoteSocketAddress());
            var bytes = ThreadLocalRandom.current().nextInt(16);
            for (int i = 0; i < bytes; i++) {
                var w = ThreadLocalRandom.current().nextInt(256);
                client.getOutputStream().write(w);
                client.getOutputStream().flush();
                var r = client.getInputStream().read();
                if (r == -1) {
                    throw new EOFException("unexpected eof");
                }
            }
            log.debug("number of bytes send/received: {}", bytes);
        }
    }
}
