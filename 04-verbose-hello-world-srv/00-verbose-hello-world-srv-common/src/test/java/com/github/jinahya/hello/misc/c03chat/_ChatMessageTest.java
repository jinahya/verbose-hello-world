package com.github.jinahya.hello.misc.c03chat;

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

import com.github.jinahya.hello.misc.c03chat._ChatMessage.OfArray;
import com.github.jinahya.hello.misc.c03chat._ChatMessage.OfBuffer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.withSettings;

@Slf4j
class _ChatMessageTest {

    // https://tatoeba.org/ko/
    private static Stream<String> getMessageStream() {
        return Stream.of(
                "أمك مريضة",
                "У нього було сиве волосся.",
                "Trovu, kiu vizitas vian profilon.",
                "There is no public toilet.",
                "Ur zmireɣ ad d-sɣeɣ tasewlaft yelhan am tin-nnem.",
                "La réunion s'est tenue hier.",
                "私はあなたのご両親にかなり気に入られているようです。",
                "汝英語會話係啵？",
                "어떻게 그의 아버지가 저 남자의 정체를 간파한 것인지 알 수 없어요.",
                """
                        その問いかけに答えるチャンスを今、私たちは手にしました。
                        今この時こそが、私たちの瞬間です。
                        今この時にこそ、私たちは人々がまた仕事につけるようにしなくてはなりません。
                        子供たちのために、チャンスの扉を開かなくてはなりません。
                        繁栄を取り戻し、平和を推進しなくてはなりません。
                        今この時にこそ、アメリカの夢を取り戻し、基本的な真理を再確認しなくてはなりません。
                        大勢の中にあって、私たちはひとつなのだと。
                        息をし続ける限り、私たちは希望をもち続けるのだと。
                        そして疑り深く悲観し否定する声に対しては、そんなことできないという人たちに対しては、ひとつ国民の魂を端的に象徴するあの不朽の信条でもって、必ずやこう答えましょう。"""
        );
    }

    @Nested
    class OfArrayTest {

        private static Stream<String> getMessageStream_() {
            return getMessageStream();
        }

        @MethodSource({"getMessageStream_"})
        @ParameterizedTest
        void of__(String message) {
            var array = OfArray.of(message);
            assertNotNull(array);
            assertTrue(message.startsWith(OfArray.getMessage(array)));
        }

        @MethodSource({"getMessageStream_"})
        @ParameterizedTest
        void copyOf__(String message) {
            var original = OfArray.of(message);
            var copy = OfArray.copyOf(original);
            assertNotNull(copy);
            assertEquals(OfArray.getTimestamp(original),
                         OfArray.getTimestamp(copy));
            assertEquals(OfArray.getMessage(original),
                         OfArray.getMessage(copy));
        }

        @Test
        void getTimestamp__() {
            var array = OfArray.empty();
            try (var mock = mockStatic(OfArray.class,
                                       withSettings().defaultAnswer(CALLS_REAL_METHODS))) {
                var value = System.currentTimeMillis();
                mock.when(() -> OfArray.getTimestamp(array)).thenReturn(value);
                mock.clearInvocations();
                var actual = OfArray.getTimestampAsInstant(array);
                assertNotNull(actual);
                assertEquals(value, actual.toEpochMilli());
                mock.verify(() -> OfArray.getTimestamp(array), times(1));
            }
        }

        @Test
        void setTimestamp__() {
            var array = OfArray.empty();
            try (var mock = mockStatic(OfArray.class,
                                       withSettings().defaultAnswer(CALLS_REAL_METHODS))) {
                var instant = Instant.now();
                OfArray.setTimestamp(array, instant);
                mock.verify(() -> OfArray.setTimestamp(array, instant.toEpochMilli()), times(1));
            }
        }

        @Test
        void getMessage__() {
            var array = OfArray.empty();
            var message = OfArray.getMessage(array);
            assertNotNull(message);
            assertTrue(message.isBlank());
        }

        @MethodSource({"getMessageStream_"})
        @ParameterizedTest
        void setMessage__(String message) {
            var array = OfArray.empty();
            OfArray.setMessage(array, message);
            assertTrue(message.startsWith(OfArray.getMessage(array)));
        }
    }

    @Nested
    class OfBufferTest {

        private static Stream<String> getMessageStream_() {
            return getMessageStream();
        }

        @MethodSource({"getMessageStream_"})
        @ParameterizedTest
        void of__(String message) {
            var buffer = OfBuffer.of(message);
            assertNotNull(buffer);
            assertTrue(message.startsWith(OfBuffer.getMessage(buffer)));
        }

        @MethodSource({"getMessageStream_"})
        @ParameterizedTest
        void copyOf__(String message) {
            var original = OfBuffer.of(message);
            var copy = OfBuffer.copyOf(original);
            assertNotNull(copy);
            assertEquals(OfBuffer.getTimestamp(original), OfBuffer.getTimestamp(copy));
            assertEquals(OfBuffer.getMessage(original), OfBuffer.getMessage(copy));
        }

        @Test
        void getTimestamp__() {
            var buffer = OfBuffer.empty();
            try (var mock = mockStatic(OfBuffer.class,
                                       withSettings().defaultAnswer(CALLS_REAL_METHODS))) {
                var value = System.currentTimeMillis();
                mock.when(() -> OfBuffer.getTimestamp(same(buffer))).thenReturn(value);
                mock.clearInvocations();
                var actual = OfBuffer.getTimestampAsInstant(buffer);
                assertNotNull(actual);
                assertEquals(Instant.ofEpochMilli(value), actual);
                mock.verify(() -> OfBuffer.getTimestamp(buffer), times(1));
            }
        }

        @Test
        void setTimestamp__() {
            var buffer = OfBuffer.empty();
            try (var mock = mockStatic(OfBuffer.class,
                                       withSettings().defaultAnswer(CALLS_REAL_METHODS))) {
                var instant = Instant.now();
                OfBuffer.setTimestamp(buffer, instant);
                mock.verify(
                        () -> OfBuffer.setTimestamp(buffer, instant.toEpochMilli()),
                        times(1)
                );
            }
        }

        @Test
        void getMessage__() {
            var buffer = OfBuffer.empty();
            var message = OfBuffer.getMessage(buffer);
            assertNotNull(message);
            assertTrue(message.isBlank());
        }

        @MethodSource({"getMessageStream_"})
        @ParameterizedTest
        void setMessage__(String message) {
            var buffer = OfBuffer.empty();
            OfBuffer.setMessage(buffer, message);
            assertTrue(message.startsWith(OfBuffer.getMessage(buffer)));
        }
    }
}
