package com.github.jinahya.hello.misc.c03calc;

/*-
 * #%L
 * verbose-hello-world-srv-common
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
