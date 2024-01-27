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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.nio.charset.StandardCharsets;

@Slf4j
class _OperatorTest {

    @DisplayName("name()")
    @Nested
    class NameTest {

        @DisplayName("value.name().bytes(\"US_ASCII\").length == NAME_BYTES")
        @EnumSource(_Operator.class)
        @ParameterizedTest
        void _IsEqualToBytes_NameBytesLength(final _Operator value) {
            Assertions.assertEquals(
                    _Operator.NAME_BYTES,
                    value.name().getBytes(StandardCharsets.US_ASCII).length
            );
        }
    }

    @DisplayName("randomValue()")
    @Nested
    class RandomValueTest {

        @Test
        void __() {
            final var randomValue = _Operator.randomValue();
            log.debug("randomValue: {}", randomValue);
        }
    }
}
