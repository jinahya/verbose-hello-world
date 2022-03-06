package com.github.jinahya.hello.miscellaneous.rfc862_m1;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class Rfc862TcpClient1 {

    private static final boolean BIND = false;

    public static void connectWriteAndRead(SocketAddress endpoint) throws IOException {
        try (var client = new Socket()) {
            if (BIND) {
                client.bind(new InetSocketAddress(InetAddress.getLocalHost(), 0));
                log.debug("[C] client bound to {}", client.getLocalSocketAddress());
            }
            client.connect(endpoint);
            log.debug("[C] connected to {}, through {}", client.getRemoteSocketAddress(),
                      client.getLocalSocketAddress());
            var bytes = ThreadLocalRandom.current().nextInt(1, 9);
            for (int j = 0; j < bytes; j++) {
                var written = ThreadLocalRandom.current().nextInt(256);
                client.getOutputStream().write(written);
                client.getOutputStream().flush();
                var read = client.getInputStream().read();
                assert read != -1;
                assert read == written;
            }
            log.debug("[C] {} byte(s) written/read", bytes);
        }
    }

    public static void main(String... args) throws IOException {
        var host = InetAddress.getLoopbackAddress();
        var endpoint = new InetSocketAddress(host, Rfc862TcpServer1.PORT);
        connectWriteAndRead(endpoint);
    }

    private Rfc862TcpClient1() {
        throw new AssertionError("instantiation is not allowed");
    }
}
