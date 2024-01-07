package com.github.jinahya.hello._05_util_concurrent;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
class AtomicLongTest {

    @Test
    void __() {
        final var atomic = new AtomicLong();
        {
            final var got = atomic.addAndGet(Long.MAX_VALUE);
            log.debug("got: {}", got);
        }
        {
            final var got = atomic.addAndGet(Long.MAX_VALUE);
            log.debug("got: {}", got);
        }
    }
}
