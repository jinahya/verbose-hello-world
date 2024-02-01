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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
abstract class ChatMessage<T extends ChatMessage<T>> {

    // ----------------------------------------------------------------------------------- TIMESTAMP
    static final int INDEX_TIMESTAMP = 0;

    static final int LENGTH_TIMESTAMP = Integer.BYTES;

    // ------------------------------------------------------------------------------------- MESSAGE
    static final int INDEX_MESSAGE_LENGTH = INDEX_TIMESTAMP + LENGTH_TIMESTAMP;

    static final int LENGTH_MESSAGE_LENGTH = Byte.BYTES;

    static final int INDEX_MESSAGE_CONTENT = INDEX_MESSAGE_LENGTH + LENGTH_MESSAGE_LENGTH;

    static final int LENGTH_MESSAGE_CONTENT =
            (int) Math.pow(2, (double) LENGTH_MESSAGE_LENGTH * Byte.SIZE);

    static final Charset CHARSET_MESSAGE_CONTENT = StandardCharsets.UTF_8;

    // --------------------------------------------------------------------------------------- BYTES
    static final int BYTES = INDEX_MESSAGE_CONTENT + LENGTH_MESSAGE_CONTENT;

    // ---------------------------------------------------------------------------------------------
    static String prependUserName(final String message) {
        if (Objects.requireNonNull(message, "message is null").isBlank()) {
            return message;
        }
        return '['
               + Optional.ofNullable(System.getProperty("user.name"))
                       .map(String::trim)
                       .filter(n -> !n.isBlank())
                       .orElse("unknown")
               + "] "
               + message;
    }

