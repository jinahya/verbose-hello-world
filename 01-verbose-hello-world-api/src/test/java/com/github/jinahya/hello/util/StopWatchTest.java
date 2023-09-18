package com.github.jinahya.hello.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Stopwatch1")
@Nested
class StopWatchTest {

    @Test
    void __() throws Exception {
        var count = 128;
        var futures = new ArrayList<Future<?>>(count);
        final var executor = Executors.newFixedThreadPool(count);
        for (int i = 0; i < count; i++) {
            futures.add(executor.submit(() -> {
                final var carrier = Stopwatch.startStopwatch();
                var sleep = Duration.ofMillis(current().nextLong(1024));
                await().pollDelay(sleep).until(() -> true);
                var elapsed = Stopwatch.stopStopwatch(carrier);
                assertTrue(elapsed.compareTo(sleep) >= 0);
            }));
        }
        executor.shutdown();
        var terminated = executor.awaitTermination(16L, TimeUnit.SECONDS);
        assert terminated;
        for (var future : futures) {
            future.get();
        }
    }
}
