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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.LongFunction;

@Slf4j
final class ___Message2 {

    // ----------------------------------------------------------------------------------- TIMESTAMP
    static final int TIMESTAMP_BYTES = Integer.BYTES;

    private static final DateTimeFormatter TIMESTAMP_TIMESTAMP = DateTimeFormatter.ISO_INSTANT;

    // ------------------------------------------------------------------------------------- MESSAGE
    static final int MESSAGE_LENGTH_BYTES = Byte.BYTES;

    static {
        assert MESSAGE_LENGTH_BYTES >= Byte.BYTES;
        assert MESSAGE_LENGTH_BYTES <= Short.BYTES;
    }

    static final int MIN_MESSAGE_CONTENT_BYTES = 1;

    static final int MAX_MESSAGE_CONTENT_BYTES =
            (int) Math.pow(2, (double) MESSAGE_LENGTH_BYTES * Byte.SIZE);

    static final Charset CHARSET_MESSAGE = StandardCharsets.UTF_8;

    // --------------------------------------------------------------------------------------- BYTES
    static final int MAX_BYTES = TIMESTAMP_BYTES + MESSAGE_LENGTH_BYTES + MAX_MESSAGE_CONTENT_BYTES;

    // ------------------------------------------------------------------------------------- message
    static String prependUserName(final String message) {
        Objects.requireNonNull(message, "message is null");
        return '['
               + Optional.ofNullable(System.getProperty("user.name"))
                       .map(String::trim)
                       .filter(n -> !n.isBlank())
                       .orElse("unknown")
               + "] "
               + message;
    }

    private static byte[] trim(final String message) {
        if (Objects.requireNonNull(message, "message is null").isBlank()) {
            throw new IllegalArgumentException("message is blank");
        }
        final var buffer = ByteBuffer.wrap(message.getBytes(CHARSET_MESSAGE));
        try {
            return CHARSET_MESSAGE
                    .newDecoder()
                    .onMalformedInput(CodingErrorAction.IGNORE)
                    .decode(buffer)
                    .toString()
                    .getBytes(CHARSET_MESSAGE);
        } catch (final CharacterCodingException cce) {
            throw new RuntimeException(cce);
        }
    }

    private static void trim(final String message, final Consumer<? super byte[]> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        consumer.accept(trim(message));
    }

    // ---------------------------------------------------------------------------------------------
    private static <T extends OutputStream> T integral(final T stream, final int bytes,
                                                       final long value)
            throws IOException {
        for (var size = (bytes - 1) * Byte.SIZE; size >= 0; size -= Byte.SIZE) {
            stream.write((int) (value >> size));
        }
        return stream;
    }

    private static <T extends InputStream> long integral(final T stream, int bytes)
            throws IOException {
        var value = 0L;
        for (int r; bytes > 0; bytes--) {
            if ((r = stream.read()) == -1) {
                throw new EOFException("premature eof");
            }
            value <<= Byte.SIZE;
            value |= r;
        }
        return value;
    }

    static <T extends OutputStream> T write(final T stream, final String message)
            throws IOException {
        Objects.requireNonNull(stream, "stream is null");
        if (Objects.requireNonNull(message, "message is null").isBlank()) {
            throw new IllegalArgumentException("message is blank");
        }
        return new ___Message2(Instant.now().getEpochSecond(), message)
                .write(stream);
    }

    static ___Message2 read(final InputStream stream) throws IOException {
        Objects.requireNonNull(stream, "stream is null");
        final var timestamp = integral(stream, TIMESTAMP_BYTES);
        final var messageLength = (int) integral(stream, MESSAGE_LENGTH_BYTES);
        final var messageBytes = new byte[messageLength + 1];
        if (stream.readNBytes(messageBytes, 0, messageBytes.length) < messageBytes.length) {
            throw new EOFException("premature eof");
        }
        final var message = new String(messageBytes);
        return new ___Message2(timestamp, message);
    }

    // ---------------------------------------------------------------------------------------------

    private static void putIntegral(final ByteBuffer buffer, int bytes, long value) {
        Objects.requireNonNull(buffer, "buffer is null");
        if (bytes <= 0) {
            throw new IllegalArgumentException("bytes(" + bytes + ") <= 0");
        }
        buffer.position(buffer.position() + bytes);
        while (bytes > 0) {
            buffer.put(buffer.position() + --bytes, (byte) (value & 0xFF));
        }
    }

