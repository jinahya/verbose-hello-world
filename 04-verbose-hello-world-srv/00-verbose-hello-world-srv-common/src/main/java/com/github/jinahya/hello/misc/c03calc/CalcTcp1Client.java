package com.github.jinahya.hello.misc.c03calc;

import lombok.extern.slf4j.Slf4j;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

@Slf4j
class CalcTcp1Client {

    private static List<Future<Void>> sub(final ExecutorService executor) {
        final var futures = new ArrayList<Future<Void>>(_CalcConstants.TOTAL_REQUESTS);
        for (var c = 0; c < _CalcConstants.TOTAL_REQUESTS; c++) {
            final var future = executor.<Void>submit(() -> {
                try (var client = new Socket()) {
                    client.connect(_CalcConstants.ADDR, _CalcConstants.CONNECT_TIMEOUT_MILLIS);
                    client.setSoTimeout((int) _CalcConstants.READ_TIMEOUT_MILLIS);
                    _CalcMessage.newInstanceForClient()
                            .sendRequest(client.getOutputStream())
                            .receiveResult(client.getInputStream())
                            .log();
                }
                return null;
            });
            futures.add(future);
        }
        return futures;
    }

    public static void main(final String... args) {
        final var executor = _CalcUtils.newExecutorForClient();
        sub(executor).forEach(f -> {
            try {
                f.get(_CalcConstants.CLIENT_PROGRAM_TIMEOUT,
                      _CalcConstants.CLIENT_PROGRAM_TIMEOUT_UNIT);
            } catch (final InterruptedException ie) {
                log.error("interrupted while getting the result", ie);
                Thread.currentThread().interrupt();
            } catch (final ExecutionException ee) {
                log.error("failed to execute", ee);
            } catch (final TimeoutException te) {
                log.error("times up while  getting the result", te);
            }
        });
        executor.shutdown();
        try {
            final var terminated = executor.awaitTermination(
                    _CalcConstants.CLIENT_PROGRAM_TIMEOUT,
                    _CalcConstants.CLIENT_PROGRAM_TIMEOUT_UNIT
            );
            assert terminated : "executor hasn't been terminated";
        } catch (final InterruptedException ie) {
            log.error("interrupted while awaiting executor to be terminated", ie);
            Thread.currentThread().interrupt();
        }
    }
}
