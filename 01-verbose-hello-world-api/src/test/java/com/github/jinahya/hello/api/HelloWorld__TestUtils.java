package com.github.jinahya.hello.api;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
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

import com.github.jinahya.hello.api.util.*;
import com.github.tomakehurst.wiremock.*;
import com.github.tomakehurst.wiremock.client.*;
import com.github.tomakehurst.wiremock.core.*;
import lombok.extern.slf4j.*;
import org.awaitility.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.*;
import org.mockito.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.security.*;
import java.time.*;
import java.time.temporal.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

import static java.util.Objects.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A collection of test-side helpers shared across the {@link HelloWorld} service test classes.
 * <p>
 * The class groups three kinds of utilities:
 * <ul>
 *   <li><strong>Reference payload constructors</strong> — {@link #hello_world_byte_array()},
 *       {@link #hello_world_int_array()}, {@link #hello_world_int_stream()},
 *       {@link #hello_world_byte_stream()}, {@link #hello_world_string()},
 *       {@link #hello_world_char_array()}, {@link #hello_world_byte_buffer()}, and
 *       {@link #hello_world_char_buffer()} return fresh copies of the canonical
 *       {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} payload in different shapes for
 *       assertion comparisons.</li>
 *   <li><strong>Mock stubbing helpers</strong> — the {@code <verb>_<argType>_<does>(service)}
 *       methods (e.g., {@link #set_array_returns_the_array(HelloWorld)},
 *       {@link #put_buffer12_increases_buffer_position_by_12(HelloWorld)},
 *       {@link #write_outputstream_writes_hello_world_bytes(HelloWorld)}) stub a single
 *       {@link HelloWorld} method on a {@linkplain Mockito#mock(Class) mock} service to perform
 *       the most common "actual hello-world bytes" behaviour, then return the same service for
 *       chaining. Each helper validates that the passed-in object is a mock via
 *       {@link #requireMock(HelloWorld)}.</li>
 *   <li><strong>Mock verification helpers</strong> — methods named
 *       {@code <verb>_<argType>_invoked_once(service)} (e.g.,
 *       {@link #set_array12_invoked_once(HelloWorld)},
 *       {@link #put_buffer12_invoked_once(HelloWorld)}) verify a single invocation occurred on
 *       the mock and return the captured argument for further assertions.</li>
 * </ul>
 * The class also exposes small support utilities for HTTP-server-backed tests
 * ({@link #executeWithHttpEchoStarted(IntFunction)}), file pre-population
 * ({@link #writeSome(File)}, {@link #writeSome(Path)}), and Awaitility waits
 * ({@link #awaitFor(Duration)}, {@link #awaitForOneSecond()}).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@SuppressWarnings({
        "java:S101"
})
public final class HelloWorld__TestUtils {

    /**
     * Starts a {@link WireMockServer} on a dynamic port that echoes every request back as the
     * response (status {@code 200}, same {@code Content-Type}, body equal to the request body — via
     * WireMock's response templating), then invokes {@code function.apply(port).execute()} with the
     * live port. Each request is logged to {@link System#out}. The server is stopped in a
     * {@code finally} block; thrown {@code Throwable}s are logged but not rethrown.
     *
     * @param function a factory that takes the chosen port and returns the
     *                 {@link Executable test action} to run against it; must not be {@code null}.
     * @throws NullPointerException if the {@code function} is {@code null}.
     */
    public static void executeWithHttpEchoStarted(
            final IntFunction<? extends Executable> function) {
        requireNonNull(function, "function is null");
        final var server = new WireMockServer(
                WireMockConfiguration.wireMockConfig()
                        .dynamicPort()
                        .globalTemplating(true)
        );
        try {
            server.start();
            server.addMockServiceRequestListener((request, response) -> {
                System.out.printf("%s %s %s%n", request.getMethod(),
                                  request.getPathAndQueryWithoutPrefix(), request.getProtocol());
                request.getHeaders().all().forEach(
                        h -> h.values().forEach(v -> System.out.printf("%s: %s%n", h.key(), v))
                );
                System.out.printf("%n%s%n", request.getBodyAsString());
                System.out.println("-------------------------------------------------------------");
            });
            server.stubFor(
                    WireMock.any(WireMock.anyUrl()).willReturn(
                            WireMock.aResponse()
                                    .withStatus(200)
                                    .withHeader("Content-Type", "{{request.headers.Content-Type}}")
                                    .withBody("{{request.body}}")
                                    .withTransformers("response-template")
                    )
            );
            function.apply(server.port()).execute();
        } catch (final Throwable t) {
            log.error("failed to execute with HTTP server started", t);
        } finally {
            server.stop();
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a byte array containing the {@value HelloWorld__TestConstants#HELLO_WORLD_STRING}
     * bytes.
     *
     * @return a byte array containing the {@value HelloWorld__TestConstants#HELLO_WORLD_STRING}
     * bytes.
     */
    public static byte[] hello_world_byte_array() {
        return HelloWorld__TestConstants.HELLO_WORLD_STRING.getBytes(StandardCharsets.US_ASCII);
    }

    /**
     * Returns an int array containing the {@value HelloWorld__TestConstants#HELLO_WORLD_STRING}
     * bytes, each widened to an {@code int} as an unsigned 8-bit value.
     *
     * @return an int array containing the {@value HelloWorld__TestConstants#HELLO_WORLD_STRING}
     * bytes.
     */
    public static int[] hello_world_int_array() {
        final var bytes = hello_world_byte_array();
        final var ints = new int[bytes.length];
        for (var i = 0; i < bytes.length; i++) {
            ints[i] = bytes[i] & 0xFF;
        }
        return ints;
    }

    /**
     * Returns an {@link IntStream} of the {@value HelloWorld__TestConstants#HELLO_WORLD_STRING}
     * bytes widened to unsigned 8-bit {@code int}s, sourced from {@link #hello_world_int_array()}.
     *
     * @return an {@link IntStream} of the {@value HelloWorld__TestConstants#HELLO_WORLD_STRING}
     * bytes.
     */
    public static IntStream hello_world_int_stream() {
        return IntStream.of(hello_world_int_array());
    }

    /**
     * Returns a {@link Stream} of boxed {@link Byte}s for each
     * {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} byte, sourced from
     * {@link #hello_world_int_stream()}.
     *
     * @return a {@link Stream} of the {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} bytes
     * as boxed {@link Byte}s.
     */
    public static Stream<Byte> hello_world_byte_stream() {
        return hello_world_int_stream().mapToObj(v -> (byte) v);
    }

    /**
     * Returns the canonical {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} payload as a
     * {@link String}, US-ASCII decoded from {@link #hello_world_byte_array()}.
     *
     * @return the canonical payload as a {@link String}.
     */
    public static String hello_world_string() {
        return new String(hello_world_byte_array(), StandardCharsets.US_ASCII);
    }

    /**
     * Returns the canonical {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} payload as a
     * fresh {@code char[]} of length {@value HelloWorld#BYTES}, with each byte widened to
     * {@code char}.
     *
     * @return the canonical payload as a {@code char[]}.
     */
    public static char[] hello_world_char_array() {
        final var bytes = hello_world_byte_array();
        final var chars = new char[bytes.length];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) bytes[i];
        }
        return chars;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Asserts that the specified object is a Mockito mock and returns it; throws
     * {@link IllegalArgumentException} otherwise. Used by every stubbing helper to refuse to stub a
     * non-mock service (which would silently fail to take effect).
     *
     * @param object the object to check.
     * @param <T>    the {@link HelloWorld} subtype.
     * @return the given {@code object}, unchanged.
     * @throws NullPointerException     if the {@code object} is {@code null}.
     * @throws IllegalArgumentException if the {@code object} is not a Mockito mock.
     */
    public static <T extends HelloWorld> T requireMock(final T object) {
        requireNonNull(object, "object is null");
        if (!Mockito.mockingDetails(object).isMock()) {
            throw new IllegalArgumentException("not a mock: " + object);
        }
        return object;
    }

    /**
     * Asserts that the specified service is <em>not</em> a Mockito mock and returns it; throws
     * {@link IllegalArgumentException} otherwise. Used by helpers that exercise a real
     * implementation and would be misled by a mock.
     *
     * @param service the service to check.
     * @param <T>     the {@link HelloWorld} subtype.
     * @return the given {@code service}, unchanged.
     * @throws NullPointerException     if the {@code service} is {@code null}.
     * @throws IllegalArgumentException if the {@code service} is a Mockito mock.
     */
    public static <T extends HelloWorld> T requireNotMock(final T service) {
        requireNonNull(service, "service is null");
        if (Mockito.mockingDetails(service).isMock()) {
            throw new IllegalArgumentException("a mock: " + service);
        }
        return service;
    }

    // ----------------------------------------------------------------------------------- java.lang

    /**
     * Stubs given mock object's {@link HelloWorld#set(byte[]) set(array)} method to just return the
     * {@code array}.
     *
     * @param service the mock object whose {@link HelloWorld#set(byte[]) set(array)} method needs
     *                to be stubbed.
     * @return the given {@code service}.
     * @see #set_array_sets_hello_world_bytes(HelloWorld)
     * @see #set_array12_invoked_once(HelloWorld)
     */
    public static <T extends HelloWorld> T set_array_returns_the_array(final T service) {
        requireMock(service);
        doAnswer(returnsFirstArg())
                .when(service)
                .set(ArgumentMatchers.<byte[]>argThat(
                        v -> v != null && v.length >= HelloWorld.BYTES
                ));
        return service;
    }

    /**
     * Stubs the specified mock service's {@link HelloWorld#set(byte[]) set(array)} method to fill
     * the entire {@code array} with {@linkplain ThreadLocalRandom random bytes} and return it.
     * Useful when an assertion only cares that <em>some</em> 12 bytes were written, not what they
     * are.
     *
     * @param service the mock service.
     * @param <T>     the {@link HelloWorld} subtype.
     * @return the given {@code service}.
     * @see #set_array_returns_the_array(HelloWorld)
     * @see #set_array_sets_hello_world_bytes(HelloWorld)
     */
    public static <T extends HelloWorld> T set_array_sets_random_bytes(final T service) {
        requireMock(service);
        doAnswer(i -> {
            final var array = i.getArgument(0, byte[].class);
            ThreadLocalRandom.current().nextBytes(array);
            return array;
        }).when(service).set(any(byte[].class));
        return service;
    }

    /**
     * Stubs the specified mock service's {@link HelloWorld#set(byte[]) set(array)} method to set an
     * actual {@code hello, world} bytes to the {@code array}, and returns the array.
     *
     * @param service the mock service.
     * @param <T>     service type parameter
     * @return given {@code service}.
     * @see #set_array_returns_the_array(HelloWorld)
     * @see #set_array12_invoked_once(HelloWorld)
     */
    public static <T extends HelloWorld>
    T set_array_sets_hello_world_bytes(final T service) {
        requireMock(service);
        doAnswer(i -> {
            final var array = i.getArgument(0, byte[].class);
            System.arraycopy(hello_world_byte_array(), 0, array, 0, HelloWorld.BYTES);
            return array;
        }).when(service).set(any(byte[].class));
        return service;
    }

    /**
     * Verifies that the specified mock service's {@link HelloWorld#set(byte[]) set(array)} method
     * is invoked once, and returns the captured array.
     *
     * @param service the mock service.
     * @return captured array.
     */
    public static byte[] set_array12_invoked_once(final HelloWorld service) {
        requireMock(service);
        final var captor = ArgumentCaptor.forClass(byte[].class);
        verify(service, times(1)).set(captor.capture());
        final var array = captor.getValue();
        assertNotNull(array);
        assertEquals(HelloWorld.BYTES, array.length);
        return array;
    }

    // ------------------------------------------------------------------------------------- java.io
    public static void closeSilently(final Closeable closeable) {
        try {
            closeable.close();
        } catch (final IOException ioe) {
            log.error("failed to close {}", closeable, ioe);
        }
    }

    /**
     * Stubs the specified mock service's {@link HelloWorld#append(File) append(file)} method to
     * append the actual {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} bytes to the given
     * file via a {@link FileOutputStream} (append mode), and return the file.
     *
     * @param service the mock service.
     * @param <T>     the {@link HelloWorld} subtype.
     * @return the given {@code service}.
     * @throws IOException if an I/O error occurs (declared for stubbing convenience; the lambda
     *                     itself rethrows).
     */
    public static <T extends HelloWorld> T append_file_appends_hello_world(final T service)
            throws IOException {
        requireMock(service);
        doAnswer(i -> {
            final var file = i.getArgument(0, File.class);
            try (var stream = new FileOutputStream(file, true)) {
                stream.write(hello_world_byte_array());
                stream.flush();
            }
            return file;
        }).when(service).append(ArgumentMatchers.<File>notNull());
        return service;
    }

    /**
     * Stubs the specified mock service's {@link HelloWorld#write(DataOutput) write(output)} method
     * to write the actual {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} bytes to the given
     * {@link DataOutput} and return it.
     *
     * @param service the mock service.
     * @param <T>     the {@link HelloWorld} subtype.
     * @return the given {@code service}.
     * @throws IOException declared for stubbing convenience.
     */
    public static <T extends HelloWorld>
    T write_dataoutput_writes_hello_world_bytes(final T service) throws IOException {
        requireMock(service);
        doAnswer(i -> {
            final var output = i.getArgument(0, DataOutput.class);
            output.write(hello_world_byte_array());
            return output;
        }).when(service).write(ArgumentMatchers.<DataOutput>notNull());
        return service;
    }

    /**
     * Stubs the specified mock service's {@link HelloWorld#write(OutputStream) write(stream)}
     * method to write exactly {@value HelloWorld#BYTES} zero bytes (a
     * {@code new byte[HelloWorld.BYTES]}) to the stream and return it. Useful when an assertion
     * only cares that 12 bytes were written, not what they are.
     *
     * @param service the mock service.
     * @param <T>     the {@link HelloWorld} subtype.
     * @return the given {@code service}.
     * @throws IOException declared for stubbing convenience.
     * @see #write_outputstream_writes_hello_world_bytes(HelloWorld)
     */
    public static <T extends HelloWorld>
    T write_stream_writes_12_bytes(final T service) throws IOException {
        requireMock(service);
        doAnswer(i -> {
            final var stream = i.getArgument(0, OutputStream.class);
            stream.write(new byte[HelloWorld.BYTES]);
            return stream;
        })
                .when(service)
                .<OutputStream>write(notNull());
        return service;
    }

    /**
     * Stubs the specified mock service's {@link HelloWorld#write(OutputStream) write(stream)}
     * method to write the actual {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} bytes to the
     * stream and return it.
     *
     * @param service the mock service.
     * @param <T>     the {@link HelloWorld} subtype.
     * @return the given {@code service}.
     * @throws IOException declared for stubbing convenience.
     */
    public static <T extends HelloWorld>
    T write_outputstream_writes_hello_world_bytes(final T service) throws IOException {
        requireMock(service);
        doAnswer(i -> {
            final var stream = i.getArgument(0, OutputStream.class);
            stream.write(hello_world_byte_array());
            return stream;
        }).when(service).write(ArgumentMatchers.<OutputStream>notNull());
        return service;
    }

    /**
     * Alias of {@link #write_outputstream_writes_hello_world_bytes(HelloWorld)} kept for older call
     * sites: stubs {@link HelloWorld#write(OutputStream) write(stream)} to write the actual
     * hello-world bytes.
     *
     * @param service the mock service.
     * @param <T>     the {@link HelloWorld} subtype.
     * @return the given {@code service}.
     * @throws IOException declared for stubbing convenience.
     */
    public static <T extends HelloWorld>
    T write_stream_writes_hello_world_bytes(final T service) throws IOException {
        requireMock(service);
        doAnswer(i -> {
            final var stream = i.getArgument(0, OutputStream.class);
            stream.write(hello_world_byte_array());
            return stream;
        }).when(service).write(ArgumentMatchers.<OutputStream>notNull());
        return service;
    }

    /**
     * Verifies that the specified mock service's
     * {@link HelloWorld#write(OutputStream) write(stream)} method was invoked exactly once, and
     * returns the captured non-{@code null} {@link OutputStream} argument.
     *
     * @param service the mock service.
     * @return the captured {@link OutputStream} argument.
     * @throws IOException declared for stubbing convenience.
     */
    public static OutputStream write_stream_invoked_once(final HelloWorld service)
            throws IOException {
        requireMock(service);
        final var captor = ArgumentCaptor.forClass(OutputStream.class);
        verify(service, times(1)).write(captor.capture());
        final var value = captor.getValue();
        assertNotNull(value);
        return value;
    }

    /**
     * Stubs the {@link HelloWorld#write(Writer)} method of the specified mock service to write an
     * array {@value HelloWorld#BYTES} {@code char}s.
     *
     * @param service the service mock to stub.
     * @param <T>     service type parameter
     * @return given {@code service}.
     * @throws IOException if an I/O error occurs.
     */
    public static <T extends HelloWorld>
    T write_writer_writes_12_chars(final T service) throws IOException {
        requireMock(service);
        doAnswer(i -> {
            final var writer = i.getArgument(0, Writer.class);
            writer.write(new char[HelloWorld.BYTES]);
            return writer;
        })
                .when(service)
                .write(ArgumentMatchers.<Writer>notNull());
        return service;
    }

    /**
     * Stubs the specified mock service's {@link HelloWorld#write(Writer) write(writer)} method to
     * write the actual {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} characters (via
     * {@link #hello_world_char_array()}) to the writer and return it.
     *
     * @param service the mock service.
     * @param <T>     the {@link HelloWorld} subtype.
     * @return the given {@code service}.
     * @throws IOException declared for stubbing convenience.
     */
    public static <T extends HelloWorld>
    T write_writer_writes_hello_world_string(final T service) throws IOException {
        requireMock(service);
        doAnswer(i -> {
            final var writer = i.getArgument(0, Writer.class);
            writer.write(hello_world_char_array());
            return writer;
        }).when(service).write(ArgumentMatchers.<Writer>notNull());
        return service;
    }

    // ------------------------------------------------------------------------------------ java.net

    /**
     * Stubs the specified mock service's {@link HelloWorld#append(DatagramPacket) append(packet)}
     * method to increase the packet's {@linkplain DatagramPacket#getLength() length} by
     * {@value HelloWorld#BYTES} and return the packet. Useful when an assertion only cares that 12
     * bytes were appended, not what they are.
     *
     * @param service the mock service.
     * @param <T>     the {@link HelloWorld} subtype.
     * @return the given {@code service}.
     */
    public static <T extends HelloWorld> T append_packet_increases_packet_length_by_12(
            final T service) {
        requireMock(service);
        doAnswer(i -> {
            final var packet = i.getArgument(0, DatagramPacket.class);
            packet.setLength(packet.getLength() + HelloWorld.BYTES);
            return packet;
        }).when(service).<DatagramPacket>append(any());
        return service;
    }

    /**
     * Stubs the specified mock service's {@link HelloWorld#append(DatagramPacket) append(packet)}
     * method to copy the actual {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} bytes into
     * the packet's data buffer (starting at {@linkplain DatagramPacket#getOffset() offset}),
     * increase the packet's {@linkplain DatagramPacket#getLength() length} by
     * {@value HelloWorld#BYTES}, and return the packet.
     *
     * @param service the mock service.
     * @param <T>     the {@link HelloWorld} subtype.
     * @return the given {@code service}.
     */
    public static <T extends HelloWorld> T append_packet_appends_hello_world_bytes(
            final T service) {
        requireMock(service);
        doAnswer(i -> {
            final var packet = i.getArgument(0, DatagramPacket.class);
            System.arraycopy(hello_world_byte_array(), 0, packet.getData(), packet.getOffset(),
                             HelloWorld.BYTES);
            packet.setLength(packet.getLength() + HelloWorld.BYTES);
            return packet;
        }).when(service).<DatagramPacket>append(any());
        return service;
    }

    /**
     * Stubs the specified mock service's {@link HelloWorld#send(Socket) send(socket)} method to
     * write the actual {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} bytes to the socket's
     * {@link Socket#getOutputStream() output stream} and return the socket.
     *
     * @param service the mock service.
     * @param <T>     the {@link HelloWorld} subtype.
     * @return the given {@code service}.
     * @throws IOException declared for stubbing convenience.
     */
    public static <T extends HelloWorld> T send_socket_sends_hello_world_bytes(final T service)
            throws IOException {
        requireMock(service);
        doAnswer(i -> {
            final var socket = i.getArgument(0, Socket.class);
            socket.getOutputStream().write(hello_world_byte_array());
            return socket;
        }).when(service).send(ArgumentMatchers.<Socket>notNull());
        return service;
    }

    // ------------------------------------------------------------------------------------ java.nio

    /**
     * Returns the canonical {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} payload as a
     * fresh, read-write {@link ByteBuffer} backed by a new {@code byte[]} — position {@code 0},
     * limit and capacity {@value HelloWorld#BYTES}.
     *
     * @return the canonical payload as a {@link ByteBuffer}.
     */
    public static ByteBuffer hello_world_byte_buffer() {
        return ByteBuffer.wrap(hello_world_byte_array());
    }

    /**
     * Returns the canonical {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} payload as a
     * fresh {@link CharBuffer} wrapping the {@value HelloWorld#BYTES}-character string — position
     * {@code 0}, limit and capacity {@value HelloWorld#BYTES}.
     *
     * @return the canonical payload as a {@link CharBuffer}.
     */
    public static CharBuffer hello_world_char_buffer() {
        return StandardCharsets.US_ASCII.decode(hello_world_byte_buffer());
    }

    /**
     * Stubs given mock service's {@link HelloWorld#put(ByteBuffer) put(buffer)} method, when the
     * {@code buffer} is not {@code null} and has remaining greater than or equal to
     * {@value HelloWorld#BYTES}, to just return the {@code buffer} whose
     * {@link ByteBuffer#position() position} increased by {@value HelloWorld#BYTES}.
     *
     * @param service the mock service.
     * @return given {@code service} whose {@link HelloWorld#put(ByteBuffer)} method stubbed as
     * above.
     * @see #put_buffer12_invoked_once(HelloWorld)
     */
    public static <T extends HelloWorld>
    T put_buffer12_increases_buffer_position_by_12(final T service) {
        requireMock(service);
        doAnswer(i -> {
            final var buffer = i.getArgument(0, ByteBuffer.class);
            assert buffer != null;
            assert buffer.capacity() == HelloWorld.BYTES;
            assert buffer.remaining() == HelloWorld.BYTES;
            buffer.position(buffer.position() + HelloWorld.BYTES);
            return buffer;
        }).when(service).put(any());
        return service;
    }

    /**
     * Stubs the specified mock service's {@link HelloWorld#put(ByteBuffer) put(buffer)} method,
     * when the buffer is non-{@code null} and has at least {@value HelloWorld#BYTES} remaining, to
     * put {@value HelloWorld#BYTES} freshly generated random bytes into the buffer, forward the
     * same {@code byte[]} to the given {@code consumer} for capture, and return the buffer.
     *
     * @param service  the mock service.
     * @param consumer a consumer that receives the random bytes actually written (for later
     *                 assertion); must not be {@code null}.
     * @param <T>      the {@link HelloWorld} subtype.
     * @return the given {@code service}.
     * @throws NullPointerException if the {@code consumer} is {@code null}.
     */
    public static <T extends HelloWorld>
    T put_buffer12_put_random_bytes(final T service, final Consumer<? super byte[]> consumer) {
        requireMock(service);
        requireNonNull(consumer, "consumer is null");
        doAnswer(i -> {
            final var buffer = i.getArgument(0, ByteBuffer.class);
            assert buffer != null;
            assert buffer.capacity() == HelloWorld.BYTES;
            assert buffer.remaining() == HelloWorld.BYTES;
            final var src = new byte[HelloWorld.BYTES];
            ThreadLocalRandom.current().nextBytes(src);
            buffer.put(src);
            consumer.accept(src);
            return buffer;
        }).when(service).put(any());
        return service;
    }

    /**
     * Convenience overload of
     * {@link #put_buffer12_put_random_bytes(HelloWorld, Consumer)
     * put_buffer_will_put_12_random_bytes(service, consumer)} with a no-op consumer — use when the
     * random bytes themselves are not needed for later assertion.
     *
     * @param service the mock service.
     * @param <T>     the {@link HelloWorld} subtype.
     * @return the given {@code service}.
     */
    public static <T extends HelloWorld>
    T put_buffer12_put_random_bytes(final T service) {
        return put_buffer12_put_random_bytes(
                service,
                b -> {
                }
        );
    }

    /**
     * Stubs the specified mock service's {@link HelloWorld#put(ByteBuffer) put(buffer)} method,
     * when the buffer is non-{@code null} and has at least {@value HelloWorld#BYTES} remaining, to
     * put the actual {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} bytes into the buffer
     * and return it.
     *
     * @param service the mock service.
     * @param <T>     the {@link HelloWorld} subtype.
     * @return the given {@code service}.
     */
    public static <T extends HelloWorld>
    T put_buffer_put_actual_hello_world_bytes(final T service) {
        requireMock(service);
        doAnswer(i -> {
            final var buffer = i.getArgument(0, ByteBuffer.class);
            buffer.put(hello_world_byte_array());
            return buffer;
        })
                .when(service)
                .put(ArgumentMatchers.<ByteBuffer>argThat(
                        v -> v != null && v.remaining() >= HelloWorld.BYTES)
                );
        return service;
    }

    /**
     * Verifies that the specified mock service's {@link HelloWorld#put(ByteBuffer) put(buffer)}
     * method was invoked exactly once with a non-{@code null} {@link ByteBuffer} of capacity
     * {@value HelloWorld#BYTES}, and returns the captured buffer for further assertion.
     *
     * @param service the mock service.
     * @return the captured {@link ByteBuffer} argument.
     */
    public static ByteBuffer put_buffer12_invoked_once(final HelloWorld service) {
        requireMock(service);
        final var captor = ArgumentCaptor.forClass(ByteBuffer.class);
        verify(service, times(1)).put(captor.capture());
        final var buffer = captor.getValue();
        assertNotNull(buffer);
        assertEquals(HelloWorld.BYTES, buffer.capacity());
        return buffer;
    }

    /**
     * Stubs the specified mock service's {@link HelloWorld#append(Appendable) append(appendable)}
     * method to append exactly {@value HelloWorld#BYTES} {@code '0'} characters to the
     * {@link Appendable}, and return it. Useful when an assertion only cares that 12 chars were
     * appended, not what they are.
     *
     * @param service the mock service.
     * @param <T>     the {@link HelloWorld} subtype.
     * @return the given {@code service}.
     * @throws IOException declared for stubbing convenience.
     */
    public static <T extends HelloWorld>
    T append_appendable_appends_12_chars(final T service) throws IOException {
        requireMock(service);
        doAnswer(i -> {
            final var appendable = i.getArgument(0, Appendable.class);
            IntStream.range(0, HelloWorld.BYTES).forEach(_ -> {
                try {
                    appendable.append('0');
                } catch (final IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            });
            return appendable;
        }).when(service).append(ArgumentMatchers.<Appendable>notNull());
        return service;
    }

    public static <T extends HelloWorld>
    T append_appendable_appends_hello_world_bytes(final T service) throws IOException {
        requireMock(service);
        doAnswer(i -> {
            final var appendable = i.getArgument(0, Appendable.class);
            for (byte b : hello_world_byte_array()) {
                appendable.append((char) b);
            }
            return appendable;
        }).when(service).append(ArgumentMatchers.<Appendable>notNull());
        return service;
    }

    // --------------------------------------------------------------------------- java.nio.channels

    /**
     * Stubs the specified mock service's
     * {@link HelloWorld#write(WritableByteChannel) write(channel)} method to just return the
     * channel (no bytes written). Useful when an assertion only cares that the method was invoked,
     * not what (if anything) it wrote.
     *
     * @param service the mock service.
     * @param <T>     the {@link HelloWorld} subtype.
     * @return the given {@code service}.
     * @throws IOException declared for stubbing convenience.
     */
    public static <T extends HelloWorld>
    T write_writablebytechannel_returns_channel(final T service) throws IOException {
        requireMock(service);
        doAnswer(returnsFirstArg()).when(service).<WritableByteChannel>write(any());
        return service;
    }

    /**
     * Stubs the specified {@link HelloWorld#write(WritableByteChannel)} method to write actual
     * 'hello, world' bytes.
     *
     * @param service the service whose {@link HelloWorld#write(WritableByteChannel)} method is
     *                stubbed.
     * @param <T>     service type parameter
     * @return given {@code service}.
     * @throws IOException if an I/O error occurs.
     */
    public static <T extends HelloWorld>
    T write_writablebytechannel_writes_hello_world_buffer(final T service) throws IOException {
        requireMock(service);
        doAnswer(i -> {
            final var channel = i.getArgument(0, WritableByteChannel.class);
            for (final var src = hello_world_byte_buffer(); src.hasRemaining(); ) {
                channel.write(src);
            }
            return channel;
        }).when(service).write(ArgumentMatchers.<WritableByteChannel>notNull());
        return service;
    }

    /**
     * Stubs the specified mock service's
     * {@link HelloWorld#send(DatagramChannel, SocketAddress) send(channel, target)} method to loop
     * {@link DatagramChannel#send(ByteBuffer, SocketAddress) channel.send(buffer, target)} until a
     * non-zero count is returned (i.e., the {@value HelloWorld#BYTES}-byte datagram is actually
     * accepted by the OS), and then return the channel.
     *
     * @param service the mock service.
     * @param <T>     the {@link HelloWorld} subtype.
     * @return the given {@code service}.
     * @throws IOException declared for stubbing convenience.
     */
    public static <T extends HelloWorld>
    T send_datagramchannel_socketaddress_sends_hello_world_buffer(final T service)
            throws IOException {
        requireMock(service);
        doAnswer(i -> {
            final var channel = i.getArgument(0, DatagramChannel.class);
            final var target = i.getArgument(1, SocketAddress.class);
            final var src = hello_world_byte_buffer();
            while (src.remaining() == HelloWorld.BYTES) {
                channel.send(src, target);
            }
            return channel;
        }).when(service).<DatagramChannel>send(notNull(), notNull());
        return service;
    }

    /**
     * Stubs the specified mock service's {@link HelloWorld#send(DatagramChannel) send(channel)}
     * method, when the channel is non-{@code null} and
     * {@linkplain DatagramChannel#isConnected() connected}, to write the actual
     * {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} bytes in a write-until-drained loop,
     * and return the channel.
     *
     * @param service the mock service.
     * @param <T>     the {@link HelloWorld} subtype.
     * @return the given {@code service}.
     * @throws IOException declared for stubbing convenience.
     */
    public static <T extends HelloWorld>
    T send_datagramchannel_writes_hello_world_buffer(final T service) throws IOException {
        requireMock(service);
        doAnswer(i -> {
            final var channel = i.getArgument(0, WritableByteChannel.class);
            final var src = hello_world_byte_buffer();
            while (src.hasRemaining()) {
                channel.write(src);
            }
            return channel;
        }).when(service).send(
                ArgumentMatchers.<DatagramChannel>argThat(v -> v != null && v.isConnected())
        );
        return service;
    }

    /**
     * Stubs the specified mock service's
     * {@link HelloWorld#write(AsynchronousByteChannel) write(channel)} method to
     * {@linkplain AsynchronousByteChannel#write(ByteBuffer) channel.write(buffer)} until the
     * {@value HelloWorld#BYTES}-byte source is fully drained, blocking on each
     * {@link java.util.concurrent.Future#get() Future.get()}.
     *
     * @param service the mock service.
     * @param <T>     the {@link HelloWorld} subtype.
     * @return the given {@code service}.
     * @throws ExecutionException   declared for stubbing convenience.
     * @throws InterruptedException declared for stubbing convenience.
     */
    public static <T extends HelloWorld>
    T write_asynchornousbytechannel_writes_hello_world(final T service)
            throws ExecutionException, InterruptedException {
        requireMock(service);
        doAnswer(i -> {
            final var channel = i.getArgument(0, AsynchronousByteChannel.class);
            for (final var b = hello_world_byte_buffer(); b.hasRemaining(); ) {
                channel.write(b).get();
            }
            return channel;
        }).when(service).<AsynchronousByteChannel>write(notNull());
        return service;
    }

    /**
     * Stubs the specified mock service's
     * {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)} method to
     * write the actual {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} bytes starting at the
     * given file {@code position}, advancing {@code position} by each
     * {@link java.util.concurrent.Future#get() Future.get()} result until the source is drained,
     * and return the channel.
     *
     * @param service the mock service.
     * @param <T>     the {@link HelloWorld} subtype.
     * @return the given {@code service}.
     * @throws ExecutionException   declared for stubbing convenience.
     * @throws InterruptedException declared for stubbing convenience.
     */
    public static <T extends HelloWorld>
    T write_asynchornousfilechannel_position_writes_hello_world(final T service)
            throws ExecutionException, InterruptedException {
        requireMock(service);
        doAnswer(i -> {
            final var channel = i.getArgument(0, AsynchronousFileChannel.class);
            var position = i.getArgument(1, Long.class);
            for (final var b = hello_world_byte_buffer(); b.hasRemaining(); ) {
                position += channel.write(b, position).get();
            }
            return channel;
        }).when(service).write(notNull(), longThat(v -> v >= 0L));
        return service;
    }

    // ------------------------------------------------------------------------------ java.nio.files

    /**
     * Stubs the specified mock service's {@link HelloWorld#append(Path) append(path)} method to
     * append the actual {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} bytes to the given
     * {@link Path}, opening a {@link FileChannel} with
     * {@link StandardOpenOption#CREATE CREATE}{@code +}{@link StandardOpenOption#APPEND APPEND},
     * forcing to disk on close, and returning the path.
     *
     * @param service the mock service.
     * @param <T>     the {@link HelloWorld} subtype.
     * @return the given {@code service}.
     * @throws IOException declared for stubbing convenience.
     */
    public static <T extends HelloWorld> T append_path_appends_hello_world(final T service)
            throws IOException {
        requireMock(service);
        doAnswer(i -> {
            final var path = i.getArgument(0, Path.class);
            try (var channel = FileChannel.open(path, StandardOpenOption.CREATE,
                                                StandardOpenOption.APPEND)) {
                for (final var b = hello_world_byte_buffer(); b.hasRemaining(); ) {
                    channel.write(b);
                }
                channel.force(false);
            }
            return path;
        }).when(service).append(ArgumentMatchers.<Path>notNull());
        return service;
    }

    // ------------------------------------------------------------------------------- java.security

    /**
     * Stubs the specified mock service's {@link HelloWorld#update(Signature) update(signature)}
     * method to call {@link Signature#update(byte[]) signature.update(...)} with the actual
     * {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} bytes and return the signature.
     *
     * @param service the mock service.
     * @param <T>     the {@link HelloWorld} subtype.
     * @return the given {@code service}.
     * @throws SignatureException declared for stubbing convenience.
     */
    public static <T extends HelloWorld> T update_signature_updates_hello_world_bytes(
            final T service)
            throws SignatureException {
        requireMock(service);
        doAnswer(i -> {
            final var signature = i.getArgument(0, Signature.class);
            signature.update(hello_world_byte_array());
            return signature;
        }).when(service).update(ArgumentMatchers.<Signature>notNull());
        return service;
    }

    // ---------------------------------------------------------------------------------------------
    private static <T extends File> T writeSome_(final T file) throws IOException {
        requireNonNull(file, "file is null");
        try (var stream = new FileOutputStream(file)) {
            stream.write(new byte[ThreadLocalRandom.current().nextInt(128)]);
        }
        return file;
    }

    private static <T extends Path> T writeSome_(final T path) throws IOException {
        requireNonNull(path, "path is null");
        try (var channel = FileChannel.open(path, StandardOpenOption.WRITE)) {
            for (final var b = ByteBuffer.allocate(ThreadLocalRandom.current().nextInt(128));
                 b.hasRemaining(); ) {
                final var written = channel.write(b);
                assert written >= 0;
            }
        }
        return path;
    }

    /**
     * Writes a random number of zero bytes ({@code 0..127}) to the given {@link File}, choosing
     * between a {@link FileOutputStream}-based path and a {@link FileChannel}-based path with even
     * probability. Used to pre-populate temp files with non-empty content before exercising the
     * append/write methods.
     *
     * @param file the file to write to.
     * @param <T>  the {@link File} subtype.
     * @return the given {@code file}.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings({"unchecked"})
    public static <T extends File> T writeSome(final T file) throws IOException {
        requireNonNull(file, "file is null");
        if (ThreadLocalRandom.current().nextBoolean()) {
            return (T) writeSome_(file.toPath()).toFile();
        }
        return writeSome_(file);
    }

    /**
     * Writes a random number of zero bytes ({@code 0..127}) to the given {@link Path}, choosing
     * between a {@link FileOutputStream}-based path and a {@link FileChannel}-based path with even
     * probability. Used to pre-populate temp files with non-empty content before exercising the
     * append/write methods.
     *
     * @param path the path to write to.
     * @param <T>  the {@link Path} subtype.
     * @return the given {@code path}.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings({"unchecked"})
    public static <T extends Path> T writeSome(final T path) throws IOException {
        requireNonNull(path, "path is null");
        if (ThreadLocalRandom.current().nextBoolean()) {
            return (T) writeSome_(path.toFile()).toPath();
        }
        return writeSome_(path);
    }

    // ---------------------------------------------------------------------------------- Awaitility

    /**
     * Sleeps for at least the specified {@link Duration} using Awaitility's
     * {@link org.awaitility.core.ConditionFactory#pollDelay(Duration) pollDelay} — the assertion
     * always succeeds, so the call returns exactly when the delay elapses. Useful to pause a test
     * thread for a specific wall-clock interval without using {@link Thread#sleep(long)} (which
     * would require handling {@link InterruptedException}).
     *
     * @param duration the duration to wait; must not be {@code null}.
     */
    public static void awaitFor(final Duration duration) {
        log.debug("awaiting for {}...", duration);
        Awaitility.await()
                .timeout(duration.plusSeconds(1L))
                .pollDelay(duration)
                .untilAsserted(() -> Assertions.assertTrue(true));
    }

    /**
     * Convenience overload of {@link #awaitFor(Duration)} that takes a {@code (amount, unit)}
     * pair.
     *
     * @param amount the duration amount.
     * @param unit   the duration {@link TemporalUnit}.
     */
    public static void awaitFor(final long amount, final TemporalUnit unit) {
        awaitFor(Duration.of(amount, unit));
    }

    /**
     * Shortcut for {@link #awaitFor(long, TemporalUnit) awaitFor(1L, ChronoUnit.SECONDS)} — a
     * one-second sleep.
     */
    public static void awaitForOneSecond() {
        awaitFor(1L, ChronoUnit.SECONDS);
    }

    // ---------------------------------------------------------------------------------------------
    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private HelloWorld__TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
