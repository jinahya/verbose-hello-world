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
class Rfc862UdpServer {

    static final int PORT = Rfc862TcpServer.PORT;

    static final int PACKET_LENGTH = 8;

    public static void main(String... args) throws IOException {
        var host = InetAddress.getLoopbackAddress();
        SocketAddress endpoint = new InetSocketAddress(host, PORT);
        try (var server = new DatagramSocket(null)) {
            server.bind(endpoint);
            log.info("server bound to {}", server.getLocalSocketAddress());
            var buf = new byte[PACKET_LENGTH];
            var packet = new DatagramPacket(buf, buf.length);
            while (!server.isClosed()) {
                try {
                    server.receive(packet);
                    log.debug("received {} byte(s) from {}", packet.getLength(),
                              packet.getSocketAddress());
                    server.send(packet);
                    log.debug("sent back to {}", packet.getSocketAddress());
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
