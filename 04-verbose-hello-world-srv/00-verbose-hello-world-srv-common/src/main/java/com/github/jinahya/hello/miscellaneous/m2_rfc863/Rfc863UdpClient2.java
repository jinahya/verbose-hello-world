package com.github.jinahya.hello.miscellaneous.m2_rfc863;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc863UdpClient2 {

    public static void main(String... args) throws IOException, InterruptedException {
        var host = InetAddress.getLoopbackAddress();
        var endpoint = new InetSocketAddress(host, Rfc863UdpServer2.PORT);
        var executor = Executors.newCachedThreadPool();
        for (int i = 0; i < 4; i++) {
            executor.submit(() -> {
                try (var client = new DatagramSocket(null)) {
                    var length = ThreadLocalRandom.current()
                            .nextInt(1, Rfc863UdpServer2.MAX_PACKET_LENGTH + 1);
                    var buffer = new byte[length];
                    var packet = new DatagramPacket(buffer, buffer.length, endpoint);
                    client.send(packet);
                    log.debug("[C] {} byte(s) sent to {}, through {}", packet.getLength(), endpoint,
                              client.getLocalSocketAddress());
                }
                return null;
            });
        }
        executor.shutdown();
        var timeout = 4L;
        var unit = TimeUnit.SECONDS;
        if (!executor.awaitTermination(timeout, unit)) {
            log.error("executor not terminated in {} {}", timeout, unit);
        }
    }

    private Rfc863UdpClient2() {
        throw new AssertionError("instantiation is not allowed");
    }
}
