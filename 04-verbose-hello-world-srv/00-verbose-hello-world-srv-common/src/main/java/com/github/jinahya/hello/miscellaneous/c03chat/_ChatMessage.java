package com.github.jinahya.hello.miscellaneous.c03chat;

import com.github.jinahya.hello.util.HelloWorldLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.LongFunction;
import java.util.function.LongSupplier;
import java.util.function.ToLongFunction;

@Slf4j
class _ChatMessage {

    private static final int TIMESTAMP_OFFSET = 0;

    private static final int TIMESTAMP_LENGTH = Long.BYTES;

    private static final int LENGTH_OFFSET = TIMESTAMP_OFFSET + TIMESTAMP_LENGTH;

    private static final int LENGTH_LENGTH = Byte.BYTES;

    private static final int MESSAGE_OFFSET = LENGTH_OFFSET + LENGTH_LENGTH;

    private static final int MESSAGE_LENGTH = 255;

    static final int BYTES = MESSAGE_OFFSET + MESSAGE_LENGTH;

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");

    static byte[] newEmptyArray() {
        return new byte[BYTES];
    }

    static ByteBuffer newEmptyBuffer() {
        return ByteBuffer.wrap(newEmptyArray());
    }

    static int getInt(byte[] array, int offset, int length) {
        if (length > Integer.BYTES) {
            throw new IllegalArgumentException(
                    "length(" + length + ") is greater than " + Integer.BYTES);
        }
        var result = array[offset++] & 0xFF;
        for (int i = 1; i < length; i++) {
            result <<= Byte.SIZE;
            result |= (array[offset++] & 0xFF);
        }
        return result;
    }

//    static int getInt(ByteBuffer buffer, int index, int length) {
//        if (length > Integer.BYTES) {
//            throw new IllegalArgumentException(
//                    "length(" + length + ") is greater than " + Integer.BYTES);
//        }
//        if (buffer.hasArray()) {
//            return getInt(buffer.array(), buffer.arrayOffset() + index, length);
//        }
//        var result = buffer.get(index++) & 0xFF;
//        for (int i = 0; i < length; i++) {
//            result <<= Byte.SIZE;
//            result |= (buffer.get(index++) & 0xFF);
//        }
//        return result;
//    }

    static void setInt(byte[] array, int offset, int length, int value) {
        if (length > Integer.BYTES) {
            throw new IllegalArgumentException(
                    "length(" + length + ") is greater than " + Integer.BYTES);
        }
        var i = offset + length - 1;
        array[i--] = (byte) (value & 0xFF);
        while (i >= offset) {
            value >>= Byte.SIZE;
            array[i--] = (byte) (value & 0xFF);
        }
    }

//    static void setInt(ByteBuffer buffer, int index, int length, int value) {
//        if (length > Integer.BYTES) {
//            throw new IllegalArgumentException(
//                    "length(" + length + ") is greater than " + Integer.BYTES);
//        }
//        if (buffer.hasArray()) {
//            setInt(buffer.array(), buffer.arrayOffset() + index, length, value);
//            return;
//        }
//        var i = index + length - 1;
//        buffer.put(i--, (byte) (value & 0xFF));
//        while (i >= index) {
//            value >>= Byte.SIZE;
//            buffer.put(i--, (byte) (value & 0xFF));
//        }
//    }

    static long getLong(byte[] array, int offset, int length) {
        if (length > Long.BYTES) {
            throw new IllegalArgumentException(
                    "length(" + length + ") is greater than " + Long.BYTES);
        }
        var value = array[offset++] & 0xFFL;
        for (int i = 1; i < length; i++) {
            value <<= Byte.SIZE;
            value |= (array[offset++] & 0xFF);
        }
        return value;
    }

//    static long getLong(ByteBuffer buffer, int index, int length) {
//        if (buffer.hasArray()) {
//            return getLong(buffer.array(), buffer.arrayOffset() + index, length);
//        }
//        var result = buffer.get(index++) & 0xFFL;
//        for (int i = 0; i < length; i++) {
//            result <<= Byte.SIZE;
//            result |= (buffer.get(index++) & 0xFFL);
//        }
//        return result;
//    }

