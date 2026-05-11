package com.github.jinahya.hello.api._java_text;

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.text.BreakIterator;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_SetText_BreakIterator__Test extends HelloWorldTest {

    @BeforeEach
    void __() {
        Mockito.doAnswer(i -> {
            final var iterator = i.getArgument(0, BreakIterator.class);
            iterator.setText(HelloWorldTestConstants.HELLO_WORLD_STRING);
            return iterator;
        }).when(service()).setText(ArgumentMatchers.notNull());
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
        Assertions.assertTrue(truncatedBytes <= 20);
        Assertions.assertTrue(string.startsWith(truncated));
    }
}
