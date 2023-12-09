package com.github.jinahya.hello.util;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
    void __()
            throws Exception {
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
