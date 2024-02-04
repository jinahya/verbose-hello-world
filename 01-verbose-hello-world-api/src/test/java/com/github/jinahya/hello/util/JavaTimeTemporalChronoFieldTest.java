package com.github.jinahya.hello.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.temporal.ChronoField;

@Slf4j
class JavaTimeTemporalChronoFieldTest {

    @Nested
    class RangeTest {

        @EnumSource(ChronoField.class)
        @ParameterizedTest
        void __(final ChronoField field) {
            final var range = field.range();
            log.debug("largestMinimum: {}", range.getLargestMinimum());
            log.debug("maximum: {}", range.getMaximum());
            log.debug("minimum: {}", range.getMinimum());
            log.debug("smallestMaximum: {}", range.getSmallestMaximum());
            log.debug("fixed: {}", range.isFixed());
            log.debug("intValue: {}", range.isIntValue());
        }
    }
}
