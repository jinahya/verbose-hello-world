package com.github.jinahya.hello.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.slf4j.event.Level;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class LogbackUtilsTest {

    @DisplayName("toLogbackLevel(level)")
    @Nested
    class ToLogbackLevelTest {

        @EnumSource(Level.class)
        @ParameterizedTest
        void __(final Level slf4jLevel) throws ReflectiveOperationException {
            final var logbackLevel = LogbackUtils.toLogbackLevel(slf4jLevel);
            assertThat(logbackLevel)
                    .isNotNull()
                    .isInstanceOf(LogbackUtils.LEVEL_CLASS);
        }
    }

    @DisplayName("setLevel(logger,level))")
    @Nested
    class SetLevelTest {

        @EnumSource(Level.class)
        @ParameterizedTest
        void __(final Level slf4jLevel) throws ReflectiveOperationException {
            LogbackUtils.setLevel(log, slf4jLevel);
            assertThat(LogbackUtils.getLevel_(log))
                    .isInstanceOf(LogbackUtils.LEVEL_CLASS);
        }
    }
}