    static void setLong(byte[] array, int offset, int length, long value) {
        if (length > Long.BYTES) {
            throw new IllegalArgumentException(
                    "length(" + length + ") is greater than " + Integer.BYTES);
        }
        var i = offset + length - 1;
        array[i--] = (byte) (value & 0xFF);
        while (i >= offset) {
            value >>= Byte.SIZE;
            array[i--] = (byte) (value & 0xFF);
        }
    }

//    static void setLong(ByteBuffer buffer, int index, int length, long value) {
//        if (length > Long.BYTES) {
//            throw new IllegalArgumentException(
//                    "length(" + length + ") is greater than " + Long.BYTES);
//        }
//        if (buffer.hasArray()) {
//            setLong(buffer.array(), buffer.arrayOffset() + index, length, value);
//            return;
//        }
//        var i = index + length - 1;
//        buffer.put(i--, (byte) (value & 0xFF));
//        while (i >= index) {
//            value >>= Byte.SIZE;
//            buffer.put(i--, (byte) (value & 0xFF));
//        }
//    }

    static class OfArray {

        private static byte[] requireValid(byte[] array) {
            if (Objects.requireNonNull(array, "array is null").length != BYTES) {
                throw new IllegalArgumentException(
                        "array.length(" + array.length + ") != " + BYTES);
            }
            return array;
        }

        static byte[] empty() {
            return requireValid(new byte[BYTES]);
        }

        static byte[] of(long timestamp, String message) {
            var array = empty();
            setTimestamp(array, timestamp);
            setMessage(array, message);
            return array;
        }

        static byte[] of(String message) {
            return of(System.currentTimeMillis(), message);
        }

        static byte[] copyOf(byte[] array) {
            return Arrays.copyOfRange(requireValid(array), 0, array.length);
        }

        static long getTimestamp(byte[] array) {
            return getLong(requireValid(array), TIMESTAMP_OFFSET, TIMESTAMP_LENGTH);
        }

        static <R> R getTimestampAsMapped(byte[] array, LongFunction<R> mapper) {
            Objects.requireNonNull(mapper, "mapper is null");
            return mapper.apply(getTimestamp(array));
        }

        static Instant getTimestampAsInstant(byte[] array) {
            return getTimestampAsMapped(array, Instant::ofEpochMilli);
        }

        static void setTimestamp(byte[] array, long value) {
            setLong(requireValid(array), TIMESTAMP_OFFSET, Long.BYTES, value);
        }

        static void setTimestampAsSupplied(byte[] array, LongSupplier supplier) {
            Objects.requireNonNull(supplier, "supplier is null");
            setTimestamp(array, supplier.getAsLong());
        }

        static <T> void setTimestampAsMapped(byte[] array, ToLongFunction<? super T> mapper,
                                             T value) {
            Objects.requireNonNull(mapper, "mapper is null");
            setTimestampAsSupplied(array, () -> mapper.applyAsLong(value));
        }

        static <T> void setTimestamp(byte[] array, Instant value) {
            Objects.requireNonNull(value, "value is null");
            setTimestampAsMapped(array, Instant::toEpochMilli, value);
        }

        private static int getMessageLength(byte[] array) {
            return getInt(array, LENGTH_OFFSET, LENGTH_LENGTH);
        }

        private static void setMessageLength(byte[] array, int value) {
            setInt(array, LENGTH_OFFSET, LENGTH_LENGTH, value);
        }

        private static byte[] getMessageBytes(byte[] array) {
            return Arrays.copyOfRange(
                    array,
                    MESSAGE_OFFSET,
                    MESSAGE_OFFSET + getMessageLength(array)
            );
        }

        static String getMessage(byte[] array) {
            requireValid(array);
            return new String(getMessageBytes(array), CHARSET);
        }

        private static void setMessageBytes(byte[] array, byte[] value) {
            var messageLength = value.length;
            setMessageLength(array, messageLength);
            System.arraycopy(value, 0, array, MESSAGE_OFFSET, messageLength);
        }

