package com.github.jinahya.hello;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;

import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static java.lang.invoke.MethodHandles.lookup;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * A class for testing {@link HelloWorld} class.
 */
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class})
public class HelloWorldTest {

    private static final Logger logger = getLogger(lookup().lookupClass());

    /**
     * Asserts the value of {@link HelloWorld#SIZE} constant equals to the length of {@code hello, world} string in form
     * of bytes encoded with {@code US-ASCII} character set.
     *
     * @see String#getBytes(Charset)
     * @see StandardCharsets#US_ASCII
     */
    @Test
    void assertSizeEqualsToHelloWorldBytes() {
        assertEquals("hello, world".getBytes(StandardCharsets.US_ASCII).length, HelloWorld.SIZE);
    }

    // ----------------------------------------------------------------------------------------------------- set(byte[])

    /**
     * Asserts {@link HelloWorld#set(byte[])} method throws a {@link NullPointerException} when {@code array} argument
     * is {@code null}.
     */
    @Test
    public void assertSetArrayThrowsNullPointerExceptionWhenArrayIsNull() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#set(byte[])} method throws an {@link IndexOutOfBoundsException} when {@code
     * array.length} is less than {@link HelloWorld#SIZE}.
     */
    @Test
    public void assertSetArrayThrowsIndexOufOfBoundsExceptionWhenArrayLengthIsLessThanHelloWorldSize() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#set(byte[])} method returns specified byte array.
     */
    @Test
    public void assertSetArrayReturnsSpecifiedArray() {
        // TODO: implement!
    }

    // ----------------------------------------------------------------------------------------------- write(DataOutput)

    /**
     * Asserts {@link HelloWorld#write(DataOutput)} method throws a {@link NullPointerException} when {@code data}
     * argument is {@code null}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteDataThrowsNullPointerExceptionWhenDataIsNull() throws IOException {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(DataOutput)} method writes as many bytes as {@value HelloWorld#SIZE} to specified
     * data output.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteDataWritesAsManyBytesAsHelloWorldSizeToData() throws IOException {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(DataOutput)} method returns the specified data output.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteDataReturnsSpecifiedData() throws IOException {
        // TODO: implement!
    }

    // ----------------------------------------------------------------------------------------- write(RandomAccessFile)

    /**
     * Asserts {@link HelloWorld#write(RandomAccessFile)} method throws a {@link NullPointerException} when {@code file}
     * argument is {@code null}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteRandomAccessFileThrowsNullPointerExceptionWhenFileIsNull() throws IOException {
        assertThrows(NullPointerException.class, () -> helloWorld.write((RandomAccessFile) null));
    }

    /**
     * Asserts {@link HelloWorld#write(RandomAccessFile)} method writes as many bytes as {@link HelloWorld#SIZE} to
     * specified random access file.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteRandomAccessFileWritesAsManyBytesAsHelloWorldSizeToFile() throws IOException {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(RandomAccessFile)} method returns the specified random access file.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteRandomAccessFileReturnsSpecifiedFile() throws IOException {
        // TODO: implement!
    }

    // --------------------------------------------------------------------------------------------- write(OutputStream)

    /**
     * Asserts {@link HelloWorld#write(OutputStream)} method throws a {@link NullPointerException} when {@code stream}
     * argument is {@code null}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteStreamThrowsNullPointerExceptionWhenStreamIsNull() throws IOException {
        assertThrows(NullPointerException.class, () -> helloWorld.write((OutputStream) null));
    }

    /**
     * Asserts {@link HelloWorld#write(OutputStream)} method writes as many bytes as {@value HelloWorld#SIZE} to
     * specified stream.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteStreamWritesAsManyBytesAsHelloWorldSizeToStream() throws IOException {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(OutputStream)} method returns specified output stream.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteStreamReturnsSpecifiedStream() throws IOException {
        // TODO: implement!
    }

    // ----------------------------------------------------------------------------------------------------- write(File)

    /**
     * Asserts {@link HelloWorld#write(File)} method throws a {@link NullPointerException} when {@code file} argument is
     * {@code null}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteFileThrowsNullPointerExceptionWhenFileIsNull() throws IOException {
        assertThrows(NullPointerException.class, () -> helloWorld.write((File) null));
    }

    /**
     * Asserts {@link HelloWorld#write(File)} method writes as many bytes as {@link HelloWorld#SIZE} to specified file.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void assertWriteFileWritesAsManyBytesAsHelloWorldSizeToFile() throws IOException {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(File)} method returns the specified file.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void assertWriteFileReturnsSpecifiedFile() throws IOException {
        // TODO: implement!
    }

    // ---------------------------------------------------------------------------------------------------- send(Socket)

    /**
     * Asserts {@link HelloWorld#send(Socket)} method throws a {@link NullPointerException} when the {@code socket}
     * argument is {@code null}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertSendSocketThrowsNullPointerExceptionWhenSocketIsNull() throws IOException {
        assertThrows(NullPointerException.class, () -> helloWorld.send((Socket) null));
    }

    /**
     * Asserts {@link HelloWorld#send(Socket)} method sends as many bytes as {@link HelloWorld#SIZE} to specified
     * socket.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertSendSocketSendsAsManyBytesAsHelloWorldSizeToSocket() throws IOException {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#send(Socket)} method returns the specified socket.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertSendSocketReturnsSpecifiedSocket() throws IOException {
        // TODO: implement!
    }

    // ------------------------------------------------------------------------------------------------- put(ByteBuffer)

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method throws a {@link NullPointerException} when {@code buffer}
     * argument is {@code null}.
     */
    @Test
    public void assertPutBufferThrowsNullPointerExceptionWhenBufferIsNull() {
        assertThrows(NullPointerException.class, () -> helloWorld.put(null));
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method throws an {@link IllegalArgumentException} if {@link
     * ByteBuffer#remaining() buffer.remaining()} is less than {@link HelloWorld#SIZE}.
     */
    @Disabled
    @Test
    public void assertPutBufferThrowsIllegalArgumentExceptionWhenBufferRemainingIsLessThanHelloWorldSize() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method throws an {@link java.nio.BufferOverflowException} if {@link
     * ByteBuffer#remaining() buffer.remaining()} is less than {@link HelloWorld#SIZE}.
     */
    @Test
    public void assertPutBufferThrowsBufferOverflowExceptionWhenBufferRemainingIsLessThanHelloWorldSize() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method puts as many bytes as {@link HelloWorld#SIZE} to specified byte
     * buffer. This method aims to test with a byte buffer which has a backing-array.
     */
    @Test
    public void assertPutBufferPutsAsManyBytesAsHelloWorldSizeToBufferBackingArray() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method puts as many bytes as {@link HelloWorld#SIZE} to specified byte
     * buffer. This method aims to test with a byte buffer which doesn't have a backing-array.
     */
    @Test
    public void assertPutBufferPutsAsManyBytesAsHelloWorldToBuffer() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method returns specified byte buffer.
     */
    @Test
    public void assertPutBufferReturnsSpecifiedBufferBackingArray() {
        // TODO: implement!
    }

    // -------------------------------------------------------------------------------------- write(WritableByteChannel)

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel)} method throws a {@link NullPointerException} when {@code
     * channel} argument is {@code null}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteChannelThrowsNullPointerExceptionWhenChannelIsNull() throws IOException {
        assertThrows(NullPointerException.class, () -> helloWorld.write((WritableByteChannel) null));
    }

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel)} method writes as many bytes as {@link HelloWorld#SIZE} to
     * specified channel.
     *
     * @throws IOException if an I/O error occurs.
     * @see HelloWorld#write(WritableByteChannel)
     */
    @Test
    public void assertWriteChannelWritesAsManyBytesAsHelloWorldSizeToChannel() throws IOException {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel)} method returns the specified channel.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteChannelReturnsSpecifiedChannel() throws IOException {
        // TODO: implement!!
    }

    // ----------------------------------------------------------------------------------------------------- write(Path)

    /**
     * Asserts {@link HelloWorld#write(Path)} method throws a {@link NullPointerException} when specified {@code path}
     * argument is {@code null}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWritePathThrowsNullPointerExceptionWhenPathIsNull() throws IOException {
        assertThrows(NullPointerException.class, () -> helloWorld.write((Path) null));
    }

    /**
     * Asserts {@link HelloWorld#write(Path)} method writes as many bytes as {@link HelloWorld#SIZE} to specified path.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWritePathWritesAsManyBytesAsHelloWorldSizeToPath() throws IOException {
        // TODO: implement!!
    }

    /**
     * Asserts {@link HelloWorld#write(Path)} method returns the specified path.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWritePathReturnsSpecifiedPath() throws IOException {
        // TODO: implement!!
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Stubs {@link HelloWorld#set(byte[], int)} method of {@link #helloWorld} to return specified {@code array}
     * argument.
     */
    @BeforeEach
    private void stubSetArrayWithIndexToReturnSpecifiedArray() {
        when(helloWorld.set(any(byte[].class), anyInt())).thenAnswer(i -> i.getArgument(0));
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Spy
    private HelloWorld helloWorld;
}
