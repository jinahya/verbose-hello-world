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

import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.opentest4j.*;

import java.time.*;
import java.util.concurrent.*;

@DisplayName("JUnit assumptions")
@Slf4j
class Junit_Assumptions_Test {

    @DisplayName("should throw a <TestAbortedException> when <abort> is invoked")
    @Test
    void abort_ThrowsTestAbortedException_() {
        Assertions.assertThrowsExactly(
                TestAbortedException.class,
                Assumptions::abort
        );
    }

    @DisplayName("should abort the test when <abort()> is invoked")
    @Test
    void abort__() {
        Assumptions.abort();
        log.error("you're not supposed to see me!");
    }

    @DisplayName("should abort the test when <abort(message)> is invoked")
    @Test
    void abort__withMessage() {
        Assumptions.abort("aborting...");
        log.error("you're not supposed to see me!");
    }

    @DisplayName("should abort the test when <abort(messageSupplier)> is invoked")
    @Test
    void abort__withMessageSupplier() {
        Assumptions.abort(
                () -> String.format("aborting at %s", LocalTime.now())
        );
        log.error("you're not supposed to see me!");
    }

    @DisplayName(
            "should continue past <assumeTrue> and abort on <assumeFalse> when value is <true>")
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

    @DisplayName(
            "should continue past <assumeFalse> and abort on <assumeTrue> when value is <false>")
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

    @DisplayName("should continue or abort depending on a random <boolean> passed to <assumeTrue>")
    @Test
    void assumeRandom__() {
        final var value = ThreadLocalRandom.current().nextBoolean();
        Assumptions.assumeTrue(value);
        log.debug("the value is true!");
    }

    @DisplayName("should run the block only when <assumingThat> receives <true> as a <boolean>")
    @Test
    void assumeThat__() {
        Assumptions.assumingThat(
                ThreadLocalRandom.current().nextBoolean(),
                () -> log.debug("i got true!")
        );
    }

    @DisplayName("""
            should run the block only
            when <assumingThat> receives <true> from a <BooleanSupplier>""")
    @Test
    void assumeThat__withBooleanSupplier() {
        Assumptions.assumingThat(
                () -> ThreadLocalRandom.current().nextBoolean(),
                () -> log.debug("i got true!")
        );
    }
}
