package com.github.jinahya.hello;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.invoke.MethodHandles;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static com.github.jinahya.hello.BufferParameterResolver.HasBackingArray;
import static com.github.jinahya.hello.ChannelParameterResolver.CountableByteChannel;
import static com.github.jinahya.hello.ChannelParameterResolver.NonBlocking;
import static com.github.jinahya.hello.FileParameterResolver.Temporary;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * A class for testing {@link HelloWorld} class.
 */
@ExtendWith({BufferParameterResolver.class, ChannelParameterResolver.class, FileParameterResolver.class})
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class})
public class HelloWorldTest {

    // -----------------------------------------------------------------------------------------------------------------
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    // ------------------------------------------------------------------------------------------------------------ SIZE

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
     * Asserts {@link HelloWorld#set(byte[])} method throws a {@code NullPointerException} when {@code array} argument
     * is {@code null}.
     */
    @Test
    public void assertSetArrayThrowsNullPointerExceptionWhenArrayIsNull() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#set(byte[])} method throws an {@code IndexOutOfBoundsException} when {@code
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
     * Asserts {@link HelloWorld#write(DataOutput)} method throws a {@code NullPointerException} when {@code data}
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
    public void assertWriteDataMethodWritesAsManyBytesAsHelloWorldSizeToData() throws IOException {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(DataOutput)} method returns the specified data output.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteDataMethodReturnsSpecifiedData() throws IOException {
        // TODO: implement!
    }

    // ----------------------------------------------------------------------------------------- write(RandomAccessFile)

    /**
     * Asserts {@link HelloWorld#write(RandomAccessFile)} method throws a {@code NullPointerException} when {@code file}
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
     * Asserts {@link HelloWorld#write(OutputStream)} method throws a {@code NullPointerException} when {@code stream}
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
     * Asserts {@link HelloWorld#write(File)} method throws a {@code NullPointerException} when {@code file} argument is
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
     * @param file an empty file to use with
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void assertWriteFileWritesExpectedNumberOfBytesToFile(@Temporary final File file) throws IOException {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(File)} method returns the specified file.
     *
     * @param expected an empty file to use with
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void assertWriteFileReturnsSpecifiedFile(@Temporary final File expected) throws IOException {
        // TODO: implement!
    }

    // ---------------------------------------------------------------------------------------------------- send(Socket)

    /**
     * Asserts {@link HelloWorld#send(Socket)} method throws a {@code NullPointerException} when the {@code socket}
     * argument is {@code null}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertSendSocketThrowsNullPointerExceptionWhenSocketIsNull() throws IOException {
        assertThrows(NullPointerException.class, () -> helloWorld.send((Socket) null));
    }

    /**
     * Asserts {@link HelloWorld#send(Socket)} method sends as many bytes as {@link HelloWorld#SIZE} via specified
     * socket.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertSendSocketSendsExpectedNumberOfBytesToSocket() throws IOException {
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
     * Asserts {@link HelloWorld#put(ByteBuffer)} method throws a {@code NullPointerException} when {@code buffer}
     * argument is {@code null}.
     */
    @Test
    public void assertPutBufferThrowsNullPointerExceptionWhenBufferIsNull() {
        assertThrows(NullPointerException.class, () -> helloWorld.put(null));
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method throws an {@code IllegalArgumentException} if {@link
     * ByteBuffer#remaining() buffer.remaining()} is less than {@link HelloWorld#SIZE}.
     */
    @Test
    public void assertPutBufferThrowsIllegalArgumentExceptionWhenBufferRemainingIsLessThanHelloWorldSize() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method puts as many bytes as {@link HelloWorld#SIZE} to specified byte
     * buffer.
     *
     * @param buffer a byte buffer which has a backing array.
     */
    @Test
    public void assertPutBufferPutsAsManyBytesAsHelloWorldSizeToBufferBackingArray(
            @HasBackingArray final ByteBuffer buffer) {
        assertTrue(buffer.remaining() >= HelloWorld.SIZE);
        assertTrue(buffer.hasArray());
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method puts as many byts as {@link HelloWorld#SIZE} to specified byte
     * buffer.
     *
     * @param buffer a byte buffer allocate directly.
     */
    @Test
    public void assertPutBufferPutsAsManyBytesAsHelloWorldToBuffer(final ByteBuffer buffer) {
        assertTrue(buffer.remaining() >= HelloWorld.SIZE);
        assertTrue(buffer.isDirect());
        logger.debug("buffer.hasArray: {}", buffer.hasArray());
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method returns specified byte buffer.
     *
     * @param expected a non-direct byte buffer whose {@link ByteBuffer#remaining() remaining()} is equals to or greater
     *                 than {@link HelloWorld#SIZE}
     */
    @Test
    public void assertPutBufferReturnsSpecifiedBufferBackingArray(@HasBackingArray final ByteBuffer expected) {
        assertTrue(expected.remaining() >= HelloWorld.SIZE);
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method returns the specified byte buffer.
     *
     * @param expected a direct buffer whose {@link ByteBuffer#remaining() remaining()} is equals to or greater than
     *                 {@link HelloWorld#SIZE}
     */
    @Test
    public void assertPutBufferReturnsSpecifiedBuffer(final ByteBuffer expected) {
        assertTrue(expected.remaining() >= HelloWorld.SIZE);
        assertTrue(expected.isDirect());
        // TODO: implement!
    }

    // -------------------------------------------------------------------------------------- write(WritableByteChannel)

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel)} method throws a {@code NullPointerException} when {@code
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
     * specified writable byte channel.
     *
     * @param channel a writable byte channel to use with
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteChannelWritesAsManyBytesAsHelloWorldSizeToChannel(final CountableByteChannel channel)
            throws IOException {
        // TODO: implement!!
    }

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel)} method writes as many bytes as {@link HelloWorld#SIZE} to
     * specified writable byte channel. This method uses a channel emulating the non-blocking mode.
     *
     * @param channel a writable byte channel to use with
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteChannelWritesAsManyBytesAsHelloWorldSizeToChannelNonBlocking(
            @NonBlocking final CountableByteChannel channel)
            throws IOException {
        // TODO: implement!!
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
     * Asserts {@link HelloWorld#write(Path)} method throws a {@code NullPointerException} when specified {@code path}
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
     * @param path an empty path to use with
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWritePathWritesAsManyBytesAsHelloWorldSizeToPath(@Temporary final Path path) throws IOException {
        // TODO: implement!!
    }

    /**
     * Asserts {@link HelloWorld#write(Path)} method returns the specified path.
     *
     * @param expected an empty file to use with
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWritePathReturnsSpecifiedPath(@Temporary final Path expected) throws IOException {
        // TODO: implement!!
    }

    // --------------------------------------------------------------------------------------------- send(SocketChannel)

    /**
     * Asserts {@link HelloWorld#send(SocketChannel)} method throws a {@code NullPointerException} when {@code socket}
     * argument is {@code null}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertSendChannelThrowsNullPointerExceptionIfSocketIsNull() throws IOException {
        assertThrows(NullPointerException.class, () -> helloWorld.send((SocketChannel) null));
    }

    /**
     * Asserts {@link HelloWorld#send(SocketChannel)} method sends as many bytes as {@link HelloWorld#SIZE} to specified
     * socket channel.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertSendChannelSendsAsManyBytesAsHelloWorldSizeToSocket() throws IOException {
        // TODO: implement!!
    }

    /**
     * Asserts {@link HelloWorld#send(SocketChannel)} method sends as many bytes as {@link HelloWorld#SIZE} to specified
     * socket channel. This method emulates non-blocking channel.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertSendChannelSendsAsManyBytesAsHelloWorldSizeToSocketNonBlockingEmulated() throws IOException {
        // TODO: implement!!
    }

    /**
     * Asserts {@link HelloWorld#send(SocketChannel)} method returns the specified socket channel.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertSendChannelReturnsSpecifiedChannel() throws IOException {
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