    static byte[] trim(final String message) {
        if (Objects.requireNonNull(message, "message is null").isBlank()) {
            throw new IllegalArgumentException("message is blank");
        }
        final var buffer = ByteBuffer.wrap(message.getBytes(CHARSET_MESSAGE_CONTENT));
        try {
            return CHARSET_MESSAGE_CONTENT
                    .newDecoder()
                    .onMalformedInput(CodingErrorAction.IGNORE)
                    .decode(buffer)
                    .toString()
                    .getBytes(CHARSET_MESSAGE_CONTENT);
        } catch (final CharacterCodingException cce) {
            throw new RuntimeException(cce);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    @Slf4j
    static final class OfArray extends ChatMessage<OfArray> {

        // ---------------------------------------------------------------------------------------------
        static void integral(final byte[] array, final int index, final int length,
                             final long value) {
            for (int i = index + length - 1; i >= index; i--) {
                array[i] = (byte) (value & 0xFF);
            }
        }

        static long integral(final byte[] array, final int index, final int length) {
            long value = 0L;
            for (int i = index + length - 1; i >= index; i--) {
                value <<= Byte.SIZE;
                value |= array[i] & 0xFF;
            }
            return value;
        }

        static long integral(final byte[] array) {
            return integral(array, 0, array.length);
        }

        private static byte[] readNBytes(final InputStream stream, final int len)
                throws IOException {
            final var bytes = stream.readNBytes(len);
            if (bytes.length < len) {
                throw new EOFException("premature eof");
            }
            return bytes;
        }

        // -----------------------------------------------------------------------------------------
        OfArray read(final InputStream stream) throws IOException {
            Objects.requireNonNull(stream, "stream is null");
            timestamp(integral(readNBytes(stream, LENGTH_TIMESTAMP)));
            final var messageLength = (int) integral(readNBytes(stream, LENGTH_MESSAGE_LENGTH)) + 1;
            final var message = new String(
                    readNBytes(stream, messageLength),
                    CHARSET_MESSAGE_CONTENT
            );
            return message(message);
        }

        <T extends OutputStream> T write(final T stream) throws IOException {
            Objects.requireNonNull(stream, "stream is null");
            timestampAsInstant(Instant.now());
            stream.write(
                    array,
                    0,
                    INDEX_MESSAGE_CONTENT + messageLength()
            );
            return stream;
        }

        // ------------------------------------------------------------------------------- timestamp
        @Override
        long timestamp() {
            return integral(array, INDEX_TIMESTAMP, LENGTH_TIMESTAMP);
        }

        @Override
        OfArray timestamp(final long timestamp) {
            integral(array, INDEX_TIMESTAMP, LENGTH_TIMESTAMP, timestamp);
            return this;
        }

        // --------------------------------------------------------------------------------- message
        private int messageLength() {
            return (int) integral(array, INDEX_MESSAGE_LENGTH, LENGTH_MESSAGE_LENGTH) + 1;
        }

        private OfArray messageLength(final int messageLength) {
            integral(array, INDEX_MESSAGE_LENGTH, LENGTH_MESSAGE_LENGTH, messageLength - 1L);
            return this;
        }

        @Override
        String message() {
            return new String(
                    Arrays.copyOfRange(
                            array,
                            INDEX_MESSAGE_CONTENT,
                            INDEX_MESSAGE_CONTENT + messageLength()
                    ),
                    CHARSET_MESSAGE_CONTENT
            );
        }

        @Override
        OfArray message(final String message) {
            if (Objects.requireNonNull(message, "message is null").isBlank()) {
                throw new IllegalArgumentException("message is blank");
            }
            final var bytes = trim(message);
            messageLength(bytes.length);
            System.arraycopy(
                    bytes,
                    0,
                    array,
                    INDEX_MESSAGE_CONTENT,
                    bytes.length
            );
            return this;
        }

        // ---------------------------------------------------------------------------------------------
        private final byte[] array = new byte[BYTES];
    }

    // -------------------------------------------------------------------------------------- buffer
    static final class OfBuffer extends ChatMessage<OfBuffer> {

        OfBuffer readyToWrite() {
            timestampAsInstant(Instant.now());
            buffer.limit(INDEX_MESSAGE_CONTENT + ofArray.messageLength()).position(0);
            return this;
        }

        OfBuffer readyToRead() {
            Arrays.fill(buffer.array(), (byte) 0);
            buffer.clear().limit(INDEX_MESSAGE_CONTENT);
            return this;
        }

        // -----------------------------------------------------------------------------------------
        int write(final WritableByteChannel channel) throws IOException {
            Objects.requireNonNull(channel, "channel is null");
            return channel.write(buffer);
        }

        int read(final ReadableByteChannel channel) throws IOException {
            if (!hasRemaining() && limit() == INDEX_MESSAGE_CONTENT) {
                buffer.limit(INDEX_MESSAGE_CONTENT + ofArray.messageLength());
            }
            final int r = channel.read(buffer);
            if (r != -1) {
                final var messageLength = ofArray.messageLength();
                if (messageLength > 0) {
                    buffer.limit(INDEX_MESSAGE_CONTENT + messageLength);
                }
            }
            return r;
        }

        <A> void write(final AsynchronousByteChannel channel, final A attachment,
                       final CompletionHandler<Integer, ? super A> handler) {
            Objects.requireNonNull(channel, "channel is null");
            channel.write(buffer, attachment, handler);
        }

        <A> void read(final AsynchronousByteChannel channel, final A attachment,
                      final CompletionHandler<Integer, ? super A> handler) {
            channel.read(buffer, attachment, handler);
        }

        // ------------------------------------------------------------------------------- timestamp
        @Override
        long timestamp() {
            return ofArray.timestamp();
        }

        @Override
        OfBuffer timestamp(final long timestamp) {
            ofArray.timestamp(timestamp);
            return this;
        }

        // --------------------------------------------------------------------------------- message
        @Override
        String message() {
            return ofArray.message();
        }

        @Override
        OfBuffer message(final String message) {
            ofArray.message(message);
            return this;
        }

        // --------------------------------------------------------------------------------- ofArray

        // ---------------------------------------------------------------------------------- buffer
        boolean hasRemaining() {
            return buffer.hasRemaining();
        }

        int limit() {
            return buffer.limit();
        }

        OfBuffer clear() {
            buffer.clear();
            return this;
        }

        // -----------------------------------------------------------------------------------------
        private final OfArray ofArray = new OfArray();

        private final ByteBuffer buffer = ByteBuffer.wrap(ofArray.array).limit(0);
    }

    // ----------------------------------------------------------------------------------- timestamp
    abstract long timestamp();

    abstract T timestamp(long timestamp);

    final Instant timestampAsInstant() {
        return Instant.ofEpochSecond(timestamp());
    }

    final T timestampAsInstant(final Instant instant) {
        return timestamp(instant.getEpochSecond());
    }

    // ------------------------------------------------------------------------------------- message
    abstract String message();

    abstract T message(String message);

    // --------------------------------------------------------------------------------------- print
    final String timestampString() {
        return DateTimeFormatter.ISO_INSTANT.format(timestampAsInstant());
    }

    @SuppressWarnings({"unchecked"})
    final T print(final PrintStream printer) {
        Objects.requireNonNull(printer, "printer is null");
        printer.printf(
                "[%1$s] %2$s%n",
                timestampString(),
                message()
        );
        return (T) this;
    }

    @SuppressWarnings({
            "java:S106" // System.out
    })
    final T print() {
        return print(System.out);
    }
}
