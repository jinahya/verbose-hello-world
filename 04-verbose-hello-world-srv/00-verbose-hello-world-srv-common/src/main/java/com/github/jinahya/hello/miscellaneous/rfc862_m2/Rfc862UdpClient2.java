package com.github.jinahya.hello.miscellaneous.rfc862_m2;

import com.github.jinahya.hello.miscellaneous.rfc862_m1.Rfc862UdpClient1;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862UdpClient2 {

    public static void main(String... args) throws IOException, InterruptedException {
        var host = InetAddress.getLoopbackAddress();
        var endpoint = new InetSocketAddress(host, Rfc862UdpServer2.PORT);
        var executor = Executors.newCachedThreadPool();
        for (int i = 0; i < 4; i++) {
            executor.submit(() -> {
                Rfc862UdpClient1.sendAndReceive(endpoint);
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

    private Rfc862UdpClient2() {
        throw new AssertionError("instantiation is not allowed");
    }
}
