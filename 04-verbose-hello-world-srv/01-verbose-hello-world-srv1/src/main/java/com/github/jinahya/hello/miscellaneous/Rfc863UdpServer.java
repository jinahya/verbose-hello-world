package com.github.jinahya.hello.miscellaneous;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

// https://datatracker.ietf.org/doc/html/rfc863
@Slf4j
class Rfc863UdpServer {

    static final int PORT = Rfc863TcpServer.PORT;

    public static void main(String... args) throws IOException {
        var host = InetAddress.getLoopbackAddress();
        SocketAddress endpoint = new InetSocketAddress(host, PORT);
        try (var server = new DatagramSocket(null)) {
            server.bind(endpoint);
            log.info("server bound to {}", server.getLocalSocketAddress());
            var length = 1; // use 1 when you receive unexpected packets!
            var buf = new byte[length];
            var packet = new DatagramPacket(buf, buf.length);
            while (!server.isClosed()) {
                try {
                    server.receive(packet);
                    log.debug("received from {}", packet.getSocketAddress());
                } catch (IOException ioe) {
                    if (server.isClosed()) {
                        break;
                    }
                    log.error("failed to accept/discard", ioe);
                }
            }
        }
    }
}