        static void setMessage(byte[] array, String message) {
            requireValid(array);
            Objects.requireNonNull(message, "message is null");
            var value = HelloWorldLangUtils.trimByCodepoints(message, CHARSET, MESSAGE_LENGTH)
                    .getBytes(CHARSET);
            setMessageBytes(array, value);
        }

        static void printToSystemOut(byte[] array) {
            requireValid(array);
            var temporal = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(getTimestamp(array)),
                    ZoneId.systemDefault()
            );
            var string = TIMESTAMP_FORMATTER.format(temporal) + ' ' + getMessage(array);
            System.out.printf("%1$s%n", string);
        }
    }

    static class OfBuffer {

        private static ByteBuffer requireValid(ByteBuffer buffer) {
            Objects.requireNonNull(buffer, "buffer is null");
            if (!buffer.hasArray()) {
                throw new IllegalArgumentException(
                        "buffer is not backed by an accessible byte array");
            }
            if (buffer.arrayOffset() != 0) {
                throw new IllegalArgumentException(
                        "buffer.arrayOffset(" + buffer.arrayOffset() + ") != 0");
            }
            OfArray.requireValid(buffer.array());
            return buffer;
        }

        static ByteBuffer empty() {
            return requireValid(ByteBuffer.wrap(OfArray.empty()));
        }

        static ByteBuffer of(long timestamp, String message) {
            var buffer = empty();
            setTimestamp(buffer, timestamp);
            setMessage(buffer, message);
            return buffer;
        }

        static ByteBuffer of(String message) {
            return of(System.currentTimeMillis(), message);
        }

        static ByteBuffer copyOf(ByteBuffer buffer) {
            requireValid(buffer);
            return ByteBuffer.wrap(OfArray.copyOf(buffer.array()));
        }

        static long getTimestamp(ByteBuffer buffer) {
            requireValid(buffer);
            return OfArray.getTimestamp(buffer.array());
        }

        static <R> R getTimestampAsMapped(ByteBuffer buffer, LongFunction<R> mapper) {
            Objects.requireNonNull(mapper, "mapper is null");
            return mapper.apply(getTimestamp(buffer));
        }

        static Instant getTimestampAsInstant(ByteBuffer buffer) {
            return getTimestampAsMapped(buffer, Instant::ofEpochMilli);
        }

        static void setTimestamp(ByteBuffer buffer, long value) {
            requireValid(buffer);
            OfArray.setTimestamp(buffer.array(), value);
        }

        static void setTimestampAsSupplied(ByteBuffer buffer, LongSupplier supplier) {
            Objects.requireNonNull(supplier, "supplier is null");
            setTimestamp(buffer, supplier.getAsLong());
        }

        static <T> void setTimestampAsMapped(ByteBuffer buffer, ToLongFunction<? super T> mapper,
                                             T value) {
            Objects.requireNonNull(mapper, "mapper is null");
            setTimestampAsSupplied(buffer, () -> mapper.applyAsLong(value));
        }

        static void setTimestamp(ByteBuffer buffer, Instant value) {
            Objects.requireNonNull(value, "value is null");
            setTimestampAsMapped(buffer, Instant::toEpochMilli, value);
        }

        static void setTimestamp(ByteBuffer buffer, TemporalAccessor value) {
            setTimestamp(buffer, Instant.from(value));
        }

        static void setTimestampWithNow(ByteBuffer buffer) {
            setTimestamp(buffer, Instant.now());
        }

        static String getMessage(ByteBuffer buffer) {
            requireValid(buffer);
            return OfArray.getMessage(buffer.array());
        }

        static void setMessage(ByteBuffer buffer, String message) {
            requireValid(buffer);
            OfArray.setMessage(buffer.array(), message);
        }

        static void printToSystemOut(ByteBuffer buffer) {
            OfArray.printToSystemOut(buffer.array());
        }
    }

    private _ChatMessage() {
        throw new IllegalArgumentException("");
    }
}
