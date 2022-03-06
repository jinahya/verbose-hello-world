package com.github.jinahya.hello.miscellaneous.m2_rfc862;

import com.github.jinahya.hello.miscellaneous.m1_rfc862.Rfc862TcpClient1;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862TcpClient2 {

    public static void main(String... args) throws IOException, InterruptedException {
        var host = InetAddress.getLoopbackAddress();
        var endpoint = new InetSocketAddress(host, Rfc862TcpServer2.PORT);
        var executor = Executors.newCachedThreadPool();
        for (int i = 0; i < 4; i++) {
            executor.submit(() -> {
                Rfc862TcpClient1.connectWriteAndRead(endpoint);
                return null;
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

    private Rfc862TcpClient2() {
        throw new AssertionError("instantiation is not allowed");
    }
}
