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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class _CalcMessageTest {

    @DisplayName("newInstanceForClient()")
    @Nested
    class InstanceForClientsTest {

        @Test
        void __() {
            final var instance = _CalcMessage.newInstanceForClient();
            Assertions.assertNotNull(instance);
            instance.debug(b -> a -> {
                Assertions.assertEquals(0, b.position());
                Assertions.assertEquals(_CalcMessage.LENGTH_REQUEST, b.remaining());
                return null;
            });
            Assertions.assertNotNull(instance.operator());
            Assertions.assertNotNull(instance.result());
        }
    }

    @DisplayName("newInstanceForServer()")
    @Nested
    class InstanceForServersTest {

        @Test
        void __() {
            final var instance = _CalcMessage.newInstanceForServer();
            Assertions.assertNotNull(instance);
            instance.debug(b -> a -> {
                Assertions.assertEquals(0, b.position());
                Assertions.assertEquals(_CalcMessage.LENGTH_REQUEST, b.remaining());
                return null;
            });
            Assertions.assertThrows(IllegalArgumentException.class, () -> instance.operator());
            Assertions.assertEquals(0, instance.operand1());
            Assertions.assertEquals(0, instance.operand2());
            Assertions.assertEquals(0, instance.result());
        }
    }
}
