package com.github.jinahya.hello.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class HelloWorldLangUtilsTest {

    @DisplayName("trim(string, charset, length)")
    @Nested
    class TrimTest {

        // https://tatoeba.org/ko/
        @ValueSource(strings = {
                "У нього було сиве волосся.",
                "Trovu, kiu vizitas vian profilon.",
                "There is no public toilet.",
                "Ur zmireɣ ad d-sɣeɣ tasewlaft yelhan am tin-nnem.",
                "La réunion s'est tenue hier.",
                "私はあなたのご両親にかなり気に入られているようです。",
                "汝英語會話係啵？",
                "어떻게 그의 아버지가 저 남자의 정체를 간파한 것인지 알 수 없어요."
        })
        @ParameterizedTest
        void __(String string) {
            var charset = StandardCharsets.UTF_8;
            for (int length = 255; length >= 0; length--) {
                var bytes = HelloWorldLangUtils.trim(string, charset, length);
                var trimmed = new String(bytes, charset);
                assertTrue(string.startsWith(trimmed));
            }
        }
    }
}
