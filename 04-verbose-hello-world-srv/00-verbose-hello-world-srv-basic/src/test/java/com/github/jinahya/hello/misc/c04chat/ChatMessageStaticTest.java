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
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class ChatMessageStaticTest {

    // https://tatoeba.org/ko/
    static Stream<String> messageStream() {
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

    @DisplayName("prependUserName(message)")
    @Nested
    class PrependUserNameTest {

        @DisplayName("(blank)same")
        @ValueSource(strings = {
                "",
                " ",
                "\t"
        })
        @ParameterizedTest
        void __Blank(final String message) {
            Assertions.assertTrue(message.isBlank());
            // -------------------------------------------------------------------------------- when
            final var result = ChatMessage.prependUserName(message);
            // -------------------------------------------------------------------------------- then
            Assertions.assertSame(message, result);
        }

        @MethodSource({
                "com.github.jinahya.hello.misc.c04chat.ChatMessageStaticTest#messageStream"
        })
        @ParameterizedTest
        void __NotBlank(final String message) {
            // -------------------------------------------------------------------------------- when
            final var result = ChatMessage.prependUserName(message);
            // -------------------------------------------------------------------------------- then
            Assertions.assertTrue(result.contains(
                    Optional.ofNullable(System.getProperty(ChatMessage.PROPERTY_NAME_USER_NAME))
                            .orElse(ChatMessage.PROPERTY_VALUE_USER_NAME_UNKNOWN))
            );
        }
    }

    @DisplayName("trimToBytes(message)")
    @Nested
    class TrimToBytesTest {

        @MethodSource({
                "com.github.jinahya.hello.misc.c04chat.ChatMessageStaticTest#messageStream"
        })
        @ParameterizedTest
        void _Trimmed_NotBlank(final String message) {
            assert !message.isBlank();
            // -------------------------------------------------------------------------------- when
            final var result = ChatMessage.trimToBytes(message);
            // -------------------------------------------------------------------------------- then
            log.debug("result: {}", new String(result, ChatMessage.CHARSET_MESSAGE_CONTENT));
            Assertions.assertNotNull(result);
            Assertions.assertTrue(result.length <= ChatMessage.LENGTH_MESSAGE_CONTENT_MAX);
        }
    }
}
