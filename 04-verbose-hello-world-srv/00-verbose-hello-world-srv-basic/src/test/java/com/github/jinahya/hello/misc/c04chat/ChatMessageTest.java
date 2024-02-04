package com.github.jinahya.hello.misc.c04chat;

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
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Objects;

@Slf4j
abstract class ChatMessageTest<T extends _ChatMessage<T>> {

    // ---------------------------------------------------------------------------------------------
    ChatMessageTest(final Class<T> messageClass) {
        super();
        this.messageClass = Objects.requireNonNull(messageClass, "messageClass is null");
    }

    // ----------------------------------------------------------------------------------- timestamp
    @DisplayName("timestamp")
    @Nested
    class TimestampTest {

        @DisplayName("timestamp()L")
        @Nested
        class GetTimestampTest {

            @Test
            void __() {
                // --------------------------------------------------------------------------- given
                final var instance = Mockito.spy(newMessageInstance());
                // ---------------------------------------------------------------------------- when
                final var result = instance.timestamp();
                // ---------------------------------------------------------------------------- then
                Assertions.assertEquals(0L, result);
                Mockito.verify(instance, Mockito.only()).timestamp();
            }
        }

        @DisplayName("timestamp(L)")
        @Nested
        class SetTimestampTest {

            @Test
            void __() {
                // --------------------------------------------------------------------------- given
                final var instance = Mockito.spy(newMessageInstance());
                final var timestamp = Instant.now().getEpochSecond();
                // ---------------------------------------------------------------------------- when
                final T result = instance.timestamp(timestamp);
                // ---------------------------------------------------------------------------- then
                Mockito.verify(instance, Mockito.only()).timestamp(timestamp);
                Assertions.assertSame(instance, result);
                Assertions.assertEquals(timestamp, instance.timestamp());
            }
        }
    }

    // ------------------------------------------------------------------------------------- message
    @DisplayName("message")
    @Nested
    class MessageTest {

        @DisplayName("message()message")
        @Nested
        class GetMessageTest {

            @Test
            void _NotNull_New() {
                // --------------------------------------------------------------------------- given
                final var instance = newMessageInstance();
                final var spy = Mockito.spy(instance);
                // ---------------------------------------------------------------------------- when
                final var message = spy.message();
                log.debug("message: {}", message);
                // ---------------------------------------------------------------------------- then
                Assertions.assertNotNull(message);
            }
        }

        @DisplayName("message(message)T")
        @Nested
        class SetMessageTest {

            @MethodSource({
                    "com.github.jinahya.hello.misc.c04chat.ChatMessageStaticTest#messageStream"
            })
            @ParameterizedTest
            void __(final String message) {
                assert message != null;
                assert !message.isBlank();
                // --------------------------------------------------------------------------- given
                final var instance = Mockito.spy(newMessageInstance());
                // ---------------------------------------------------------------------------- when
                final var result = instance.message(message);
                // ---------------------------------------------------------------------------- then
                Assertions.assertSame(instance, result);
                Assertions.assertNotNull(instance.message());
                Assertions.assertTrue(message.startsWith(instance.message()));
            }
        }
    }

    // -------------------------------------------------------------------------------- messageClass
    private T newMessageInstance() {
        try {
            final var constructor = messageClass.getDeclaredConstructor();
            if (!constructor.canAccess(null)) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance();
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException("failed to instantiate " + messageClass, roe);
        }
    }

    // ---------------------------------------------------------------------------------------------
    private final Class<T> messageClass;
}
