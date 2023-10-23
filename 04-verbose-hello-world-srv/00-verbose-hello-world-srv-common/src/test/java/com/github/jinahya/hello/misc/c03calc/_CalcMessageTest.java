package com.github.jinahya.hello.misc.c03calc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class _CalcMessageTest {

    @DisplayName("newInstanceForClient()")
    @Nested
    class InstanceForClientsTest {

        @Test
        void __() {
            assertThat(_CalcMessage.newInstanceForClient())
                    .isNotNull()
                    .satisfies(i -> {
                        i.debug(b -> a -> {
                            assertThat(b.position()).isZero();
                            assertThat(b.remaining()).isEqualTo(_CalcMessage.LENGTH_REQUEST);
                            return null;
                        });
                        assertThat(i.operator()).isNotNull();
                        assertThat(i.result()).isZero();
                    });
        }
    }

    @DisplayName("newInstanceForServer()")
    @Nested
    class InstanceForServersTest {

        @Test
        void __() {
            assertThat(_CalcMessage.newInstanceForServer())
                    .isNotNull()
                    .satisfies(i -> {
                        i.debug(b -> a -> {
                            assertThat(b.position()).isZero();
                            assertThat(b.remaining()).isEqualTo(_CalcMessage.LENGTH_REQUEST);
                            return null;
                        });
                        assertThatThrownBy(i::operator)
                                .isInstanceOf(IllegalArgumentException.class);
                        assertThat(i.operand1()).isZero();
                        assertThat(i.operand2()).isZero();
                        assertThat(i.result()).isZero();
                    });
        }
    }
}
