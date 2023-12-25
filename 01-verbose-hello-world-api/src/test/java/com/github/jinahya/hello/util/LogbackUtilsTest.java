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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.slf4j.event.Level;

@Slf4j
class LogbackUtilsTest {

    @DisplayName("toLogbackLevel(level)")
    @Nested
    class ToLogbackLevelTest {

        @EnumSource(Level.class)
        @ParameterizedTest
        void __(final Level slf4jLevel) throws ReflectiveOperationException {
            final var logbackLevel = LogbackUtils.toLogbackLevel(slf4jLevel);
            Assertions.assertNotNull(logbackLevel);
            Assertions.assertInstanceOf(LogbackUtils.LEVEL_CLASS, logbackLevel);
        }
    }

    @DisplayName("setLevel(logger,level))")
    @Nested
    class SetLevelTest {

        @EnumSource(Level.class)
        @ParameterizedTest
        void __(final Level slf4jLevel) throws ReflectiveOperationException {
            LogbackUtils.setLevel(log, slf4jLevel);
            Assertions.assertInstanceOf(LogbackUtils.LEVEL_CLASS, LogbackUtils.getLevel_(log));
        }
    }
}
