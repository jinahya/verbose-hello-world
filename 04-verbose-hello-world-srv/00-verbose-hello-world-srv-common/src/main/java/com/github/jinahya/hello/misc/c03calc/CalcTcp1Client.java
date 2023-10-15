package com.github.jinahya.hello.misc.c03calc;

import com.github.jinahya.hello.misc._TcpUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
class CalcTcp1Client {

    public static void main(final String... args) throws IOException, InterruptedException {
        final var executor = Executors.newFixedThreadPool(_CalcConstants.NUMBER_OF_REQUESTS);
        for (var c = 0; c < _CalcConstants.NUMBER_OF_REQUESTS; c++) {
            executor.submit(() -> {
                try (var client = new Socket()) {
                    // ------------------------------------------------------------------------ bind
                    if (ThreadLocalRandom.current().nextBoolean()) {
                        client.bind(new InetSocketAddress(_CalcConstants.HOST, 0));
                    }
                    // --------------------------------------------------------------------- connect
                    client.connect(_CalcConstants.ADDR, _CalcConstants.CONNECT_TIMEOUT_MILLIS);
                    // ---------------------------------------------------------------- send/receive
                    client.setSoTimeout(_CalcConstants.READ_TIMEOUT_MILLIS);
                    // ----------------------------------------------------------------------- write
                    final var array = _CalcUtils.newArrayForClient();
                    final var length = array.length - Integer.BYTES;
                    client.getOutputStream().write(array, 0, length);
                    client.getOutputStream().flush();
                    // ------------------------------------------------------------------------ read
                    final int r = client.getInputStream().readNBytes(array, length, Integer.BYTES);
                    if (r == -1) {
                        throw new EOFException("unexpected eof");
                    }
                    // ------------------------------------------------------------------------- log
                    _CalcUtils.log(array);
                }
                return null;
            });
        }
        executor.shutdown();
        final var terminated = executor.awaitTermination(10L, TimeUnit.SECONDS);
        assert terminated;
    }
}
