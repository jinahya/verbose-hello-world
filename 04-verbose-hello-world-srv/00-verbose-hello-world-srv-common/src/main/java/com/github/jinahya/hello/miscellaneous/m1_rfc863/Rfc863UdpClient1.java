package com.github.jinahya.hello.miscellaneous.m1_rfc863;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc863UdpClient1 {

    private static final boolean BIND = false;

    private static final boolean CONNECT = false;

    public static void main(String... args) throws IOException {
        var host = InetAddress.getLoopbackAddress();
        var endpoint = new InetSocketAddress(host, Rfc863UdpServer1.PORT);
        try (var client = new DatagramSocket(null)) {
            if (BIND) {
                client.bind(new InetSocketAddress(host, 0));
                log.debug("[C] client bound to {}", client.getLocalSocketAddress());
            }
            if (CONNECT) {
                client.connect(endpoint);
                log.debug("[C] client connected");
                var wrong = new InetSocketAddress(host, 1234);
                try {
                    client.send(new DatagramPacket(new byte[0], 0, wrong));
                    assert false;
                } catch (IllegalArgumentException iae) {
                    log.debug("[C] unable to send to other than connected: {}",
                              iae.getMessage());
                }
            }
            var length = ThreadLocalRandom.current()
                    .nextInt(1, Rfc863UdpServer1.MAX_PACKET_LENGTH + 1);
            var buffer = new byte[length];
            var packet = new DatagramPacket(buffer, buffer.length, endpoint);
            client.send(packet);
            log.debug("[C] {} byte(s) sent to {}, through {}", packet.getLength(),
                      packet.getSocketAddress(), client.getLocalSocketAddress());
            if (CONNECT) {
                client.disconnect();
                log.debug("[C] client disconnected");
            }
        }
    }

    private Rfc863UdpClient1() {
        throw new AssertionError("instantiation is not allowed");
    }
}
