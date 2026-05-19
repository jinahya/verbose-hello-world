package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;

import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Junit_Assumptions_Test {

    @Test
    void abort_ThrowsTestAbortedException_() {
        Assertions.assertThrowsExactly(
                TestAbortedException.class,
                Assumptions::abort
        );
    }

    @Test
    void abort__() {
        Assumptions.abort();
        log.error("you're not supposed to see me!");
    }

    @Test
    void abort__withMessage() {
        Assumptions.abort("aborting...");
        log.error("you're not supposed to see me!");
    }

    @Test
    void abort__withMessageSupplier() {
        Assumptions.abort(
                () -> String.format("aborting at %s", LocalTime.now())
        );
        log.error("you're not supposed to see me!");
    }

    @Test
    void assumeTrue__() {
        final var value = true;
        Assumptions.assumeTrue(value);
        Assertions.assertThrows(
                TestAbortedException.class,
                () -> Assumptions.assumeFalse(value)
        );
        Assumptions.assumeFalse(value);
    }

    @Test
    void assumeFalse__() {
        final var value = false;
        Assumptions.assumeFalse(value);
        Assertions.assertThrows(
                TestAbortedException.class,
                () -> Assumptions.assumeTrue(value)
        );
        Assumptions.assumeTrue(value);
    }

    @Test
    void assumeRandom__() {
        final var value = ThreadLocalRandom.current().nextBoolean();
        Assumptions.assumeTrue(value);
        log.debug("the value is true!");
    }

    @Test
    void assumeThat__() {
        Assumptions.assumingThat(
                ThreadLocalRandom.current().nextBoolean(),
                () -> log.debug("i got true!")
        );
    }

    @Test
    void assumeThat__withBooleanSupplier() {
        Assumptions.assumingThat(
                () -> ThreadLocalRandom.current().nextBoolean(),
                () -> log.debug("i got true!")
        );
    }
}
