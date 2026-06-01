package com.github.jinahya.hello.api.util;

/*-
 * #%L
 * verbose-hello-world-api
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

import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.nio.charset.*;
import java.util.*;

/**
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("JavaLangUtils")
@Slf4j
class JavaLangUtilsTest {

    @DisplayName("isPrimitive(clazz)")
    @Nested
    class IsPrimitiveTest {

        private static Set<Class<?>> getPrimitiveClasses() {
            return JavaLangUtils.WRAPPER_CLASSES.keySet();
        }

        @DisplayName("should return <true> when the <clazz> is a primitive class")
        @MethodSource({"getPrimitiveClasses"})
        @ParameterizedTest
        void __(final Class<?> clazz) {
            Assertions.assertTrue(JavaLangUtils.isPrimitive(clazz));
        }
    }

    @DisplayName("isWrapper(clazz)")
    @Nested
    class IsWrapperTest {

        private static Set<Class<?>> getWrapperClasses() {
            return JavaLangUtils.PRIMITIVE_CLASSES.keySet();
        }

        @DisplayName("should return <true> when the <clazz> is a wrapper class")
        @MethodSource({"getWrapperClasses"})
        @ParameterizedTest
        void __(final Class<?> clazz) {
            Assertions.assertTrue(JavaLangUtils.isWrapper(clazz));
        }
    }

    @DisplayName("trimByCodepoints(string, charset, bytes)")
    @Nested
    class TrimByCodepointsTest {

        // https://tatoeba.org/ko/
        @ValueSource(strings = {
                "",
                " ",
                "   ",
                "У нього було сиве волосся.",
                "Trovu, kiu vizitas vian profilon.",
                "There is no public toilet.",
                "Ur zmireɣ ad d-sɣeɣ tasewlaft yelhan am tin-nnem.",
                "La réunion s'est tenue hier.",
                "私はあなたのご両親にかなり気に入られているようです。",
                "汝英語會話係啵？",
                "어떻게 그의 아버지가 저 남자의 정체를 간파한 것인지 알 수 없어요."
        })
        @DisplayName("""
                should trim the <string> within the given byte <length>
                across multi-byte encodings""")
        @ParameterizedTest
        void __(String string) {
            var charset = StandardCharsets.UTF_8;
            var previous = string;
            for (int bytes = previous.getBytes(charset).length + 1; bytes > 0;
                 bytes--) {
                var trimmed = JavaLangUtils.trimByCodepoints(string,
                                                             charset,
                                                             bytes);
                Assertions.assertTrue(trimmed.getBytes(charset).length <= bytes);
                Assertions.assertTrue(previous.startsWith(trimmed));
                previous = trimmed;
            }
        }
    }
}
