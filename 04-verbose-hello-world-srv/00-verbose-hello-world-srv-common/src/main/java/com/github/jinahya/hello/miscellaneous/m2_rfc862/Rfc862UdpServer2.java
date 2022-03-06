package com.github.jinahya.hello.miscellaneous.m2_rfc862;

import com.github.jinahya.hello.miscellaneous.m1_rfc862.Rfc862UdpServer1;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// https://datatracker.ietf.org/doc/html/rfc863
@Slf4j
class Rfc862UdpServer2 {

    static final int PORT = Rfc862TcpServer2.PORT;

    static final int MAX_PACKET_LENGTH = 8;

    public static void main(String... args) throws IOException, InterruptedException {
        var host = InetAddress.getLoopbackAddress();
        var endpoint = new InetSocketAddress(host, PORT);
        try (var server = new DatagramSocket(null)) {
            server.bind(endpoint);
            log.info("[S] server bound to {}", server.getLocalSocketAddress());
            var executor = Executors.newCachedThreadPool();
            while (!server.isClosed()) {
                var buffer = new byte[MAX_PACKET_LENGTH];
                var packet = new DatagramPacket(buffer, buffer.length);
                server.receive(packet);
                executor.submit(() -> {
                    Rfc862UdpServer1.send(packet, server);
                    return null;
                });
            } // end-of-while
            executor.shutdown();
            var timeout = 4L;
            var unit = TimeUnit.SECONDS;
            if (!executor.awaitTermination(timeout, unit)) {
                log.error("executor not terminated in {} {}", timeout, unit);
            }
        }
    }

    private Rfc862UdpServer2() {
        throw new AssertionError("instantiation is not allowed");
    }
}
