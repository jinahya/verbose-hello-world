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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
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
     * world}" string encoded in {@link StandardCharsets#US_ASCII US-ASCII} character set.
     *
     * @see String#getBytes(Charset)
     * @see StandardCharsets#US_ASCII
     */
    @DisplayName("BYTES equals to the actual number of \"hello, world\" bytes")
    @Test
    void assertHelloWorldBytesEqualsToActualNumberOfHelloWorldBytes() {
        final int expected = "hello, world".getBytes(US_ASCII).length;
        Assertions.assertEquals(expected, BYTES);
    }

    // ------------------------------------------------------------------------------------------------------- set([B)[B

    /**
     * Asserts {@link HelloWorld#set(byte[])} method throws a {@link NullPointerException} when the {@code array}
     * argument is {@code null}.
     */
    @DisplayName("set(array) throws NullPointerException when array is null")
    @Test
    public void setArray_NullPointerException_ArrayIsNull() {
    }

    /**
     * Asserts {@link HelloWorld#set(byte[])} method throws an {@link IndexOutOfBoundsException} when {@code
     * array.length} is less than {@link HelloWorld#BYTES}.
     */
    @DisplayName("set(array) throws IndexOutOfBoundsException when array.length is less than BYTES")
    @Test
    public void setArray_IndexOutOfBoundsException_ArrayLengthIsLessThanBYTES() {
    }

    /**
     * Asserts {@link HelloWorld#set(byte[])} method invokes {@link HelloWorld#set(byte[], int)} method with specified
     * {@code array} and {@code 0} and returns the result.
     */
    @DisplayName("set(array) invokes set(array, 0)")
    @Test
    public void setArray_InvokesSetArrayWithArrayAndZero() {
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
     * Asserts {@link HelloWorld#write(OutputStream) write(stream)} method throws a {@link NullPointerException} when
     * {@code stream} argument is {@code null}.
     */
    @DisplayName("write(stream) throws NullPointerException when stream is null")
    @Test
    public void writeStream_NullPointerException_StreamIsNull() {
    }

    /**
     * Asserts {@link HelloWorld#write(OutputStream) write(stream)} method invokes {@link HelloWorld#set(byte[])
     * set(byte[])} method with an array of {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes and writes the
     * array to specified {@code stream}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(stream) invokes set(array) and writes the array to the stream")
    @Test
    public void writeStream_InvokeSetArrayAndWriteArrayToStream_() throws IOException {
    }

    /**
     * Asserts {@link HelloWorld#write(OutputStream) write(stream)} method returns specified {@code stream}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(stream) returns the stream")
    @Test
    public void writeStream_ReturnStream_() throws IOException {
    }

    // ----------------------------------------------------------------------------- write(Ljava.io.File;)Ljava.io.File;

    /**
     * Asserts {@link HelloWorld#append(File) append(file)} method throws a {@link NullPointerException} when {@code
     * file} argument is {@code null}.
     */
    @DisplayName("append(file) throws a NullPointerException when the file is null")
    @Test
    public void appendFile_NullPointerException_FileIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> helloWorld.append((File) null));
    }

    /**
     * Asserts {@link HelloWorld#append(File) append(file)} method invokes {@link HelloWorld#write(OutputStream)
     * write(stream)} method.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("append(file) invokes write(stream)")
    @Test
    public void appendFile_InvokeWriteStream_(final @TempDir File tempDir) throws IOException {
        final File file = java.io.File.createTempFile("tmp", null, tempDir);
    }

    /**
     * Asserts {@link HelloWorld#append(File) append(file)} method returns given file.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("append(file) returns given file")
    @Test
    public void appendFile_ReturnFile_(final @TempDir File tempDir) throws IOException {
        final File file = java.io.File.createTempFile("tmp", null, tempDir);
    }

    // ------------------------------------------------------------------------ send(Ljava.net.Socket;)Ljava.net.Socket;

    /**
     * Asserts {@link HelloWorld#send(Socket) send(socket)} method throws a {@link NullPointerException} when the {@code
     * socket} argument is {@code null}.
     */
    @DisplayName("send(socket) throws NullPointerException when socket is null")
    @Test
    public void sendSocket_NullPointerException_SocketIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> helloWorld.send((Socket) null));
    }

    /**
     * Asserts {@link HelloWorld#send(Socket)} method invokes the {@link HelloWorld#write(OutputStream)} method with
     * what specified socket's {@link Socket#getOutputStream() outputStream}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("send(socket) invokes write(socket.outputStream)")
    @Test
    public void sendSocket_InvokeWriteStreamWithSocketOutputStream_() throws IOException {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#send(Socket)} method returns the specified socket.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("send(socket) returns socket")
    @Test
    public void sendSocket_ReturnSocket_() throws IOException {
        // TODO: implement!
    }

    // ----------------------------------------------------------------- write(Ljava.io.DataOutput;)Ljava.io.DataOutput;

    /**
     * Asserts {@link HelloWorld#write(DataOutput)} method throws a {@link NullPointerException} when {@code data}
     * argument is {@code null}.
     */
    @DisplayName("write(data) method throws NullPointerException when data is null")
    @Test
    public void writeData_NullPointerException_DataIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> helloWorld.write((DataOutput) null));
    }

    /**
     * Asserts {@link HelloWorld#write(DataOutput)} method invokes {@link HelloWorld#set(byte[])} method with an array
     * of {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes and writes the array to specified data output.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(data) invokes set(array) and writes the array to data")
    @Test
    public void writeData_InvokeSetArrayWriteArrayToData_() throws IOException {
    }

    /**
     * Asserts {@link HelloWorld#write(DataOutput)} method returns given data output.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(data) returns data")
    @Test
    public void writeData_ReturnData_() throws IOException {
        // TODO: implement!
    }

    // ----------------------------------------------------- write(Ljava.io.RandomAccessFile;)Ljava.io.RandomAccessFile;

    /**
     * Asserts {@link HelloWorld#write(RandomAccessFile)} method throws a {@link NullPointerException} when {@code file}
     * argument is {@code null}.
     */
    @DisplayName("write(file) throws NullPointerException when file is null")
    @Test
    public void writeFile_NullPointerException_FileIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> helloWorld.write((RandomAccessFile) null));
    }

    /**
     * Asserts {@link HelloWorld#write(RandomAccessFile)} invokes {@link HelloWorld#set(byte[])} method with an array of
     * {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes and writes the array to specified random access file.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(file) invokes set(array) method and writes the array to file")
    @Test
    public void writeFile_InvokeSetArrayWriteArrayToFiled_() throws IOException {
    }

    /**
     * Asserts {@link HelloWorld#write(RandomAccessFile)} method returns specified {@code file}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(file) returns file")
    @Test
    void writeFile_ReturnFile_() throws IOException {
    }

    // ----------------------------------------------------------------- put(Ljava.nio.ByteBuffer;)Ljava.nio.ByteBuffer;

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method throws a {@link NullPointerException} when {@code buffer}
     * argument is {@code null}.
     */
    @DisplayName("put(buffer) throws NullPointerException when buffer is null")
    @Test
    public void putBuffer_NullPointerException_BufferIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> helloWorld.put(null));
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method throws a {@link BufferOverflowException} when {@link
     * ByteBuffer#remaining() buffer.remaining} is less than {@link HelloWorld#BYTES}({@value
     * com.github.jinahya.hello.HelloWorld#BYTES}).
     */
    @DisplayName("put(buffer) throws BufferOverflowException when buffer.remaining is less than BYTES")
    @Test
    public void putBuffer_BufferOverflowException_BufferRemainingIsNotEnough() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method, when invoked with a byte buffer {@link ByteBuffer#hasArray()
     * backed by an array}, invokes {@link HelloWorld#set(byte[], int) set(buffer.array, buffer.arrayOffset +
     * buffer.position)} and increments the {@link ByteBuffer#position(int) buffer.position} by {@value
     * com.github.jinahya.hello.HelloWorld#BYTES}.
     */
    @DisplayName("put(buffer-with-backing-array) invokes set(buffer.array, buffer.arrayOffset + buffer.position)")
    @Test
    public void putBufferWithBackingArray_InvokeSetArrayWithIndexAndIncrementPosition_() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method, when invoked with a byte buffer {@link ByteBuffer#hasArray()
     * not backed by any array}, invokes {@link HelloWorld#set(byte[]) set(byte[12])} method and puts the array to the
     * buffer.
     */
    @DisplayName("put(buffer-with-no-backing-array) invokes set(array) and put the array to the buffer")
    @Test
    public void putBufferWithNoBackingArray_InvokeSetArrayPutArrayToBuffer() {
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method returns given buffer when the buffer has a backing array.
     */
    @DisplayName("put(buffer-with-backing-array) returns buffer")
    @Test
    public void putBufferWithBackingArray_ReturnBuffer_() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method returns given buffer when the buffer has no backing array.
     */
    @DisplayName("put(buffer with no backing array) returns specified buffer")
    @Test
    public void putBufferWithNoBackingArray_ReturnBuffer_() {
        // TODO: implement!
    }

    // --------------------------- write(Ljava.nio.channels.WritableByteChannel;)Ljava.nio.channels.WritableByteChannel;

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel)} method throws a {@link NullPointerException} when {@code
     * channel} argument is {@code null}.
     */
    @DisplayName("write(channel) throws NullPointerException when channel is null")
    @Test
    public void writeChannel_NullPointerException_ChannelIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> helloWorld.write((WritableByteChannel) null));
    }

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel)} method invokes {@link HelloWorld#put(ByteBuffer)} method
     * with a byte buffer of {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes and writes the buffer to specified
     * channel.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(channel) invokes put(buffer) and writes the buffer to the channel")
    @Test
    public void writeChannel_InvokePutBufferWriteBufferToChannel_() throws IOException {
    }

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel)} method returns given channel.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("write(channel) returns channel")
    @Test
    public void writeChannel_ReturnChannel_() throws IOException {
    }

    // ----------------------------------------------------------------- write(Ljava.nio.file.Path;)Ljava.nio.file.Path;

    /**
     * Asserts {@link HelloWorld#append(Path) append(path)} method throws a {@link NullPointerException} when the {@code
     * path} argument is {@code null}.
     */
    @DisplayName("append(path) throws NullPointerException when path is null")
    @Test
    public void appendPath_NullPointerException_PathIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> helloWorld.append((Path) null));
    }

    /**
     * Asserts {@link HelloWorld#append(Path) append(path)} method invokes {@link HelloWorld#write(WritableByteChannel)}
     * method with a channel and asserts {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes are appended to the
     * {@code path}.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("append(path) invokes write(channel)")
    @Test
    public void appendPath_InvokeWriteChannel_(final @TempDir Path tempDir) throws IOException {
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
     * Intercepts the result of {@link HelloWorld#set(byte[])} method.
     */
    @BeforeEach
    private void interceptTheResultOfSetArray() {
        arrayCaptor.captured = false;
        doAnswer(arrayCaptor).when(helloWorld).set(any(byte[].class));
    }

    /**
     * Intercepts the result of {@link HelloWorld#put(ByteBuffer)} method.
     */
    @BeforeEach
    private void interceptTheResultOfPutBuffer() {
        bufferCaptor.captured = false;
        doAnswer(bufferCaptor).when(helloWorld).put(any(ByteBuffer.class));
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

    /**
     * A result captor for capturing the argument of {@link HelloWorld#set(byte[])} method.
     */
    final ResultCaptor<byte[]> arrayCaptor = new ResultCaptor<>();

    /**
     * A result captor for capturing the argument of {@link HelloWorld#put(ByteBuffer)} method.
     */
    final ResultCaptor<ByteBuffer> bufferCaptor = new ResultCaptor<>();

    /**
     * An injected spy of {@link HelloWorld} interface.
     */
    @Spy
    HelloWorld helloWorld;
}
