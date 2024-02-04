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

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
@SuppressWarnings({
        "java:S6217" // all permitted classes are in the same file
})
abstract sealed class _ChatMessage<T extends _ChatMessage<T>>
        permits _ChatMessage.OfArray, _ChatMessage.OfBuffer {

    // ----------------------------------------------------------------------------------- TIMESTAMP
    static final int INDEX_TIMESTAMP = 0;

    static final int LENGTH_TIMESTAMP = Integer.BYTES;
//    static final int LENGTH_TIMESTAMP = Long.BYTES;

    // ------------------------------------------------------------------------------ MESSAGE_LENGTH
    static final int INDEX_MESSAGE_LENGTH = INDEX_TIMESTAMP + LENGTH_TIMESTAMP;

    static final int LENGTH_MESSAGE_LENGTH = Byte.BYTES;

    // ----------------------------------------------------------------------------- MESSAGE_CONTENT
    static final int INDEX_MESSAGE_CONTENT = INDEX_MESSAGE_LENGTH + LENGTH_MESSAGE_LENGTH;

    static final int LENGTH_MESSAGE_CONTENT_MIN = 1;

    static final int LENGTH_MESSAGE_CONTENT_MAX =
            (int) Math.pow(2, (double) LENGTH_MESSAGE_LENGTH * Byte.SIZE);

    static final Charset CHARSET_MESSAGE_CONTENT = StandardCharsets.UTF_8;

    // --------------------------------------------------------------------------------------- BYTES
    static final int BYTES = INDEX_MESSAGE_CONTENT + LENGTH_MESSAGE_CONTENT_MAX;

    // ---------------------------------------------------------------------------------------------
    static final String PROPERTY_NAME_USER_NAME = "user.name";

    static final String PROPERTY_VALUE_USER_NAME_UNKNOWN = "unknown";

    static String prependUserName(final String message) {
        if (Objects.requireNonNull(message, "message is null").isBlank()) {
            throw new IllegalArgumentException("message is blank");
        }
        return '['
               + Optional.ofNullable(System.getProperty(PROPERTY_NAME_USER_NAME))
                       .map(String::trim)
                       .filter(n -> !n.isBlank())
                       .orElse(PROPERTY_VALUE_USER_NAME_UNKNOWN)
               + "] "
               + message;
    }

    // ---------------------------------------------------------------------------------------------
    private static CharBuffer trimToBuffer(final String message) {
        if (Objects.requireNonNull(message, "message is null").isBlank()) {
            throw new IllegalArgumentException("message is blank");
        }
        final var bytes = message.getBytes(CHARSET_MESSAGE_CONTENT);
        final var buffer = ByteBuffer.wrap(
                bytes, 0, Math.min(LENGTH_MESSAGE_CONTENT_MAX, bytes.length)
        );
        try {
            return CHARSET_MESSAGE_CONTENT
                    .newDecoder()
                    .onMalformedInput(CodingErrorAction.IGNORE)
                    .decode(buffer);
        } catch (final CharacterCodingException cce) {
            throw new RuntimeException("failed to decode", cce);
        }
    }

    private static String trimToString(final String message) {
        return trimToBuffer(message).toString();
    }

    static byte[] trimToBytes(final String message) {
        return trimToString(message).getBytes(CHARSET_MESSAGE_CONTENT);
    }

    // ---------------------------------------------------------------------------------------------
    @ToString(callSuper = true)
    static final class OfArray extends _ChatMessage<OfArray> {

        static OfArray copyOf(final OfArray original) {
            final var instance = new OfArray();
            System.arraycopy(
                    original.array,
                    0,
                    instance.array,
                    0,
                    original.array.length
            );
            return instance;
        }

        private static void integralRange(final byte[] array, int to, final int from, long value) {
            while (to >= from) {
                array[to--] = (byte) (value & 0xFF);
                value >>= Byte.SIZE;
            }
        }

        private static void integral(final byte[] array, final int index, final int length,
                                     final long value) {
            integralRange(array, index + length - 1, index, value);
        }

        private static long integralRange(final byte[] array, int from, final int to) {
            long value = 0L;
            while (from < to) {
                value <<= Byte.SIZE;
                value |= array[from++] & 0xFF;
            }
            return value;
        }

        private static long integral(final byte[] array, final int index, final int length) {
            return integralRange(array, index, index + length);
        }

        private static long integral(final byte[] array) {
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
        OfArray() {
            super();
            array = new byte[BYTES];
            array[INDEX_MESSAGE_CONTENT] = 0x21;
        }

        // -----------------------------------------------------------------------------------------

        /**
         * Reads this message from specified stream.
         *
         * @param stream the stream from which this message is read.
         * @return this message.
         * @throws IOException if an I/O error occurs.
         */
        OfArray read(final InputStream stream) throws IOException {
            Objects.requireNonNull(stream, "stream is null");
            timestamp(integral(readNBytes(stream, LENGTH_TIMESTAMP)));
            messageLength((int) integral(readNBytes(stream, LENGTH_MESSAGE_LENGTH)) + 1);
            return messageContent(readNBytes(stream, messageLength()));
        }

        /**
         * Writes this message to specified stream.
         *
         * @param stream the stream to which this message is written.
         * @param <T>    stream type parameter
         * @return given {@code stream}.
         * @throws IOException if an I/O error occurs.
         */
        <T extends OutputStream> T write(final T stream) throws IOException {
            Objects.requireNonNull(stream, "stream is null");
            timestamp(Instant.now().getEpochSecond());
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

        // --------------------------------------------------------------------------- messageLength
        @Override
        int messageLength() {
            return (int) integral(array, INDEX_MESSAGE_LENGTH, LENGTH_MESSAGE_LENGTH) + 1;
        }

        OfArray messageLength(final int messageLength) {
            integral(array, INDEX_MESSAGE_LENGTH, LENGTH_MESSAGE_LENGTH, messageLength - 1L);
            return this;
        }

        // -------------------------------------------------------------------------- messageContent
        @Override
        byte[] messageContent() {
            return Arrays.copyOfRange(
                    array,                 // <original>
                    INDEX_MESSAGE_CONTENT, // <from>
                    array.length           // <to>
            );
        }

        @Override
        OfArray messageContent(final byte[] messageContent) {
            System.arraycopy(
                    messageContent,        // <src>
                    0,                     // <srcPos>
                    array,                 // <dest>
                    INDEX_MESSAGE_CONTENT, // <destPos>
                    messageContent.length  // <length>
            );
            return this;
        }

        // ---------------------------------------------------------------------------------------------
        private final byte[] array;
    }

    // -------------------------------------------------------------------------------------- buffer
    @ToString(callSuper = true)
    static final class OfBuffer extends _ChatMessage<OfBuffer> {

        static OfBuffer copyOf(final OfBuffer original) {
            return new OfBuffer(OfArray.copyOf(original.ofArray));
        }

        // -----------------------------------------------------------------------------------------
        private OfBuffer(final OfArray ofArray) {
            super();
            this.ofArray = Objects.requireNonNull(ofArray, "ofArray is null");
            buffer = ByteBuffer.wrap(this.ofArray.array).limit(0);
        }

        OfBuffer() {
            this(new OfArray());
        }

        // ----------------------------------------------------------------------------- client-side
        OfBuffer readyToWriteToServer() {
            buffer.limit(INDEX_MESSAGE_CONTENT + messageLength()).position(0);
            return this;
        }

        OfBuffer readyToReadFromServer() {
            buffer.limit(INDEX_MESSAGE_CONTENT).position(0);
            return this;
        }

        // ----------------------------------------------------------------------------- server-side
        OfBuffer readyToReadFromClient() {
            return readyToReadFromServer();
        }

        OfBuffer readyToWriteToClient() {
            return readyToWriteToServer();
        }

        // -----------------------------------------------------------------------------------------

        /**
         * Writes this message's content to specified channel.
         *
         * @param channel the channel to which this message's content is written.
         * @return the number of bytes written.
         * @throws IOException if an I/O error occurs.
         */
        int write(final WritableByteChannel channel) throws IOException {
            Objects.requireNonNull(channel, "channel is null");
            return channel.write(buffer);
        }

        int read(final ReadableByteChannel channel) throws IOException {
            Objects.requireNonNull(channel, "channel is null");
            final int r = channel.read(buffer);
            if (!buffer.hasRemaining() && buffer.limit() == INDEX_MESSAGE_CONTENT) {
                buffer.limit(buffer.limit() + messageLength());
            }
            return r;
        }

        void write(final AsynchronousByteChannel channel,
                   final CompletionHandler<Integer, ? super OfBuffer> handler) {
            Objects.requireNonNull(channel, "channel is null");
            Objects.requireNonNull(handler, "handler is null");
            assert buffer.hasRemaining();
            channel.write(buffer, this, handler);
        }

        // @formatter:off
        void read(final AsynchronousByteChannel channel,
                  final CompletionHandler<Integer, ? super OfBuffer> handler) {
            Objects.requireNonNull(channel, "channel is null");
            Objects.requireNonNull(handler, "handler is null");
            channel.read(buffer, this, new CompletionHandler<>() {
                @Override
                public void completed(final Integer result, final OfBuffer attachment) {
                    if (!buffer.hasRemaining() && buffer.limit() == INDEX_MESSAGE_CONTENT) {
                        buffer.limit(buffer.limit() + messageLength());
                    }
                    handler.completed(result, attachment);
                }
                @Override
                public void failed(final Throwable exc, final OfBuffer attachment) {
                    handler.failed(exc, attachment);
                }
            });
        }
        // @formatter:on

        void write(final AsynchronousSocketChannel channel, final long timeout, final TimeUnit unit,
                   final CompletionHandler<Integer, ? super OfBuffer> handler) {
            Objects.requireNonNull(channel, "channel is null");
            Objects.requireNonNull(handler, "handler is null");
            channel.write(buffer, timeout, unit, this, handler);
        }

        // @formatter:off
        void read(final AsynchronousSocketChannel channel, final long timeout, final TimeUnit unit,
                  final CompletionHandler<Integer, ? super OfBuffer> handler) {
            channel.read(buffer, timeout, unit, this, new CompletionHandler<>() {
                @Override
                public void completed(final Integer result, final OfBuffer attachment) {
                    if (!buffer.hasRemaining() && buffer.limit() == INDEX_MESSAGE_CONTENT) {
                        buffer.limit(buffer.limit() + messageLength());
                    }
                    handler.completed(result, attachment);
                }
                @Override
                public void failed(final Throwable exc, final OfBuffer attachment) {
                    handler.failed(exc, attachment);
                }
            });
        }
        // @formatter:on

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

        // --------------------------------------------------------------------------- messageLength
        @Override
        int messageLength() {
            return ofArray.messageLength();
        }

        @Override
        OfBuffer messageLength(final int messageLength) {
            ofArray.messageLength(messageLength);
            buffer.limit(INDEX_MESSAGE_CONTENT + messageLength());
            return this;
        }

        // -------------------------------------------------------------------------- messageContent
        @Override
        byte[] messageContent() {
            return ofArray.messageContent();
        }

        @Override
        OfBuffer messageContent(final byte[] messageContent) {
            ofArray.messageContent(messageContent);
            return this;
        }

        // --------------------------------------------------------------------------------- ofArray

        // ---------------------------------------------------------------------------------- buffer
        <R> R applyBuffer(final Function<? super ByteBuffer, ? extends R> function) {
            Objects.requireNonNull(function, "function is null");
            return function.apply(buffer);
        }

        OfBuffer acceptBuffer(final Consumer<? super ByteBuffer> consumer) {
            Objects.requireNonNull(consumer, "consumer is null");
            return applyBuffer(b -> {
                consumer.accept(b);
                return this;
            });
        }

        boolean hasRemaining() {
            return buffer.hasRemaining();
        }

        int limit() {
            return buffer.limit();
        }

        OfBuffer limit(int limit) {
            buffer.limit(limit);
            return this;
        }

        OfBuffer clear() {
            buffer.clear();
            return this;
        }

        // -----------------------------------------------------------------------------------------
        private final OfArray ofArray;

        private final ByteBuffer buffer;
    }

    // ----------------------------------------------------------------------------------- timestamp

    /**
     * Returns current value of {@code timestamp} property of this message.
     *
     * @return current value of the {@code timestamp} property of this message.
     */
    abstract long timestamp();

    /**
     * Replaces current value of {@code timestamp} property with specified value.
     *
     * @param timestamp current value of the {@code timestamp} property.
     * @return this instance.
     */
    abstract T timestamp(long timestamp);

    final T timestamp(final Instant instant) {
        return timestamp(Objects.requireNonNull(instant, "instant is null").getEpochSecond());
    }

    final T timestamp(final TemporalAccessor temporal) {
        Objects.requireNonNull(temporal, "temporal is null");
        if (temporal instanceof Instant instant) {
            return timestamp(instant);
        }
        return timestamp(Instant.from(temporal));
    }

    private String timestampString() {
        final var instant = Instant.ofEpochSecond(timestamp());
        return DateTimeFormatter.ISO_INSTANT.format(instant);
//        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(
//                java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault())
//        );
    }

    // ------------------------------------------------------------------------------- messageLength
    abstract int messageLength();

    abstract T messageLength(int messageLength);

    // ------------------------------------------------------------------------------ messageContent
    abstract byte[] messageContent();

    abstract T messageContent(byte[] messageContent);

    // ------------------------------------------------------------------------------------- message
    @NotBlank
    final String message() {
        return new String(
                messageContent(),
                0,
                messageLength(),
                CHARSET_MESSAGE_CONTENT
        );
    }

    final T message(@NotBlank final String message) {
        final var messageContent = trimToBytes(message);
        return messageLength(messageContent.length)
                .messageContent(messageContent);
    }

    // --------------------------------------------------------------------------------------- print

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
