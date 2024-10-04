package com.github.jinahya.hello.util;

/*-
 * #%L
 * verbose-hello-world-api
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
