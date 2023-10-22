package com.github.jinahya.hello.misc.c03calc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class _CalcOperatorTest {

    @DisplayName("name()")
    @Nested
    class NameTest {

        @DisplayName("value.name().length() == NAME_LENGTH")
        @EnumSource(_CalcOperator.class)
        @ParameterizedTest
        void _IsEqualToNameLength_NameLength(final _CalcOperator value) {
            assertThat(value.name())
                    .hasSize(_CalcOperator.LENGTH);
        }

        @DisplayName("value.name().bytes(\"US_ASCII\").length == NAME_BYTES")
        @EnumSource(_CalcOperator.class)
        @ParameterizedTest
        void _IsEqualToNameBytes_NameBytesLength(final _CalcOperator value) {
            assertThat(value.name().getBytes(_CalcOperator.CHARSET))
                    .hasSize(_CalcOperator.BYTES);
        }
    }
}
