package com.github.jinahya.hello.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
class JavaLangArrayUtilsTest {

    static Stream<byte[]> randomByteArrayStream() {
        return Stream.concat(
                IntStream.range(0, ThreadLocalRandom.current().nextInt(8) + 1)
                        .mapToObj(i -> new byte[ThreadLocalRandom.current().nextInt(8) + 1]),
                Stream.of(new byte[0])
        );
    }

    @DisplayName("randomize(array, offset, length)")
    @Nested
    class RandomizeArrayOffsetLengthTest {

        @DisplayName("should throw a NullPointerException when <array> argument is <null>")
        @Test
        void _ThrowNullPointerException_ArrayIsNull() {
            // ------------------------------------------------------------------------------- given
            final var array = (byte[]) null;
            final var offset = 0;
            final var length = 0;
            // --------------------------------------------------------------------------- when/then
            Assertions.assertThrows(
                    NullPointerException.class,
                    () -> JavaLangArrayUtils.randomize(array, offset, length)
            );
        }

        @DisplayName("should throw a NullPointerException when <offset> argument is <negative>")
        @Test
        void _IndexOutOfBoundsException_OffsetIsNegative() {
            // ------------------------------------------------------------------------------- given
            final var array = new byte[0];
            final var offset = ThreadLocalRandom.current().nextInt() | Integer.MIN_VALUE;
            final var length = 0;
            // --------------------------------------------------------------------------- when/then
            Assertions.assertThrows(
                    IndexOutOfBoundsException.class,
                    () -> JavaLangArrayUtils.randomize(array, offset, length)
            );
        }

        @DisplayName("should throw a NullPointerException when <length> argument is <negative>")
        @Test
        void _IndexOutOfBoundsException_LengthIsNegative() {
            // ------------------------------------------------------------------------------- given
            final var array = new byte[0];
            final var offset = 0;
            final var length = ThreadLocalRandom.current().nextInt() | Integer.MIN_VALUE;
            // --------------------------------------------------------------------------- when/then
            Assertions.assertThrows(
                    IndexOutOfBoundsException.class,
                    () -> JavaLangArrayUtils.randomize(array, offset, length)
            );
        }

        @MethodSource({
                "com.github.jinahya.hello.util.JavaLangArrayUtilsTest#randomByteArrayStream"
        })
        @ParameterizedTest
        void _IndexOutOfBoundsException_OffsetPlusLengthIsGreaterThanArrayLength(
                final byte[] array) {
            // ------------------------------------------------------------------------------- given
            final var offset = ThreadLocalRandom.current().nextInt((array.length << 1) + 1);
            final var length = array.length - offset + 1;
            assert offset + length > array.length;
            // --------------------------------------------------------------------------- when/then
            Assertions.assertThrows(
                    IndexOutOfBoundsException.class,
                    () -> JavaLangArrayUtils.randomize(array, offset, length)
            );
            Assertions.assertThrows(
                    IndexOutOfBoundsException.class,
                    () -> JavaLangArrayUtils.randomize(array, Integer.MAX_VALUE, Integer.MAX_VALUE)
            );
        }

        @MethodSource({
                "com.github.jinahya.hello.util.JavaLangArrayUtilsTest#randomByteArrayStream"
        })
        @ParameterizedTest
        void __(final byte[] array) {
            // ------------------------------------------------------------------------------- given
            final var offset = ThreadLocalRandom.current().nextInt(array.length + 1);
            final var length = ThreadLocalRandom.current().nextInt(array.length - offset + 1);
            // -------------------------------------------------------------------------------- when
            final var result = JavaLangArrayUtils.randomize(array, offset, length);
            // -------------------------------------------------------------------------------- then
            Assertions.assertSame(array, result);
            for (int i = 0; i < offset; i++) {
                Assertions.assertEquals(0, array[i]);
            }
            for (int i = offset + length; i < array.length; i++) {
                Assertions.assertEquals(0, array[i]);
            }
        }
    }

    @DisplayName("randomize(array, offset)")
    @Nested
    class RandomizeArrayOffsetTest {

        @DisplayName("should throw a NullPointerException when <array> argument is <null>")
        @Test
        void _ThrowNullPointerException_ArrayIsNull() {
            // ------------------------------------------------------------------------------- given
            final var array = (byte[]) null;
            final var offset = 0;
            // --------------------------------------------------------------------------- when/then
            Assertions.assertThrows(
                    NullPointerException.class,
                    () -> JavaLangArrayUtils.randomize(array, offset)
            );
        }

        @DisplayName("should throw a NullPointerException when <offset> argument is <negative>")
        @Test
        void _IndexOutOfBoundsException_OffsetIsNegative() {
            // ------------------------------------------------------------------------------- given
            final var array = new byte[0];
            final var offset = ThreadLocalRandom.current().nextInt() | Integer.MIN_VALUE;
            // --------------------------------------------------------------------------- when/then
            Assertions.assertThrows(
                    IndexOutOfBoundsException.class,
                    () -> JavaLangArrayUtils.randomize(array, offset)
            );
        }

        @MethodSource({
                "com.github.jinahya.hello.util.JavaLangArrayUtilsTest#randomByteArrayStream"
        })
        @ParameterizedTest
        void _IndexOutOfBoundsException_OffsetIsGreaterThanArrayLength(
                final byte[] array) {
            // ------------------------------------------------------------------------------- given
            final var offset = ThreadLocalRandom.current().nextInt(
                    array.length,
                    (array.length << 1) + 1
            ) + 1;
            assert offset > array.length;
            // --------------------------------------------------------------------------- when/then
            Assertions.assertThrows(
                    IndexOutOfBoundsException.class,
                    () -> JavaLangArrayUtils.randomize(array, offset)
            );
            Assertions.assertThrows(
                    IndexOutOfBoundsException.class,
                    () -> JavaLangArrayUtils.randomize(array, Integer.MAX_VALUE)
            );
        }

        @MethodSource({
                "com.github.jinahya.hello.util.JavaLangArrayUtilsTest#randomByteArrayStream"
        })
        @ParameterizedTest
        void __(final byte[] array) {
            // ------------------------------------------------------------------------------- given
            final var offset = ThreadLocalRandom.current().nextInt(array.length + 1);
            // -------------------------------------------------------------------------------- when
            final var result = JavaLangArrayUtils.randomize(array, offset);
            // -------------------------------------------------------------------------------- then
            Assertions.assertSame(array, result);
            for (int i = 0; i < offset; i++) {
                Assertions.assertEquals(0, array[i]);
            }
        }
    }

    @DisplayName("randomize(array)")
    @Nested
    class RandomizeArrayTest {

        @DisplayName("should throw a NullPointerException when <array> argument is <null>")
        @Test
        void _ThrowNullPointerException_ArrayIsNull() {
            // ------------------------------------------------------------------------------- given
            final var array = (byte[]) null;
            final var offset = 0;
            // --------------------------------------------------------------------------- when/then
            Assertions.assertThrows(
                    NullPointerException.class,
                    () -> JavaLangArrayUtils.randomize(array, offset)
            );
        }

        @MethodSource({
                "com.github.jinahya.hello.util.JavaLangArrayUtilsTest#randomByteArrayStream"
        })
        @ParameterizedTest
        void __(final byte[] array) {
            // ------------------------------------------------------------------------------- given
            // -------------------------------------------------------------------------------- when
            final var result = JavaLangArrayUtils.randomize(array);
            // -------------------------------------------------------------------------------- then
            Assertions.assertSame(array, result);
        }
    }
}