    private static long getIntegral(final ByteBuffer buffer, int bytes) {
        Objects.requireNonNull(buffer, "buffer is null");
        if (bytes <= 0) {
            throw new IllegalArgumentException("bytes(" + bytes + ") <= 0");
        }
        long value = 0L;
        for (; bytes > 0; bytes--) {
            value |= (buffer.get() & 0xFF);
            value <<= Byte.SIZE;
        }
        return value;
    }

    static <T extends WritableByteChannel> T write(final T channel, final String message,
                                                   final ByteBuffer buffer)
            throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        if (Objects.requireNonNull(message, "message is null").isBlank()) {
            throw new IllegalArgumentException("message is blank");
        }
        if (Objects.requireNonNull(buffer, "buffer is null").capacity() < MAX_BYTES) {
            throw new IllegalArgumentException(
                    "buffer.capacity(" + buffer.capacity() + ") < " + MAX_BYTES);
        }
        buffer.clear();
        putIntegral(buffer, TIMESTAMP_BYTES, Instant.now().getEpochSecond());
        trim(message, b -> {
            putIntegral(buffer, MESSAGE_LENGTH_BYTES, b.length - 1L);
            buffer.put(b);
        });
        for (buffer.flip(); buffer.hasRemaining(); ) {
            channel.write(buffer);
        }
        return channel;
    }

    static <T extends ReadableByteChannel> T read(
            final T channel, final ByteBuffer buffer,
            final LongFunction<? extends Consumer<? super String>> function)
            throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        if (Objects.requireNonNull(buffer, "buffer is null").capacity() < MAX_BYTES) {
            throw new IllegalArgumentException(
                    "buffer.capacity(" + buffer.capacity() + ") < " + MAX_BYTES);
        }
        Objects.requireNonNull(function, "function is null");
        // ------------------------------------------------------------------------------- timestamp
        buffer.clear().limit(TIMESTAMP_BYTES);
        while (buffer.hasRemaining()) {
            if (channel.read(buffer) == -1) {
                throw new EOFException("premature eof");
            }
        }
        buffer.position(buffer.position() - TIMESTAMP_BYTES);
        final var timestamp = getIntegral(buffer, TIMESTAMP_BYTES);
        assert !buffer.hasRemaining();
        // --------------------------------------------------------------------------------- message
        buffer.limit(buffer.limit() + MESSAGE_LENGTH_BYTES);
        while (buffer.hasRemaining()) {
            if (channel.read(buffer) == -1) {
                throw new EOFException("premature eof");
            }
        }
        buffer.position(buffer.position() - MESSAGE_LENGTH_BYTES);
        final var messageLength = (int) getIntegral(buffer, MESSAGE_LENGTH_BYTES);
        buffer.limit(buffer.limit() + messageLength);
        while (buffer.hasRemaining()) {
            if (channel.read(buffer) == -1) {
                throw new EOFException("premature eof");
            }
        }
        final var messageBytes = new byte[messageLength];
        buffer.position(buffer.position() - messageLength);
        buffer.get(messageBytes);
        final String message = new String(messageBytes, CHARSET_MESSAGE);
        // ---------------------------------------------------------------------------- apply/accept
        function.apply(timestamp).accept(message);
        // ---------------------------------------------------------------------------------- return
        return channel;
    }

    // ---------------------------------------------------------------------------------------------
    private ___Message2(final long timestamp, final String message) {
        super();
        this.timestamp = timestamp;
        this.message = Objects.requireNonNull(message, "message is null");
    }

    // ---------------------------------------------------------------------------------------------
    <T extends OutputStream> T write(final T stream) throws IOException {
        Objects.requireNonNull(stream, "stream is null");
        if (Objects.requireNonNull(message, "message is null").isBlank()) {
            throw new IllegalArgumentException("message is blank");
        }
        integral(stream, TIMESTAMP_BYTES, timestamp);
        final var messageBytes = trim(message);
        integral(stream, MESSAGE_LENGTH_BYTES, messageBytes.length - 1L);
        stream.write(messageBytes);
        return stream;
    }

    // ---------------------------------------------------------------------------------------------
    void print(final PrintStream printer) {
        Objects.requireNonNull(printer, "printer is null");
        printer.printf(
                "[%1$s] %2$s%n",
                TIMESTAMP_TIMESTAMP.format(Instant.ofEpochSecond(timestamp)),
                message
        );
    }

    // ---------------------------------------------------------------------------------------------
    private long timestamp;

    private String message;
}
