package com.github.jinahya.hello.miscellaneous.c03chat;

import com.github.jinahya.hello.util.HelloWorldLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.LongFunction;

@Slf4j
public class _ChatMessage {

    private static final int TIMESTAMP_OFFSET = 0;

    private static final int TIMESTAMP_LENGTH = Long.BYTES;

    private static final int MESSAGE_LENGTH_OFFSET = TIMESTAMP_OFFSET + TIMESTAMP_LENGTH;

    private static final int MESSAGE_LENGTH_LENGTH = Byte.BYTES;

    private static final int MESSAGE_BYTES_OFFSET = MESSAGE_LENGTH_OFFSET + MESSAGE_LENGTH_LENGTH;

    private static final int MESSAGE_BYTES_LENGTH = 255;

    static final int BYTES = MESSAGE_BYTES_OFFSET + MESSAGE_BYTES_LENGTH;

    private static final Charset MESSAGE_CHARSET = StandardCharsets.UTF_8;

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

    static int getInt(ByteBuffer buffer, int index, int length) {
        if (length > Integer.BYTES) {
            throw new IllegalArgumentException(
                    "length(" + length + ") is greater than " + Integer.BYTES);
        }
        if (buffer.hasArray()) {
            return getInt(buffer.array(), buffer.arrayOffset() + index, length);
        }
        var result = buffer.get(index++) & 0xFF;
        for (int i = 0; i < length; i++) {
            result <<= Byte.SIZE;
            result |= (buffer.get(index++) & 0xFF);
        }
        return result;
    }

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

    static void setInt(ByteBuffer buffer, int index, int length, int value) {
        if (length > Integer.BYTES) {
            throw new IllegalArgumentException(
                    "length(" + length + ") is greater than " + Integer.BYTES);
        }
        if (buffer.hasArray()) {
            setInt(buffer.array(), buffer.arrayOffset() + index, length, value);
            return;
        }
        var i = index + length - 1;
        buffer.put(i--, (byte) (value & 0xFF));
        while (i >= index) {
            value >>= Byte.SIZE;
            buffer.put(i--, (byte) (value & 0xFF));
        }
    }

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

    static long getLong(ByteBuffer buffer, int index, int length) {
        if (buffer.hasArray()) {
            return getLong(buffer.array(), buffer.arrayOffset() + index, length);
        }
        var result = buffer.get(index++) & 0xFFL;
        for (int i = 0; i < length; i++) {
            result <<= Byte.SIZE;
            result |= (buffer.get(index++) & 0xFFL);
        }
        return result;
    }

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

    static void setLong(ByteBuffer buffer, int index, int length, long value) {
        if (length > Long.BYTES) {
            throw new IllegalArgumentException(
                    "length(" + length + ") is greater than " + Long.BYTES);
        }
        if (buffer.hasArray()) {
            setLong(buffer.array(), buffer.arrayOffset() + index, length, value);
            return;
        }
        var i = index + length - 1;
        buffer.put(i--, (byte) (value & 0xFF));
        while (i >= index) {
            value >>= Byte.SIZE;
            buffer.put(i--, (byte) (value & 0xFF));
        }
    }

    private static long getTimestamp(byte[] array, int offset) {
        return getLong(array, offset + TIMESTAMP_OFFSET, TIMESTAMP_LENGTH);
    }

    static long getTimestamp(byte[] array) {
        return getTimestamp(array, 0);
    }

    static long getTimestamp(ByteBuffer buffer) {
        if (buffer.hasArray()) {
            return getTimestamp(buffer.array(), buffer.arrayOffset());
        }
        return getLong(buffer, TIMESTAMP_OFFSET, TIMESTAMP_LENGTH);
    }

    static <R> R getTimestamp(byte[] array, LongFunction<R> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return mapper.apply(getTimestamp(array));
    }

    static <R> R getTimestamp(ByteBuffer buffer, LongFunction<R> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return mapper.apply(getTimestamp(buffer));
    }

    private static void setTimestamp(byte[] array, int offset, long timestamp) {
        setLong(array, offset + TIMESTAMP_OFFSET, TIMESTAMP_LENGTH, timestamp);
    }

    static void setTimestamp(byte[] array, long timestamp) {
        Objects.requireNonNull(array, "array is null");
        if (timestamp < 0) {
            throw new IllegalArgumentException("timestamp(" + timestamp + ") is negative");
        }
        setTimestamp(array, 0, timestamp);
    }

    static void setTimestamp(ByteBuffer buffer, long timestamp) {
        Objects.requireNonNull(buffer, "buffer is null");
        if (timestamp < 0) {
            throw new IllegalArgumentException("timestamp(" + timestamp + ") is negative");
        }
        if (buffer.hasArray()) {
            setTimestamp(buffer.array(), buffer.arrayOffset(), timestamp);
            return;
        }
        setLong(buffer, TIMESTAMP_OFFSET, TIMESTAMP_LENGTH, timestamp);
    }

    static void setTimestampWithCurrentTimeMillis(byte[] array) {
        setTimestamp(array, System.currentTimeMillis());
    }

    static void setTimestampWithCurrentTimeMillis(ByteBuffer buffer) {
        setTimestamp(buffer, System.currentTimeMillis());
    }

    private static int getMessageLength(byte[] array, int arrayOffset) {
        return getInt(array, arrayOffset + MESSAGE_LENGTH_OFFSET, MESSAGE_LENGTH_LENGTH);
    }

    private static int getMessageLength(byte[] array) {
        Objects.requireNonNull(array, "array is null");
        return getMessageLength(array, 0);
    }

