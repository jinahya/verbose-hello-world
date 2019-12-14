package com.github.jinahya.hello;

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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static com.github.jinahya.hello.ValidationProxy.newValidationProxy;
import static java.nio.ByteBuffer.allocate;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;

/**
 * A class for unit-testing {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@MockitoSettings(strictness = LENIENT)
@ExtendWith({MockitoExtension.class})
@Slf4j
public class HelloWorldTest {

    // ----------------------------------------------------------------------------------------------------------- BYTES

    /**
     * Asserts the value of {@link HelloWorld#BYTES} constant equals to the actual number of bytes of "{@code hello,
     * world}" string encoded in {@code US-ASCII} character set.
     *
     * @see String#getBytes(Charset)
     * @see StandardCharsets#US_ASCII
     */
    @DisplayName("BYTES equals to the actual number of \"hello, world\" bytes")
    @Test
    void assertHelloWorldBytesEqualsToActualNumberOfHelloWorldBytes() {
        final int expected = "hello, world".getBytes(US_ASCII).length;
        assertEquals(expected, BYTES);
    }

    // ------------------------------------------------------------------------------------------------------- set([B)[B

    /**
     * Asserts {@link HelloWorld#set(byte[])} method throws a {@link NullPointerException} when the {@code array}
     * argument is {@code null}.
     */
    @DisplayName("set(array) throws NullPointerException when array is null")
    @Test
    public void assertSetArrayThrowsNullPointerExceptionWhenArrayIsNull() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#set(byte[])} method throws an {@link IndexOutOfBoundsException} when {@code
     * array.length} is less than {@link HelloWorld#BYTES}.
     */
    @DisplayName("set(array) throws IndexOutOfBoundsException when array.length is less than BYTES")
    @Test
    public void assertSetArrayThrowsIndexOufOfBoundsExceptionWhenArrayLengthIsLessThanHelloWorldBytes() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#set(byte[])} method invokes {@link HelloWorld#set(byte[], int)} method with specified
     * {@code array} and {@code 0}.
     */
    @DisplayName("set(array) invokes set(array, 0)")
    @Test
    public void assertSetArrayInvokesSetArrayIndex() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#set(byte[])} method returns specified {@code array} argument.
     */
    @DisplayName("set(array) returns array")
    @Test
    public void assertSetArrayReturnsArray() {
        // TODO: implement!
    }

    // ------------------------------------------------------------- write(Ljava.io.OutputStream;)Ljava.io.OutputStream;

    /**
     * Asserts {@link HelloWorld#write(OutputStream)} method throws a {@link NullPointerException} when {@code stream}
     * argument is {@code null}.
     */
    @DisplayName("write(stream) throws NullPointerException when stream is null")
    @Test
    public void assertWriteStreamThrowsNullPointerExceptionWhenStreamIsNull() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(OutputStream)} method invokes {@link HelloWorld#set(byte[])} method and writes
     * the returned array to specified output stream.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(stream) invokes set(array) and writes the returned array to the stream")
    @Test
    public void assertWriteStreamInvokesSetArrayAndWritesTheResultToStream() throws IOException {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(OutputStream)} method returns specified output stream.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(stream) returns the stream")
    @Test
    public void assertWriteStreamReturnsStream() throws IOException {
        // TODO: implement!
    }

    // ----------------------------------------------------------------------------- write(Ljava.io.File;)Ljava.io.File;

    /**
     * Asserts {@link HelloWorld#append(File)} method throws a {@link NullPointerException} when {@code file} argument
     * is {@code null}.
     */
    @DisplayName("append(file) throws NullPointerException when file is null")
    @Test
    public void assertAppendFileThrowsNullPointerExceptionWhenFileIsNull() {
        assertThrows(NullPointerException.class, () -> helloWorld.append((File) null));
    }

    /**
     * Asserts {@link HelloWorld#append(File)} method invokes {@link HelloWorld#write(OutputStream)} method.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("append(file) invokes write(stream)")
    @Test
    public void assertAppendFileInvokesWriteStream(@TempDir final File tempDir) throws IOException {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#append(File)} method returns specified file.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("append(file) returns specified file")
    @Test
    public void assertAppendFileReturnsFile(@TempDir final File tempDir) throws IOException {
        // TODO: implement!
    }

    // ------------------------------------------------------------------------ send(Ljava.net.Socket;)Ljava.net.Socket;

    /**
     * Asserts {@link HelloWorld#send(Socket)} method throws a {@link NullPointerException} when the {@code socket}
     * argument is {@code null}.
     */
    @DisplayName("send(socket) throws NullPointerException when socket is null")
    @Test
    public void assertSendSocketThrowsNullPointerExceptionWhenSocketIsNull() {
        assertThrows(NullPointerException.class, () -> helloWorld.send((Socket) null));
    }

    /**
     * Asserts {@link HelloWorld#send(Socket)} method invokes the {@link HelloWorld#write(OutputStream)} method with
     * specified socket's {@link Socket#getOutputStream() outputStream}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("send(socket) invokes write(socket.outputStream)")
    @Test
    public void assertSendSocketInvokesWriteStreamWithSocketOutputStream() throws IOException {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#send(Socket)} method returns the specified socket.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("send(socket) returns socket")
    @Test
    public void assertSendSocketReturnsSocket() throws IOException {
        // TODO: implement!
    }

    // ----------------------------------------------------------------- write(Ljava.io.DataOutput;)Ljava.io.DataOutput;

    /**
     * Asserts {@link HelloWorld#write(DataOutput)} method throws a {@link NullPointerException} when {@code data}
     * argument is {@code null}.
     */
    @DisplayName("write(data) method throws NullPointerException when data is null")
    @Test
    public void assertWriteDataThrowsNullPointerExceptionWhenDataIsNull() {
        assertThrows(NullPointerException.class, () -> helloWorld.write((DataOutput) null));
    }

    /**
     * Asserts {@link HelloWorld#write(DataOutput)} method invokes {@link HelloWorld#set(byte[])} method with an array
     * of {@link HelloWorld#BYTES} bytes and writes the returned array to specified data output.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(data) invokes set(array) and writes the result to data")
    @Test
    public void assertWriteDataInvokesSetArrayAndWritesTheArrayToData() throws IOException {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(DataOutput)} method returns the specified {@code data} argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(data) returns specified data")
    @Test
    public void assertWriteDataReturnsData() throws IOException {
        // TODO: implement!
    }

    // ----------------------------------------------------- write(Ljava.io.RandomAccessFile;)Ljava.io.RandomAccessFile;

    /**
     * Asserts {@link HelloWorld#write(RandomAccessFile)} method throws a {@link NullPointerException} when {@code file}
     * argument is {@code null}.
     */
    @DisplayName("write(file) throws NullPointerException when file is null")
    @Test
    public void assertWriteFileThrowsNullPointerExceptionWhenFileIsNull() {
        assertThrows(NullPointerException.class, () -> helloWorld.write((RandomAccessFile) null));
    }

    /**
     * Asserts {@link HelloWorld#write(RandomAccessFile)} invokes {@link HelloWorld#set(byte[])} method and writes the
     * returned array to specified {@code file}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(file) invokes set(array) method and writes the result to file")
    @Test
    public void assertWriteFileInvokesSetArrayAndWritesTheResultToFile() throws IOException {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(RandomAccessFile)} method returns the specified {@code file}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(file) returns specified file")
    @Test
    public void assertWriteRandomAccessFileReturnsFile() throws IOException {
        // TODO: implement!
    }

    // ----------------------------------------------------------------- put(Ljava.nio.ByteBuffer;)Ljava.nio.ByteBuffer;

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method throws a {@link NullPointerException} when {@code buffer}
     * argument is {@code null}.
     */
    @DisplayName("put(buffer) throws NullPointerException when buffer is null")
    @Test
    public void assertPutBufferThrowsNullPointerExceptionWhenBufferIsNull() {
        assertThrows(NullPointerException.class, () -> helloWorld.put(null));
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method throws a {@link BufferOverflowException} when {@code buffer}
     * argument's {@link ByteBuffer#remaining() remaining} is less than {@link HelloWorld#BYTES}.
     */
    @DisplayName("put(buffer) throws BufferOverflowException when buffer.remaining is less than BYTES")
    @Test
    public void assertPutBufferThrowsBufferOverflowExceptionWhenBufferRemainingIsLessThanHelloWorldBytes() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method, when the {@code buffer} {@link ByteBuffer#hasArray() has a
     * backing array}, invokes {@link HelloWorld#set(byte[], int)} method with {@link ByteBuffer#array() buffer.array}
     * and ({@link ByteBuffer#arrayOffset() buffer.arrayOffset} + {@link ByteBuffer#position() position}) and increments
     * the {@link ByteBuffer#position(int) position} by {@link HelloWorld#BYTES}.
     */
    @DisplayName("put(buffer with backing array) invokes set(buffer.array, buffer.arrayOffset + buffer.position)")
    @Test
    public void assertPutBufferInvokesSetArrayIndexWhenBufferHasBackingArray() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method, when the {@code buffer} doesn't have a backing array, invokes
     * {@link HelloWorld#set(byte[])} method and puts the result to the buffer.
     */
    @DisplayName("put(buffer with no backing array) invokes set(array) and put the result to the buffer")
    @Test
    public void assertPutBufferInvokesSetWhenBufferHasNoBackingArray() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method returns specified {@code buffer} argument when the {@code
     * buffer} argument has a backing array.
     */
    @DisplayName("put(buffer with backing array) returns specified buffer")
    @Test
    public void assertPutBufferReturnsBufferHasBackingArray() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method returns specified {@code buffer} argument when the {@code
     * buffer} argument has no backing array.
     */
    @DisplayName("put(buffer with no backing array) returns specified buffer")
    @Test
    public void assertPutBufferReturnsBufferHasNoBackingArray() {
        // TODO: implement!
    }

    // --------------------------- write(Ljava.nio.channels.WritableByteChannel;)Ljava.nio.channels.WritableByteChannel;

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel)} method throws a {@link NullPointerException} when {@code
     * channel} argument is {@code null}.
     */
    @DisplayName("write(channel) throws NullPointerException when channel is null")
    @Test
    public void assertWriteChannelThrowsNullPointerExceptionWhenChannelIsNull() {
        assertThrows(NullPointerException.class, () -> helloWorld.write((WritableByteChannel) null));
    }

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel)} method invokes {@link HelloWorld#put(ByteBuffer)} method
     * with a byte buffer of {@link HelloWorld#BYTES} bytes and writes the buffer to specified {@code channel}
     * argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(channel) invokes put(buffer) and writes the buffer to the channel")
    @Test
    public void assertWriteChannelInvokesPutAndWritesTheResultToChannel() throws IOException {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel)} method returns specified {@code channel} argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(channel) returns the channel")
    @Test
    public void assertWriteChannelReturnsChannel() throws IOException {
        // TODO: implement!!
    }

    // ----------------------------------------------------------------- write(Ljava.nio.file.Path;)Ljava.nio.file.Path;

    /**
     * Asserts {@link HelloWorld#append(Path)} method throws a {@link NullPointerException} when specified {@code path}
     * is {@code null}.
     */
    @DisplayName("append(path) throws NullPointerException when path is null")
    @Test
    public void assertAppendPathThrowsNullPointerExceptionWhenPathIsNull() {
        assertThrows(NullPointerException.class, () -> helloWorld.append((Path) null));
    }

    /**
     * Asserts {@link HelloWorld#append(Path)} method invokes {@link HelloWorld#write(WritableByteChannel)} method and
     * asserts as many bytes as {@link HelloWorld#BYTES} are written to the {@code path}.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("append(path) invokes write(channel)")
    @Test
    public void assertAppendPathInvokesWriteChannel(@TempDir final Path tempDir) throws IOException {
        // TODO: implement!!
    }

    /**
     * Asserts {@link HelloWorld#append(Path)} method returns specified {@code path} argument.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(path) returns path")
    @Test
    public void assertAppendPathReturnsPath(@TempDir final Path tempDir) throws IOException {
        // TODO: implement!!
    }

    // ---------------------------------------- send(Ljava.nio.channels.SocketChannel;)Ljava.nio.channels.SocketChannel;
    @Deprecated
    @Test
    void testSendSocketChannel() throws IOException {
        final SocketChannel expected = mock(SocketChannel.class);
        when(expected.write(any(ByteBuffer.class))).then(i -> {
            final ByteBuffer buffer = i.getArgument(0);
            final int remaining = buffer.remaining();
            buffer.position(buffer.position() + remaining); // drain all available bytes
            return remaining;
        });
        when(helloWorld.write(expected)).thenAnswer(i -> {
            final SocketChannel channel = i.getArgument(0, SocketChannel.class);
            channel.write(allocate(BYTES));
            return channel;
        });
        final SocketChannel actual = helloWorld.send(expected);
        assertEquals(expected, actual);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns a proxy of {@link #helloWorld} whose method arguments and result are validated.
     *
     * @return a proxy of {@link #helloWorld}.
     */
    HelloWorld helloWorld() {
        return newValidationProxy(HelloWorld.class, helloWorld);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Captures the sole argument of {@link HelloWorld#set(byte[])} method.
     *
     * @return the captured value of the argument.
     */
    byte[] capturedArray() {
        final ArgumentCaptor<byte[]> captor = forClass(byte[].class);
        verify(helloWorld).set(captor.capture());
        return captor.getValue();
    }

    /**
     * Captures the sole argument of {@link HelloWorld#put(ByteBuffer)} method.
     *
     * @return the captured value of the argument.
     */
    ByteBuffer capturedBuffer() {
        final ArgumentCaptor<ByteBuffer> captor = forClass(ByteBuffer.class);
        verify(helloWorld).put(captor.capture());
        return captor.getValue();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Intercepts the result of {@link HelloWorld#set(byte[])} method.
     */
    @BeforeEach
    private void interceptTheResultOfSetArray() {
        doAnswer(arrayPeeper).when(helloWorld).set(any(byte[].class));
    }

    /**
     * Intercepts the result of {@link HelloWorld#put(ByteBuffer)} method.
     */
    @BeforeEach
    private void interceptTheResultOfPutBuffer() {
        doAnswer(bufferPeeper).when(helloWorld).put(any(ByteBuffer.class));
    }

    /**
     * Stubs {@link HelloWorld#set(byte[], int)} method of {@link Spy spied} {@code helloWorld} instance to return
     * specified {@code array}.
     */
    @BeforeEach
    private void stubSetArrayIndexToReturnSpecifiedArray() {
        when(helloWorld.set(any(byte[].class), anyInt())) // <1>
                .thenAnswer(i -> i.getArgument(0));       // <2>
    }

    // -----------------------------------------------------------------------------------------------------------------
    final ResultInterceptor<byte[]> arrayPeeper = new ResultInterceptor<>();

    final ResultInterceptor<ByteBuffer> bufferPeeper = new ResultInterceptor<>();

    @Spy
    HelloWorld helloWorld;
}
