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

import lombok.*;
import lombok.experimental.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.mockito.quality.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.security.*;
import java.util.concurrent.*;

/**
 * An abstract class for testing methods defined in {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://github.com/jinahya/verbose-hello-world/issues/4">[#4] Implement HelloWorld
 * interface</a> (GitHub)
 */
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public abstract class HelloWorld__Test {

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    // ------------------------------------------------------------- HelloWorld__TestUtils shortcuts
    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#set(byte[]) set(array)} method to
     * just return the {@code array}.
     *
     * @return the {@link #service() service}.
     * @see HelloWorld__TestUtils#set_array_returns_the_array(HelloWorld)
     */
    protected HelloWorld set_array_returns_the_array() {
        return HelloWorld__TestUtils.set_array_returns_the_array(service());

    }

    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#set(byte[]) set(array)} method to
     * fill the entire {@code array} with {@linkplain ThreadLocalRandom random bytes} and return it.
     *
     * @return the {@link #service() service}.
     * @see HelloWorld__TestUtils#set_array_sets_random_bytes(HelloWorld)
     */
    protected HelloWorld set_array_sets_random_bytes() {
        return HelloWorld__TestUtils.set_array_sets_random_bytes(service());

    }

    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#set(byte[]) set(array)} method to
     * set an actual {@code hello, world} bytes to the {@code array}, and returns the array.
     *
     * @return the {@link #service() service}.
     * @see HelloWorld__TestUtils#set_array_sets_hello_world_bytes(HelloWorld)
     */
    protected HelloWorld set_array_sets_hello_world_bytes() {
        return HelloWorld__TestUtils.set_array_sets_hello_world_bytes(service());

    }

    /**
     * Verifies that the {@link #service() service}'s {@link HelloWorld#set(byte[]) set(array)}
     * method is invoked once, and returns the captured array.
     *
     * @return the captured array.
     * @see HelloWorld__TestUtils#set_array12_invoked_once(HelloWorld)
     */
    protected byte[] set_array12_invoked_once() {
        return HelloWorld__TestUtils.set_array12_invoked_once(service());

    }

    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#append(File) append(file)} method to
     * append the actual {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} bytes to the given
     * file via a {@link FileOutputStream} (append mode), and return the file.
     *
     * @return the {@link #service() service}.
     * @throws IOException declared for stubbing convenience.
     * @see HelloWorld__TestUtils#append_file_appends_hello_world(HelloWorld)
     */
    protected HelloWorld append_file_appends_hello_world() throws IOException {
        return HelloWorld__TestUtils.append_file_appends_hello_world(service());

    }

    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#write(DataOutput) write(output)}
     * method to write the actual {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} bytes to the
     * given {@link DataOutput} and return it.
     *
     * @return the {@link #service() service}.
     * @throws IOException declared for stubbing convenience.
     * @see HelloWorld__TestUtils#write_dataoutput_writes_hello_world_bytes(HelloWorld)
     */
    protected HelloWorld write_dataoutput_writes_hello_world_bytes() throws IOException {
        return HelloWorld__TestUtils.write_dataoutput_writes_hello_world_bytes(service());

    }

    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#write(OutputStream) write(stream)}
     * method to write exactly {@value HelloWorld#BYTES} zero bytes (a {@code new
     * byte[HelloWorld.BYTES]}) to the stream and return it.
     *
     * @return the {@link #service() service}.
     * @throws IOException declared for stubbing convenience.
     * @see HelloWorld__TestUtils#write_stream_writes_12_bytes(HelloWorld)
     */
    protected HelloWorld write_stream_writes_12_bytes() throws IOException {
        return HelloWorld__TestUtils.write_stream_writes_12_bytes(service());

    }

    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#write(OutputStream) write(stream)}
     * method to write the actual {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} bytes to the
     * stream and return it.
     *
     * @return the {@link #service() service}.
     * @throws IOException declared for stubbing convenience.
     * @see HelloWorld__TestUtils#write_outputstream_writes_hello_world_bytes(HelloWorld)
     */
    protected HelloWorld write_outputstream_writes_hello_world_bytes() throws IOException {
        return HelloWorld__TestUtils.write_outputstream_writes_hello_world_bytes(service());

    }

    /**
     * Alias of {@link #write_outputstream_writes_hello_world_bytes()} kept for older call sites:
     * stubs {@link HelloWorld#write(OutputStream) write(stream)} to write the actual hello-world
     * bytes.
     *
     * @return the {@link #service() service}.
     * @throws IOException declared for stubbing convenience.
     * @see HelloWorld__TestUtils#write_stream_writes_hello_world_bytes(HelloWorld)
     */
    protected HelloWorld write_stream_writes_hello_world_bytes() throws IOException {
        return HelloWorld__TestUtils.write_stream_writes_hello_world_bytes(service());

    }

    /**
     * Verifies that the {@link #service() service}'s {@link HelloWorld#write(OutputStream)
     * write(stream)} method was invoked exactly once, and returns the captured non-{@code null}
     * {@link OutputStream} argument.
     *
     * @return the captured {@link OutputStream} argument.
     * @throws IOException declared for stubbing convenience.
     * @see HelloWorld__TestUtils#write_stream_invoked_once(HelloWorld)
     */
    protected OutputStream write_stream_invoked_once() throws IOException {
        return HelloWorld__TestUtils.write_stream_invoked_once(service());

    }

    /**
     * Stubs the {@link HelloWorld#write(Writer)} method of the {@link #service() service} to write
     * an array {@value HelloWorld#BYTES} {@code char}s.
     *
     * @return the {@link #service() service}.
     * @throws IOException declared for stubbing convenience.
     * @see HelloWorld__TestUtils#write_writer_writes_12_chars(HelloWorld)
     */
    protected HelloWorld write_writer_writes_12_chars() throws IOException {
        return HelloWorld__TestUtils.write_writer_writes_12_chars(service());

    }

    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#write(Writer) write(writer)} method
     * to write the actual {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} characters (via
     * {@link #hello_world_char_array()}) to the writer and return it.
     *
     * @return the {@link #service() service}.
     * @throws IOException declared for stubbing convenience.
     * @see HelloWorld__TestUtils#write_writer_writes_hello_world_string(HelloWorld)
     */
    protected HelloWorld write_writer_writes_hello_world_string() throws IOException {
        return HelloWorld__TestUtils.write_writer_writes_hello_world_string(service());

    }

    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#append(DatagramPacket)
     * append(packet)} method to increase the packet's {@linkplain DatagramPacket#getLength()
     * length} by {@value HelloWorld#BYTES} and return the packet.
     *
     * @return the {@link #service() service}.
     * @see HelloWorld__TestUtils#append_packet_increases_packet_length_by_12(HelloWorld)
     */
    protected HelloWorld append_packet_increases_packet_length_by_12() {
        return HelloWorld__TestUtils.append_packet_increases_packet_length_by_12(service());

    }

    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#append(DatagramPacket)
     * append(packet)} method to copy the actual {@value
     * HelloWorld__TestConstants#HELLO_WORLD_STRING} bytes into the packet's data buffer (starting
     * at {@linkplain DatagramPacket#getOffset() offset}), increase the packet's {@linkplain
     * DatagramPacket#getLength() length} by {@value HelloWorld#BYTES}, and return the packet.
     *
     * @return the {@link #service() service}.
     * @see HelloWorld__TestUtils#append_packet_appends_hello_world_bytes(HelloWorld)
     */
    protected HelloWorld append_packet_appends_hello_world_bytes() {
        return HelloWorld__TestUtils.append_packet_appends_hello_world_bytes(service());

    }

    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#send(Socket) send(socket)} method to
     * write the actual {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} bytes to the socket's
     * {@link Socket#getOutputStream() output stream} and return the socket.
     *
     * @return the {@link #service() service}.
     * @throws IOException declared for stubbing convenience.
     * @see HelloWorld__TestUtils#send_socket_sends_hello_world_bytes(HelloWorld)
     */
    protected HelloWorld send_socket_sends_hello_world_bytes() throws IOException {
        return HelloWorld__TestUtils.send_socket_sends_hello_world_bytes(service());

    }

    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#append(Appendable)
     * append(appendable)} method to append the actual {@value
     * HelloWorld__TestConstants#HELLO_WORLD_STRING} characters to the {@link Appendable}, and
     * return it.
     *
     * @return the {@link #service() service}.
     * @throws IOException declared for stubbing convenience.
     * @see HelloWorld__TestUtils#send_datagramsocket_sends_hello_world_packet(HelloWorld)
     */
    protected HelloWorld send_datagramsocket_sends_hello_world_packet() throws IOException {
        return HelloWorld__TestUtils.send_datagramsocket_sends_hello_world_packet(service());

    }

    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#append(Appendable)
     * append(appendable)} method to append the actual {@value
     * HelloWorld__TestConstants#HELLO_WORLD_STRING} characters to the {@link Appendable}, and
     * return it.
     *
     * @return the {@link #service() service}.
     * @throws IOException declared for stubbing convenience.
     * @see
     * HelloWorld__TestUtils#send_datagramsocket_socketaddress_sends_hello_world_packet(HelloWorld)
     */
    protected HelloWorld send_datagramsocket_socketaddress_sends_hello_world_packet()
            throws IOException {
        return HelloWorld__TestUtils
                .send_datagramsocket_socketaddress_sends_hello_world_packet(service());

    }

    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#put(ByteBuffer) put(buffer)} method,
     * when the {@code buffer} is not {@code null} and has remaining greater than or equal to
     * {@value HelloWorld#BYTES}, to just return the {@code buffer} whose {@link
     * ByteBuffer#position() position} increased by {@value HelloWorld#BYTES}.
     *
     * @return the {@link #service() service}.
     * @see HelloWorld__TestUtils#put_buffer12_increases_buffer_position_by_12(HelloWorld)
     */
    protected HelloWorld put_buffer12_increases_buffer_position_by_12() {
        return HelloWorld__TestUtils.put_buffer12_increases_buffer_position_by_12(service());

    }

    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#put(ByteBuffer) put(buffer)} method
     * to put {@value HelloWorld#BYTES} random bytes with a no-op consumer — use when the random
     * bytes themselves are not needed for later assertion.
     *
     * @return the {@link #service() service}.
     * @see HelloWorld__TestUtils#put_buffer12_put_random_bytes(HelloWorld)
     */
    protected HelloWorld put_buffer12_put_random_bytes() {
        return HelloWorld__TestUtils.put_buffer12_put_random_bytes(service());

    }

    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#put(ByteBuffer) put(buffer)} method,
     * when the buffer is non-{@code null} and has at least {@value HelloWorld#BYTES} remaining, to
     * put the actual {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} bytes into the buffer
     * and return it.
     *
     * @return the {@link #service() service}.
     * @see HelloWorld__TestUtils#put_buffer_put_actual_hello_world_bytes(HelloWorld)
     */
    protected HelloWorld put_buffer_put_actual_hello_world_bytes() {
        return HelloWorld__TestUtils.put_buffer_put_actual_hello_world_bytes(service());

    }

    /**
     * Verifies that the {@link #service() service}'s {@link HelloWorld#put(ByteBuffer) put(buffer)}
     * method was invoked exactly once with a non-{@code null} {@link ByteBuffer} of capacity
     * {@value HelloWorld#BYTES}, and returns the captured buffer for further assertion.
     *
     * @return the captured {@link ByteBuffer} argument.
     * @see HelloWorld__TestUtils#put_buffer12_invoked_once(HelloWorld)
     */
    protected ByteBuffer put_buffer12_invoked_once() {
        return HelloWorld__TestUtils.put_buffer12_invoked_once(service());

    }

    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#append(Appendable)
     * append(appendable)} method to append exactly {@value HelloWorld#BYTES} {@code '0'} characters
     * to the {@link Appendable}, and return it.
     *
     * @return the {@link #service() service}.
     * @throws IOException declared for stubbing convenience.
     * @see HelloWorld__TestUtils#append_appendable_appends_12_chars(HelloWorld)
     */
    protected HelloWorld append_appendable_appends_12_chars() throws IOException {
        return HelloWorld__TestUtils.append_appendable_appends_12_chars(service());

    }

    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#append(Appendable)
     * append(appendable)} method to append the actual {@value
     * HelloWorld__TestConstants#HELLO_WORLD_STRING} characters to the {@link Appendable}, and
     * return it.
     *
     * @return the {@link #service() service}.
     * @throws IOException declared for stubbing convenience.
     * @see HelloWorld__TestUtils#append_appendable_appends_hello_world_bytes(HelloWorld)
     */
    protected HelloWorld append_appendable_appends_hello_world_bytes() throws IOException {
        return HelloWorld__TestUtils.append_appendable_appends_hello_world_bytes(service());

    }

    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#write(WritableByteChannel)
     * write(channel)} method to just return the channel (no bytes written).
     *
     * @return the {@link #service() service}.
     * @throws IOException declared for stubbing convenience.
     * @see HelloWorld__TestUtils#write_writablebytechannel_returns_channel(HelloWorld)
     */
    protected HelloWorld write_writablebytechannel_returns_channel() throws IOException {
        return HelloWorld__TestUtils.write_writablebytechannel_returns_channel(service());

    }

    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#write(WritableByteChannel)} method
     * to write actual 'hello, world' bytes.
     *
     * @return the {@link #service() service}.
     * @throws IOException declared for stubbing convenience.
     * @see HelloWorld__TestUtils#write_writablebytechannel_writes_hello_world_buffer(HelloWorld)
     */
    protected HelloWorld write_writablebytechannel_writes_hello_world_buffer() throws IOException {
        return HelloWorld__TestUtils.write_writablebytechannel_writes_hello_world_buffer(service());

    }

    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#send(DatagramChannel, SocketAddress)
     * send(channel, target)} method to loop {@link DatagramChannel#send(ByteBuffer, SocketAddress)
     * channel.send(buffer, target)} until a non-zero count is returned (i.e., the {@value
     * HelloWorld#BYTES}-byte datagram is actually accepted by the OS), and then return the channel.
     *
     * @return the {@link #service() service}.
     * @throws IOException declared for stubbing convenience.
     * @see HelloWorld__TestUtils#send_channel_target_sends_hello_world_buffer(HelloWorld)
     */
    protected HelloWorld send_channel_target_sends_hello_world_buffer() throws IOException {
        return HelloWorld__TestUtils.send_channel_target_sends_hello_world_buffer(service());

    }

    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#send(DatagramChannel) send(channel)}
     * method, when the channel is non-{@code null} and {@linkplain DatagramChannel#isConnected()
     * connected}, to write the actual {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} bytes
     * in a write-until-drained loop, and return the channel.
     *
     * @return the {@link #service() service}.
     * @throws IOException declared for stubbing convenience.
     * @see HelloWorld__TestUtils#send_datagramchannel_writes_hello_world_buffer(HelloWorld)
     */
    protected HelloWorld send_datagramchannel_writes_hello_world_buffer() throws IOException {
        return HelloWorld__TestUtils.send_datagramchannel_writes_hello_world_buffer(service());

    }

    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#write(AsynchronousByteChannel)
     * write(channel)} method to {@linkplain AsynchronousByteChannel#write(ByteBuffer)
     * channel.write(buffer)} until the {@value HelloWorld#BYTES}-byte source is fully drained,
     * blocking on each {@link java.util.concurrent.Future#get() Future.get()}.
     *
     * @return the {@link #service() service}.
     * @throws ExecutionException   declared for stubbing convenience.
     * @throws InterruptedException declared for stubbing convenience.
     * @see HelloWorld__TestUtils#write_asynchornousbytechannel_writes_hello_world(HelloWorld)
     */
    protected HelloWorld write_asynchornousbytechannel_writes_hello_world()
            throws ExecutionException, InterruptedException {
        return HelloWorld__TestUtils.write_asynchornousbytechannel_writes_hello_world(service());

    }

    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#write(AsynchronousFileChannel, long)
     * write(channel, position)} method to write the actual {@value
     * HelloWorld__TestConstants#HELLO_WORLD_STRING} bytes starting at the given file {@code
     * position}, advancing {@code position} by each {@link java.util.concurrent.Future#get()
     * Future.get()} result until the source is drained, and return the channel.
     *
     * @return the {@link #service() service}.
     * @throws ExecutionException   declared for stubbing convenience.
     * @throws InterruptedException declared for stubbing convenience.
     * @see HelloWorld__TestUtils#write_asynchronousfilechannel_long_writes_hello_world(HelloWorld)
     */
    protected HelloWorld write_asynchronousfilechannel_long_writes_hello_world()
            throws ExecutionException, InterruptedException {
        return HelloWorld__TestUtils
                .write_asynchronousfilechannel_long_writes_hello_world(service());

    }

    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#append(Path) append(path)} method to
     * append the actual {@value HelloWorld__TestConstants#HELLO_WORLD_STRING} bytes to the given
     * {@link Path}, opening a {@link FileChannel} with {@link StandardOpenOption#CREATE
     * CREATE}{@code +}{@link StandardOpenOption#APPEND APPEND}, forcing to disk on close, and
     * returning the path.
     *
     * @return the {@link #service() service}.
     * @throws IOException declared for stubbing convenience.
     * @see HelloWorld__TestUtils#append_path_appends_hello_world(HelloWorld)
     */
    protected HelloWorld append_path_appends_hello_world() throws IOException {
        return HelloWorld__TestUtils.append_path_appends_hello_world(service());

    }

    /**
     * Stubs the {@link #service() service}'s {@link HelloWorld#update(Signature) update(signature)}
     * method to call {@link Signature#update(byte[]) signature.update(...)} with the actual {@value
     * HelloWorld__TestConstants#HELLO_WORLD_STRING} bytes and return the signature.
     *
     * @return the {@link #service() service}.
     * @throws SignatureException declared for stubbing convenience.
     * @see HelloWorld__TestUtils#update_signature_updates_hello_world_bytes(HelloWorld)
     */
    protected HelloWorld update_signature_updates_hello_world_bytes() throws SignatureException {
        return HelloWorld__TestUtils.update_signature_updates_hello_world_bytes(service());

    }

    // ------------------------------------------------------------------------------------- service

    // ---------------------------------------------------------------------------------------------
    @Spy
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private HelloWorld service;
}
