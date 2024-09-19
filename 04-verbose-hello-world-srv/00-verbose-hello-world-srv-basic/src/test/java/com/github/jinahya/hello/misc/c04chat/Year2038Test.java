package com.github.jinahya.hello.misc.c04chat;

/*-
 * #%L
 * verbose-hello-world-srv-basic
 * %%
 * Copyright (C) 2018 - 2024 Jinahya, Inc.
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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoField;

// https://stackoverflow.com/q/77937165/330457
@Slf4j
class Year2038Test {

    private static void log(final long epochSecond) {
        final var instant = Instant.ofEpochSecond(epochSecond);
        log.debug("epochSecond: {} ({})", String.format("0x%08X", epochSecond), epochSecond);
        log.debug("    instant: {}", instant);
    }

    // -------------------------------------------------------------------------------- signe 32-bit
    @DisplayName("Integer.MAX_VALUE")
    @Test
    void __0x7FFFFFFF() {
        final var epochSecond = Integer.MAX_VALUE;
        log(epochSecond);
    }

    @DisplayName("Integer.MIN_VALUE")
    @Test
    void __0xFFFFFFFF() {
        final var epochSecond = Integer.MIN_VALUE;
        log(epochSecond);
    }

    // ----------------------------------------------------------------------------- unsigned 32-bit
    @DisplayName("0xFFFFFFFFL")
    @Test
    void __0xFFFFFFFFL_() {
        final var epochSecond = 0xFFFFFFFFL;
        log(epochSecond);
    }

    @DisplayName("0x00000000L")
    @Test
    void __0x00000000L() {
        final var epochSecond = 0x00000000L;
        log(epochSecond);
    }

    // ----------------------------------------------------------------------------- (signed) 64-bit
    @Disabled("does not work!")
    @DisplayName("0x7FFFFFFF_FFFFFFFF")
    @Test
    void __0x7FFFFFFF_FFFFFFFF() {
        log(Long.MAX_VALUE);
    }

    // --------------------------------------------------------------------------------- Instant.MAX
    @DisplayName("Instant.MAX.getEpochSecond()")
    @Test
    void __31556889864403199L() {
        log(Instant.MAX.getEpochSecond());
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("0x67E3C3CB487849")
    @Test
    void __0x67E3C3CB487849() {
        final var maximumEpochDay = ChronoField.EPOCH_DAY.range().getMaximum();
        final var maximumHourOfDay = ChronoField.HOUR_OF_DAY.range().getMaximum();
        final var maximumMinuteOfHour = ChronoField.MINUTE_OF_HOUR.range().getMaximum();
        final var maximumSecondOfMinute = ChronoField.SECOND_OF_MINUTE.range().getMaximum();
        final var epochSecond = maximumEpochDay * maximumHourOfDay * maximumMinuteOfHour
                                * maximumSecondOfMinute;
        log(epochSecond);
    }
}
