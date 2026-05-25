package com.github.jinahya.hello.api._java_text;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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

import com.github.jinahya.hello.api.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.nio.charset.*;
import java.text.*;
import java.util.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_SetText_BreakIterator__Test extends HelloWorldTest {

    @BeforeEach
    void __stubService() {
        doAnswer(i -> {
            final var iterator = i.getArgument(0, BreakIterator.class);
            iterator.setText(HelloWorldTestConstants.HELLO_WORLD_STRING);
            return iterator;
        }).when(service()).setText(notNull());
    }

    @Test
    void __CharacterInstance() {
        final var iterator = service().setText(BreakIterator.getCharacterInstance(Locale.ROOT));
        int s = iterator.first();
        for (int e = iterator.next(); e != BreakIterator.DONE; s = e, e = iterator.next()) {
            System.out.printf("[%2d, %2d): %s%n", s, e,
                              HelloWorldTestConstants.HELLO_WORLD_STRING.substring(s, e));
        }
    }

    @Test
    void __WordInstance() {
        final var iterator = service().setText(BreakIterator.getWordInstance(Locale.ROOT));
        int s = iterator.first();
        for (int e = iterator.next(); e != BreakIterator.DONE; s = e, e = iterator.next()) {
            System.out.printf("[%2d, %2d): %s%n", s, e,
                              HelloWorldTestConstants.HELLO_WORLD_STRING.substring(s, e));
        }
    }

    @Test
    void __LineInstance() {
        final var iterator = service().setText(BreakIterator.getLineInstance(Locale.ROOT));
        int s = iterator.first();
        for (int e = iterator.next(); e != BreakIterator.DONE; s = e, e = iterator.next()) {
            System.out.printf("[%2d, %2d): %s%n", s, e,
                              HelloWorldTestConstants.HELLO_WORLD_STRING.substring(s, e));
        }
    }

    @Test
    void __SentenceInstance() {
        final var iterator = service().setText(BreakIterator.getSentenceInstance(Locale.ROOT));
        int s = iterator.first();
        for (int e = iterator.next(); e != BreakIterator.DONE; s = e, e = iterator.next()) {
            System.out.printf("[%2d, %2d): %s%n", s, e,
                              HelloWorldTestConstants.HELLO_WORLD_STRING.substring(s, e));
        }
    }

    private static Stream<String> strings() {
        return Stream.of(
                "Hello, world! Goodbye, world!",
                "안녕, 세상! 잘 가, 세상!",
                "こんにちは、世界！さようなら、世界！",
                "你好，世界！再见，世界！",
                "Привет, мир! Прощай, мир!",
                "مرحبا بالعالم! وداعا أيها العالم!"
        );
    }

    @MethodSource("strings")
    @ParameterizedTest
    void __Iterators(final String string) {
        System.out.printf("original: ⁨%s⁩%n", string);
        {
            System.out.println("--- character ---");
            final var iterator = BreakIterator.getCharacterInstance(Locale.ROOT);
            iterator.setText(string);
            int s = iterator.first();
            for (int e = iterator.next(); e != BreakIterator.DONE; s = e, e = iterator.next()) {
                System.out.printf("  [%2d, %2d): ⁨%s⁩%n", s, e, string.substring(s, e));
            }
        }
        {
            System.out.println("--- word ------");
            final var iterator = BreakIterator.getWordInstance(Locale.ROOT);
            iterator.setText(string);
            int s = iterator.first();
            for (int e = iterator.next(); e != BreakIterator.DONE; s = e, e = iterator.next()) {
                System.out.printf("  [%2d, %2d): ⁨%s⁩%n", s, e, string.substring(s, e));
            }
        }
        {
            System.out.println("--- line ------");
            final var iterator = BreakIterator.getLineInstance(Locale.ROOT);
            iterator.setText(string);
            int s = iterator.first();
            for (int e = iterator.next(); e != BreakIterator.DONE; s = e, e = iterator.next()) {
                System.out.printf("  [%2d, %2d): ⁨%s⁩%n", s, e, string.substring(s, e));
            }
        }
        {
            System.out.println("--- sentence --");
            final var iterator = BreakIterator.getSentenceInstance(Locale.ROOT);
            iterator.setText(string);
            int s = iterator.first();
            for (int e = iterator.next(); e != BreakIterator.DONE; s = e, e = iterator.next()) {
                System.out.printf("  [%2d, %2d): ⁨%s⁩%n", s, e, string.substring(s, e));
            }
        }
    }

    private static String truncate(final String string, final int bytes) {
        Objects.requireNonNull(string, "string is null");
        if (bytes < 0) {
            throw new IllegalArgumentException("bytes is negative: " + bytes);
        }
        if (string.isEmpty() || bytes == 0) {
            return "";
        }
        final var iterator = BreakIterator.getCharacterInstance(Locale.ROOT);
        iterator.setText(string);
        int last = 0;
        int byteCount = 0;
        for (int b = iterator.next(); b != BreakIterator.DONE; last = b, b = iterator.next()) {
            int segBytes = 0;
            for (int i = last; i < b; ) {
                final int cp = string.codePointAt(i);
                segBytes += cp < 0x80 ? 1
                                      : cp < 0x800 ? 2
                                                   : cp < 0x10000 ? 3
                                                                  : 4;
                i += Character.charCount(cp);
            }
            if (byteCount + segBytes > bytes) {
                return string.substring(0, last);
            }
            byteCount += segBytes;
        }
        return string;
    }

    @MethodSource("strings")
    @ParameterizedTest
    void __ChopAtMost20Bytes(final String string) {
        final var originalBytes = string.getBytes(StandardCharsets.UTF_8).length;
        final var truncated = truncate(string, 20);
        final var truncatedBytes = truncated.getBytes(StandardCharsets.UTF_8).length;
        System.out.printf(" original: (%2d) ⁨%s⁩%n", originalBytes, string);
        System.out.printf("truncated: (%2d) ⁨%s⁩%n", truncatedBytes, truncated);
        assertTrue(truncatedBytes <= 20);
        assertTrue(string.startsWith(truncated));
    }
}
