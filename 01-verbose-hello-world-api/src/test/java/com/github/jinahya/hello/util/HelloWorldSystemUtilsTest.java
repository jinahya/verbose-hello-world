package com.github.jinahya.hello.util;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HelloWorldSystemUtilsTest {

    @Nested
    class StopWatch1Test {

        @Test
        void __() {
            HelloWorldSystemUtils.startStopWatch1();
            var sleep = Duration.ofMillis(ThreadLocalRandom.current().nextLong(1024));
            Awaitility.await().pollDelay(sleep).until(() -> true);
            var elapsed = HelloWorldSystemUtils.stopStopWatch1();
            assertTrue(elapsed.compareTo(sleep) >= 0);
        }
    }
}
