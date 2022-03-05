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
class Rfc863UdpClient {

    public static void main(String... args) throws IOException {
        var host = InetAddress.getLoopbackAddress();
        SocketAddress endpoint = new InetSocketAddress(host, Rfc863UdpServer.PORT);
        try (var client = new DatagramSocket(null)) {
            client.connect(endpoint);
            var bytes = new byte[1];
            ThreadLocalRandom.current().nextBytes(bytes);
            var packet = new DatagramPacket(bytes, bytes.length, endpoint);
            client.send(packet);
            log.debug("sent {} byte(s) to {}", packet.getLength(), endpoint);
            client.disconnect();
        }
    }
}
