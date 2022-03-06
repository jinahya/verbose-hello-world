package com.github.jinahya.hello.miscellaneous.m3_rfc863;

import com.github.jinahya.hello.miscellaneous.m1_rfc863.Rfc863UdpClient1;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc863UdpClient3 {

    public static void main(String... args) throws IOException, InterruptedException {
        var host = InetAddress.getLoopbackAddress();
        var endpoint = new InetSocketAddress(host, Rfc863UdpServer3.PORT);
        var executor = Executors.newCachedThreadPool();
        for (int i = 0; i < 4; i++) {
            executor.submit(() -> {
                Rfc863UdpClient1.send(endpoint);
                return null;
            });
        }
        executor.shutdown();
        {
            var timeout = 4L;
            var unit = TimeUnit.SECONDS;
            if (!executor.awaitTermination(timeout, unit)) {
                log.error("executor not terminated in {} {}", timeout, unit);
            }
        }
    }

    private Rfc863UdpClient3() {
        throw new AssertionError("instantiation is not allowed");
    }
}
