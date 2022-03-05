package com.github.jinahya.hello.miscellaneous;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc862UdpClient {

    public static void main(String... args) throws IOException {
        var host = InetAddress.getLoopbackAddress();
        SocketAddress endpoint = new InetSocketAddress(host, Rfc862UdpServer.PORT);
        try (var client = new DatagramSocket(null)) {
            client.connect(endpoint);
            var length = ThreadLocalRandom.current().nextInt(Rfc862UdpServer.PACKET_LENGTH + 1);
            var buf = new byte[length];
            var packet = new DatagramPacket(buf, buf.length, endpoint);
            client.send(packet);
            log.debug("sent {} byte(s) to {}", packet.getLength(), endpoint);
            client.receive(packet);
            log.debug("received {} byte(s) from {}", packet.getLength(), packet.getSocketAddress());
            client.disconnect();
        }
    }
}
