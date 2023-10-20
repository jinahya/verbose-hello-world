package com.github.jinahya.hello.misc.c03calc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class _CalcMessageTest {

    @DisplayName("newInstanceForServers()")
    @Nested
    class InstanceForServersTest {

        @Test
        void __() {
            assertThat(_CalcMessage.newInstanceForServers())
                    .isNotNull()
                    .satisfies(i -> {
                        assertThatThrownBy(i::operator)
                                .isInstanceOf(IllegalArgumentException.class);
                        assertThat(i.operand1()).isZero();
                        assertThat(i.operand2()).isZero();
                        assertThat(i.result()).isZero();
                    });
        }
    }

    @DisplayName("newInstanceForClients()")
    @Nested
    class InstanceForClientsTest {

        @Test
        void __() {
            assertThat(_CalcMessage.newInstanceForClients())
                    .isNotNull()
                    .satisfies(i -> {
                        assertThat(i.operator()).isNotNull();
                        assertThat(i.result()).isZero();
                    });
        }
    }
}
