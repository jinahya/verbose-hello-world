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
