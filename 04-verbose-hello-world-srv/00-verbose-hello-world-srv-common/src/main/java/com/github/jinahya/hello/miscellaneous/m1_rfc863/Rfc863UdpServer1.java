package com.github.jinahya.hello.miscellaneous.m1_rfc863;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

// https://datatracker.ietf.org/doc/html/rfc863
@Slf4j
public class Rfc863UdpServer1 {

    static final int PORT = Rfc863TcpServer1.PORT;

    static final int MAX_PACKET_LENGTH = 8;

    public static void log(DatagramPacket packet) {
        log.debug("[S] {} byte(s) received from {}", packet.getLength(),
                  packet.getSocketAddress());
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
                log(packet);
            }
        }
    }

    private Rfc863UdpServer1() {
        throw new AssertionError("instantiation is not allowed");
    }
}