    private static int getMessageLength(ByteBuffer buffer) {
        if (buffer.hasArray()) {
            return getMessageLength(buffer.array(), buffer.arrayOffset());
        }
        return getInt(buffer, MESSAGE_LENGTH_OFFSET, MESSAGE_LENGTH_LENGTH);
    }

    private static void setMessageLength(byte[] array, int arrayOffset, int value) {
        setInt(array, arrayOffset + MESSAGE_LENGTH_OFFSET, MESSAGE_LENGTH_LENGTH, value);
    }

    private static void setMessageLength(byte[] array, int value) {
        setMessageLength(array, 0, value);
    }

    private static void setMessageLength(ByteBuffer buffer, int value) {
        if (buffer.hasArray()) {
            setMessageLength(buffer.array(), buffer.arrayOffset(), value);
            return;
        }
        setInt(buffer, MESSAGE_LENGTH_OFFSET, MESSAGE_LENGTH_LENGTH, value);
    }

    private static String getMessage(byte[] array, int arrayOffset) {
        return new String(
                array,                              // <bytes>
                arrayOffset + MESSAGE_BYTES_OFFSET, // <offset>
                getMessageLength(array),            // <length>
                MESSAGE_CHARSET                     // <charset>
        );
    }

    static String getMessage(byte[] array) {
        Objects.requireNonNull(array, "array is null");
        return getMessage(array, 0);
    }

    static String getMessage(ByteBuffer buffer) {
        Objects.requireNonNull(buffer, "buffer is null");
        if (buffer.hasArray()) {
            return getMessage(buffer.array(), buffer.arrayOffset());
        }
        var dst = new byte[getMessageLength(buffer)];
        buffer.get(MESSAGE_BYTES_OFFSET, dst);
        return new String(
                dst,            // <bytes>
                MESSAGE_CHARSET // <charset>
        );
    }

    private static void setMessage(byte[] array, int arrayOffset, String message) {
//        message = '[' + Optional.ofNullable(System.getProperty("user.name")).orElse("unknown")
//                  + "] " + message;
        byte[] bytes = HelloWorldLangUtils.trim(message, MESSAGE_CHARSET, MESSAGE_BYTES_LENGTH);
        setMessageLength(array, bytes.length);
        System.arraycopy(bytes, 0, array, arrayOffset + MESSAGE_BYTES_OFFSET, bytes.length);
        Arrays.fill(array, arrayOffset + MESSAGE_BYTES_OFFSET + bytes.length, array.length,
                    (byte) 0);
    }

    static void setMessage(byte[] array, String message) {
        setMessage(array, 0, message);
    }

    static void setMessage(ByteBuffer buffer, String message) {
        Objects.requireNonNull(buffer, "buffer is null");
        if (buffer.hasArray()) {
            setMessage(buffer.array(), buffer.arrayOffset(), message);
            return;
        }
        byte[] bytes = HelloWorldLangUtils.trim(message, MESSAGE_CHARSET, MESSAGE_BYTES_LENGTH);
        setMessageLength(buffer, bytes.length);
        buffer.put(MESSAGE_BYTES_OFFSET, bytes);
    }

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");

    private static String toString(Temporal temporal, String message) {
        return TIMESTAMP_FORMATTER.format(temporal) + ' ' + message;
    }

    private static String toString(byte[] array, int arrayOffset) {
        var temporal = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(getTimestamp(array, arrayOffset)),
                ZoneId.systemDefault()
        );
        return toString(temporal, getMessage(array, arrayOffset));
    }

    static String toString(byte[] array) {
        return toString(array, 0);
    }

    static String toString(ByteBuffer buffer) {
        if (buffer.hasArray()) {
            return toString(buffer.array(), buffer.arrayOffset());
        }
        var temporal = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(getTimestamp(buffer)),
                ZoneId.systemDefault()
        );
        return toString(temporal, getMessage(buffer));
    }

    static byte[] arrayOf(long timestamp, String message) {
        var array = newEmptyArray();
        setTimestamp(array, timestamp);
        setMessage(array, message);
        return array;
    }

    static ByteBuffer bufferOf(long timestamp, String message) {
        return ByteBuffer.wrap(arrayOf(timestamp, message));
    }

    static byte[] arrayOf(String message) {
        return arrayOf(System.currentTimeMillis(), message);
    }

    static ByteBuffer bufferOf(String message) {
        return bufferOf(System.currentTimeMillis(), message);
    }

    private static byte[] copy(byte[] array, int arrayOffset) {
        return Arrays.copyOfRange(array, arrayOffset, array.length);
    }

    static byte[] copy(byte[] array) {
        return copy(array, 0);
    }

    static ByteBuffer copy(ByteBuffer buffer) {
        if (buffer.hasArray()) {
            return ByteBuffer.wrap(copy(buffer.array(), buffer.arrayOffset()));
        }
        var copy = newEmptyBuffer();
        buffer.get(0, copy.array());
        return copy;
    }

    private static String PRINT_FORMAT = "%1$s%n";

    private static void printTo(byte[] array, int arrayOffset, PrintStream printer) {
        printer.printf(PRINT_FORMAT, toString(array, arrayOffset));
    }

    static void printTo(byte[] array, PrintStream printer) {
        printTo(array, 0, printer);
    }

    static void printTo(ByteBuffer buffer, PrintStream printer) {
        if (buffer.hasArray()) {
            printTo(buffer.array(), buffer.arrayOffset(), printer);
            return;
        }
        printer.printf(PRINT_FORMAT, toString(buffer));
    }

    static void printToSystemOut(byte[] array) {
        printTo(array, System.out);
    }

    static void printToSystemOut(ByteBuffer buffer) {
        printTo(buffer, System.out);
    }

    private _ChatMessage() {
        throw new IllegalArgumentException("");
    }
}
