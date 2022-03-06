package com.github.jinahya.hello.miscellaneous.m1_rfc862;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class Rfc862UdpClient1 {

    private static final boolean BIND = false;

    private static final boolean CONNECT = false;

    public static void sendAndReceive(SocketAddress endpoint) throws IOException {
        try (var client = new DatagramSocket(null)) {
            if (BIND) {
                var host = ((InetSocketAddress) endpoint).getAddress();
                client.bind(new InetSocketAddress(host, 0));
                log.debug("[C] client bound to {}", client.getLocalSocketAddress());
            }
            if (CONNECT) {
                client.connect(endpoint);
                log.debug("[C] client connected");
                var host = ((InetSocketAddress) endpoint).getAddress();
                var other = new InetSocketAddress(host, 1234);
                try {
                    client.send(new DatagramPacket(new byte[0], 0, other));
                    assert false;
                } catch (IllegalArgumentException iae) {
                    log.debug("[C] unable to send to other than connected: {}",
                              iae.getMessage());
                }
            }
            var length = ThreadLocalRandom.current()
                    .nextInt(1, Rfc862UdpServer1.MAX_PACKET_LENGTH + 1);
            var sbuffer = new byte[length];
            {
                ThreadLocalRandom.current().nextBytes(sbuffer);
                var packet = new DatagramPacket(sbuffer, sbuffer.length, endpoint);
                client.send(packet);
                log.debug("[C] {} byte(s) sent to {}, through {}", packet.getLength(),
                          packet.getSocketAddress(), client.getLocalSocketAddress());
            }
            var rbuffer = new byte[sbuffer.length];
            {
                var packet = new DatagramPacket(rbuffer, rbuffer.length);
                client.receive(packet);
                log.debug("[C] {} byte(s) received from {}, through {}", packet.getLength(),
                          packet.getSocketAddress(), client.getLocalSocketAddress());
                assert packet.getLength() == rbuffer.length;
            }
            assert Arrays.equals(sbuffer, rbuffer);
            if (CONNECT) {
                client.disconnect();
                log.debug("[C] client disconnected");
            }
        }
    }

    public static void main(String... args) throws IOException {
        var host = InetAddress.getLoopbackAddress();
        var endpoint = new InetSocketAddress(host, Rfc862UdpServer1.PORT);
        sendAndReceive(endpoint);
    }

    private Rfc862UdpClient1() {
        throw new AssertionError("instantiation is not allowed");
    }
}
