package com.github.jinahya.hello.miscellaneous.rfc862_m1;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

// https://datatracker.ietf.org/doc/html/rfc863
@Slf4j
public class Rfc862UdpServer1 {

    static final int PORT = Rfc862TcpServer1.PORT;

    static final int MAX_PACKET_LENGTH = 8;

    public static void send(DatagramPacket packet, DatagramSocket server) throws IOException {
        log.debug("[S] {} byte(s) received from {}", packet.getLength(),
                  packet.getSocketAddress());
        server.send(packet);
        log.debug("[S] sent back to {}", packet.getSocketAddress());
    }

    public static void main(String... args) throws IOException {
        var host = InetAddress.getLoopbackAddress();
        var endpoint = new InetSocketAddress(host, PORT);
        try (var server = new DatagramSocket(null)) {
            server.bind(endpoint);
            log.info("[S] server bound to {}", server.getLocalSocketAddress());
            while (!server.isClosed()) {
                var buffer = new byte[MAX_PACKET_LENGTH];
                var packet = new DatagramPacket(buffer, buffer.length);
                server.receive(packet);
                send(packet, server);
            }
        }
    }

    private Rfc862UdpServer1() {
        throw new AssertionError("instantiation is not allowed");
    }
}
