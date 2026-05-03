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

import com.github.jinahya.hello.api.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.DataOutput;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.Signature;
import java.security.SignatureException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

/**
 * Utilities for testing {@link HelloWorld} service classes.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@SuppressWarnings({
        "java:S101"
})
public final class HelloWorldTestUtils {

    /**
     * Starts an HTTP server which responds requested content, and executes the specified function.
     *
     * @param function the function to execute.
     */
    public static void executeWithHttpServerStarted(
            final IntFunction<? extends Executable> function) {
        Objects.requireNonNull(function, "function is null");
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
     * Returns a byte array containing the {@value HelloWorldTestConstants#HELLO_WORLD_STRING}
     * bytes.
     *
     * @return a byte array containing the {@value HelloWorldTestConstants#HELLO_WORLD_STRING}
     * bytes.
     */
    public static byte[] hello_world_byte_array() {
        return HelloWorldTestConstants.HELLO_WORLD_STRING.getBytes(StandardCharsets.US_ASCII);
    }

    public static String hello_world_string() {
        return new String(hello_world_byte_array(), StandardCharsets.US_ASCII);
    }

    public static ByteBuffer hello_world_byte_buffer() {
        return ByteBuffer.wrap(hello_world_byte_array());
    }

    public static char[] hello_world_char_array() {
        final var bytes = hello_world_byte_array();
        final var chars = new char[bytes.length];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) bytes[i];
        }
        return chars;
    }

    // ---------------------------------------------------------------------------------------------
    static <T extends HelloWorld> T requireMock(final T object) {
        Objects.requireNonNull(object, "object is null");
        if (!Mockito.mockingDetails(object).isMock()) {
            throw new IllegalArgumentException("not a mock: " + object);
        }
        return object;
    }

    static <T extends HelloWorld> T requireNotMock(final T service) {
        Objects.requireNonNull(service, "service is null");
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
     * @see #set_array_sets_actual_hello_world_bytes(HelloWorld)
     * @see #set_array12_invoked_once(HelloWorld)
     */
    public static <T extends HelloWorld> T set_array_returns_the_array(final T service) {
        requireMock(service);
        Mockito.doAnswer(i -> i.getArgument(0))
                .when(service)
                .set(ArgumentMatchers.<byte[]>argThat(
                        v -> v != null && v.length >= HelloWorld.BYTES
                ));
        return service;
    }

    public static <T extends HelloWorld> T set_array_sets_random_bytes(final T service) {
        requireMock(service);
        Mockito.doAnswer(i -> {
            final var array = i.getArgument(0, byte[].class);
            ThreadLocalRandom.current().nextBytes(array);
            return array;
        }).when(service).set(ArgumentMatchers.any(byte[].class));
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
    T set_array_sets_actual_hello_world_bytes(final T service) {
        requireMock(service);
        Mockito.doAnswer(i -> {
            final var array = i.getArgument(0, byte[].class);
            System.arraycopy(hello_world_byte_array(), 0, array, 0, HelloWorld.BYTES);
            return array;
        }).when(service).set(ArgumentMatchers.any(byte[].class));
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
        Mockito.verify(service, Mockito.times(1)).set(captor.capture());
        final var array = captor.getValue();
        Assertions.assertNotNull(array);
        Assertions.assertEquals(HelloWorld.BYTES, array.length);
        return array;
    }

    // ------------------------------------------------------------------------------------- java.io
    public static <T extends HelloWorld> T append_file_appends_hello_world(final T service)
            throws IOException {
        requireMock(service);
        Mockito.doAnswer(i -> {
            final var file = i.getArgument(0, File.class);
            try (var stream = new FileOutputStream(file, true)) {
                stream.write(hello_world_byte_array());
                stream.flush();
            }
            return file;
        }).when(service).append(ArgumentMatchers.<File>notNull());
        return service;
    }

    public static <T extends HelloWorld>
    T write_dataoutput_writes_hello_world_bytes(final T service) throws IOException {
        requireMock(service);
        Mockito.doAnswer(i -> {
            final var output = i.getArgument(0, DataOutput.class);
            output.write(hello_world_byte_array());
            return output;
        }).when(service).write(ArgumentMatchers.<DataOutput>notNull());
        return service;
    }

    public static <T extends HelloWorld>
    T write_stream_will_write_12_bytes(final T service) throws IOException {
        requireMock(service);
        Mockito.doAnswer(i -> {
                    final var stream = i.getArgument(0, OutputStream.class);
                    stream.write(new byte[HelloWorld.BYTES]);
                    return stream;
                })
                .when(service)
                .write(ArgumentMatchers.<OutputStream>notNull());
        return service;
    }

    public static <T extends HelloWorld>
    T write_outputstream_writes_hello_world_bytes(final T service) throws IOException {
        requireMock(service);
        Mockito.doAnswer(i -> {
            final var stream = i.getArgument(0, OutputStream.class);
            stream.write(hello_world_byte_array());
            return stream;
        }).when(service).write(ArgumentMatchers.<OutputStream>notNull());
        return service;
    }

    public static <T extends HelloWorld>
    T write_stream_will_write_actual_hello_world_bytes(final T service) throws IOException {
        requireMock(service);
        Mockito.doAnswer(i -> {
            final var stream = i.getArgument(0, OutputStream.class);
            stream.write(hello_world_byte_array());
            return stream;
        }).when(service).write(ArgumentMatchers.<OutputStream>notNull());
        return service;
    }

    public static OutputStream write_stream_invoked_once(final HelloWorld service)
            throws IOException {
        requireMock(service);
        final var captor = ArgumentCaptor.forClass(OutputStream.class);
        Mockito.verify(service, Mockito.times(1)).write(captor.capture());
        final var value = captor.getValue();
        Assertions.assertNotNull(value);
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
    T write_writer_will_write_12_chars(final T service) throws IOException {
        requireMock(service);
        Mockito.doAnswer(i -> {
                    final var writer = i.getArgument(0, Writer.class);
                    writer.write(new char[HelloWorld.BYTES]);
                    return writer;
                })
                .when(service)
                .write(ArgumentMatchers.<Writer>notNull());
        return service;
    }

    public static <T extends HelloWorld>
    T write_writer_writes_hello_world_string(final T service) throws IOException {
        requireMock(service);
        Mockito.doAnswer(i -> {
            final var writer = i.getArgument(0, Writer.class);
            writer.write(hello_world_char_array());
            return writer;
        }).when(service).write(ArgumentMatchers.<Writer>notNull());
        return service;
    }

    // ------------------------------------------------------------------------------------ java.net
    public static <T extends HelloWorld> T send_socket_sends_hello_world_bytes(final T service)
            throws IOException {
        requireMock(service);
        Mockito.doAnswer(i -> {
            final var socket = i.getArgument(0, Socket.class);
            socket.getOutputStream().write(hello_world_byte_array());
            return socket;
        }).when(service).send(ArgumentMatchers.<Socket>notNull());
        return service;
    }

    // ------------------------------------------------------------------------------------ java.nio

    /**
     * Stubs given mock service's {@link HelloWorld#put(ByteBuffer) put(buffer)} method, when the
     * {@code buffer} is not {@code null} and has remaining greater than or equal to
     * {@value HelloWorld#BYTES}, to just return the {@code bufefer} whose
     * {@link ByteBuffer#position() position} increased by {@value HelloWorld#BYTES}.
     *
     * @param service the mock service.
     * @return given {@code service} whose {@link HelloWorld#put(ByteBuffer)} method stubbed as
     * above.
     * @see #put_buffer12_invoked_once(HelloWorld)
     */
    public static <T extends HelloWorld>
    T put_buffer_will_increase_buffer_position_by_12(final T service) {
        requireMock(service);
        Mockito.doAnswer(i -> {
                    final var buffer = i.getArgument(0, ByteBuffer.class);
                    buffer.position(buffer.position() + HelloWorld.BYTES);
                    return buffer;
                })
                .when(service)
                .<ByteBuffer>put(ArgumentMatchers.argThat(
                        b -> b != null && b.remaining() >= HelloWorld.BYTES
                ));
        return service;
    }

    /**
     * Stubs given mock service's {@link HelloWorld#put(ByteBuffer) put(buffer)} method, when the
     * {@code buffer} is not {@code null} and its capacity and remaining are equal to
     * {@value HelloWorld#BYTES}, to just return the {@code bufefer} whose
     * {@link ByteBuffer#position() position} increased by {@value HelloWorld#BYTES}.
     *
     * @param service the mock service.
     * @return given {@code service} whose {@link HelloWorld#put(ByteBuffer)} method stubbed as
     * above.
     * @see #put_buffer12_invoked_once(HelloWorld)
     */
    public static <T extends HelloWorld>
    T put_buffer12_will_increase_buffer_position_by_12(final T service) {
        requireMock(service);
        Mockito.doAnswer(i -> {
                    final var buffer = i.getArgument(0, ByteBuffer.class);
                    buffer.position(buffer.position() + HelloWorld.BYTES);
                    return buffer;
                })
                .when(service)
                .<ByteBuffer>put(ArgumentMatchers.argThat(
                        b -> b != null
                             && b.capacity() == HelloWorld.BYTES
                             && b.remaining() == HelloWorld.BYTES
                ));
        return service;
    }

    public static <T extends HelloWorld>
    T put_buffer_will_put_12_random_bytes(final T service,
                                          final Consumer<? super byte[]> consumer) {
        requireMock(service);
        Objects.requireNonNull(consumer, "consumer is null");
        Mockito.doAnswer(i -> {
                    final var buffer = i.getArgument(0, ByteBuffer.class);
                    final var src = new byte[HelloWorld.BYTES];
                    ThreadLocalRandom.current().nextBytes(src);
                    buffer.put(src);
                    consumer.accept(src);
                    return buffer;
                })
                .when(service)
                .put(ArgumentMatchers.argThat(
                        b -> b != null && b.remaining() >= HelloWorld.BYTES
                ));
        return service;
    }

    public static <T extends HelloWorld>
    T put_buffer_will_put_actual_hello_world_bytes(final T service) {
        requireMock(service);
        Mockito.doAnswer(i -> {
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

    public static ByteBuffer put_buffer12_invoked_once(final HelloWorld service) {
        requireMock(service);
        final var captor = ArgumentCaptor.forClass(ByteBuffer.class);
        Mockito.verify(service, Mockito.times(1)).put(captor.capture());
        final var buffer = captor.getValue();
        Assertions.assertNotNull(buffer);
        Assertions.assertEquals(HelloWorld.BYTES, buffer.capacity());
        return buffer;
    }

    public static <T extends HelloWorld>
    T append_appendable_appends_12_chars(final T service) throws IOException {
        requireMock(service);
        Mockito.doAnswer(i -> {
                    final var appendable = i.getArgument(0, Appendable.class);
                    IntStream.range(0, HelloWorld.BYTES).forEach(_ -> {
                        try {
                            appendable.append('0');
                        } catch (final IOException ioe) {
                            throw new RuntimeException(ioe);
                        }
                    });
                    return appendable;
                })
                .when(service)
                .append(ArgumentMatchers.<Appendable>notNull());
        return service;
    }

    // --------------------------------------------------------------------------- java.nio.channels

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
        Mockito.doAnswer(i -> {
            final var channel = i.getArgument(0, WritableByteChannel.class);
            final var src = hello_world_byte_buffer();
            while (src.hasRemaining()) {
                channel.write(src);
            }
            return channel;
        }).when(service).write(
                ArgumentMatchers.<WritableByteChannel>notNull()
        );
        return service;
    }

    public static <T extends HelloWorld>
    T send_datagramchannel_socketaddress_sends_hello_world_buffer(final T service)
            throws IOException {
        requireMock(service);
        Mockito.doAnswer(i -> {
            final var channel = i.getArgument(0, DatagramChannel.class);
            final var target = i.getArgument(1, SocketAddress.class);
            final var src = hello_world_byte_buffer();
            int written;
            do {
                written = channel.send(src, target);
            } while (written == 0);
            return channel;
        }).when(service).send(
                ArgumentMatchers.<DatagramChannel>notNull(),
                ArgumentMatchers.<SocketAddress>notNull()
        );
        return service;
    }

    public static <T extends HelloWorld>
    T write_datagramchannel_writes_hello_world_buffer(final T service) throws IOException {
        requireMock(service);
        Mockito.doAnswer(i -> {
            final var channel = i.getArgument(0, WritableByteChannel.class);
            final var src = hello_world_byte_buffer();
            while (src.hasRemaining()) {
                channel.write(src);
            }
            return channel;
        }).when(service).write(
                ArgumentMatchers.<DatagramChannel>argThat(v -> v != null && v.isConnected())
        );
        return service;
    }

    public static <T extends HelloWorld>
    T write_asynchornousbytechannel_writes_hello_world(final T service)
            throws ExecutionException, InterruptedException {
        requireMock(service);
        Mockito.doAnswer(i -> {
            final var channel = i.getArgument(0, AsynchronousByteChannel.class);
            for (final var b = hello_world_byte_buffer(); b.hasRemaining(); ) {
                channel.write(b).get();
            }
            return channel;
        }).when(service).write(ArgumentMatchers.<AsynchronousByteChannel>notNull());
        return service;
    }

    public static <T extends HelloWorld>
    T write_asynchornousfilechannel_position_writes_hello_world(final T service)
            throws ExecutionException, InterruptedException {
        requireMock(service);
        Mockito.doAnswer(i -> {
            final var channel = i.getArgument(0, AsynchronousFileChannel.class);
            var position = i.getArgument(1, Long.class);
            for (final var b = hello_world_byte_buffer(); b.hasRemaining(); ) {
                position += channel.write(b, position).get();
            }
            return channel;
        }).when(service).write(
                ArgumentMatchers.<AsynchronousFileChannel>notNull(),
                ArgumentMatchers.longThat(v -> v >= 0L)
        );
        return service;
    }

    // ------------------------------------------------------------------------------ java.nio.files
    public static <T extends HelloWorld> T append_path_appends_hello_world(final T service)
            throws IOException {
        requireMock(service);
        Mockito.doAnswer(i -> {
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
    public static <T extends HelloWorld> T update_signature_updates_hello_world_bytes(
            final T service)
            throws SignatureException {
        requireMock(service);
        Mockito.doAnswer(i -> {
            final var signature = i.getArgument(0, Signature.class);
            signature.update(hello_world_byte_array());
            return signature;
        }).when(service).update(ArgumentMatchers.<Signature>notNull());
        return service;
    }

    // ---------------------------------------------------------------------------------------------
    private static <T extends File> T writeSome_(final T file) throws IOException {
        Objects.requireNonNull(file, "file is null");
        try (var stream = new FileOutputStream(file)) {
            stream.write(new byte[ThreadLocalRandom.current().nextInt(128)]);
        }
        return file;
    }

    private static <T extends Path> T writeSome_(final T path) throws IOException {
        Objects.requireNonNull(path, "path is null");
        try (var channel = FileChannel.open(path, StandardOpenOption.WRITE)) {
            for (final var b = ByteBuffer.allocate(ThreadLocalRandom.current().nextInt(128));
                 b.hasRemaining(); ) {
                final var written = channel.write(b);
                assert written >= 0;
            }
        }
        return path;
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends File> T writeSome(final T file) throws IOException {
        Objects.requireNonNull(file, "file is null");
        if (ThreadLocalRandom.current().nextBoolean()) {
            return (T) writeSome_(file.toPath()).toFile();
        }
        return writeSome_(file);
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends Path> T writeSome(final T path) throws IOException {
        Objects.requireNonNull(path, "path is null");
        if (ThreadLocalRandom.current().nextBoolean()) {
            return (T) writeSome_(path.toFile()).toPath();
        }
        return writeSome_(path);
    }

    // ---------------------------------------------------------------------------------- Awaitility
    public static void awaitFor(final Duration duration) {
        log.debug("awaiting for {}...", duration);
        Awaitility.await()
                .timeout(duration.plusSeconds(1L))
                .pollDelay(duration)
                .untilAsserted(() -> Assertions.assertTrue(true));
    }

    public static void awaitFor(final long amount, final TemporalUnit unit) {
        awaitFor(Duration.of(amount, unit));
    }

    public static void awaitForOneSecond() {
        awaitFor(1L, ChronoUnit.SECONDS);
    }

    // ---------------------------------------------------------------------------------------------
    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private HelloWorldTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
