package com.github.jinahya.hello.miscellaneous.m3_rfc863;

import com.github.jinahya.hello.miscellaneous.m1_rfc863.Rfc863UdpServer1;
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
class Rfc863UdpServer3 {

    static final int PORT = Rfc863TcpServer3.PORT;

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
                    Rfc863UdpServer1.log(packet);
                });
            }
            executor.shutdown();
            {
                var timeout = 8L;
                var unit = TimeUnit.SECONDS;
                if (!executor.awaitTermination(timeout, unit)) {
                    log.error("executor not terminated in {} {}", timeout, unit);
                }
            }
        }
    }

    private Rfc863UdpServer3() {
        throw new AssertionError("instantiation is not allowed");
    }
}
