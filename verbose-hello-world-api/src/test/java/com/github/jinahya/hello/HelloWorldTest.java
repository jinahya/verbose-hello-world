package com.github.jinahya.hello;

import com.github.jinahya.jupiter.api.extension.TempFileParameterResolver;
import com.github.jinahya.jupiter.api.io.TempFile;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;

/**
 * A class for unit-testing {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@MockitoSettings(strictness = LENIENT)
@ExtendWith({MockitoExtension.class, TempFileParameterResolver.class})
@Slf4j
public class HelloWorldTest {

    // ----------------------------------------------------------------------------------------------------------- BYTES

    /**
     * Asserts the value of {@link HelloWorld#BYTES} constant equals to the actual length of "{@code hello, world}"
     * string in a form of bytes encoded with {@code US-ASCII} character set.
     *
     * @see String#getBytes(Charset)
     * @see StandardCharsets#US_ASCII
     */
    @Test
    void assertHelloWorldBytesEqualsToActualNumberOfHelloWorldBytes() {
        final int expected = "hello, world".getBytes(US_ASCII).length;
        assertEquals(expected, BYTES);
    }

    // ----------------------------------------------------------------------------------------------------- set(byte[])

    /**
     * Asserts {@link HelloWorld#set(byte[])} method throws a {@link NullPointerException} when {@code array} argument
     * is {@code null}.
     */
    @DisplayName("Asserts set(byte[]) method throws NullPointerException when array is null")
    @Test
    public void assertSetArrayThrowsNullPointerExceptionWhenArrayIsNull() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#set(byte[])} method throws an {@link IndexOutOfBoundsException} when {@code
     * array.length} is less than {@link HelloWorld#BYTES}.
     */
    @DisplayName("Asserts set(byte[]) method throws IndexOutOfBoundsException when array.length is less than BYTES")
    @Test
    public void assertSetArrayThrowsIndexOufOfBoundsExceptionWhenArrayLengthIsLessThanHelloWorldBytes() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#set(byte[])} method returns specified array.
     */
    @DisplayName("Assert set(byte[]) returns specified array")
    @Test
    public void assertSetArrayReturnsSpecifiedArray() {
        // TODO: implement!
    }

    // --------------------------------------------------------------------------------------------- write(OutputStream)

    /**
     * Asserts {@link HelloWorld#write(OutputStream)} method throws a {@link NullPointerException} when {@code stream}
     * argument is {@code null}.
     */
    @Test
    public void assertWriteStreamThrowsNullPointerExceptionWhenStreamIsNull() {
        assertThrows(NullPointerException.class, () -> helloWorld.write((OutputStream) null));
    }

    /**
     * Asserts {@link HelloWorld#write(OutputStream)} method writes as many bytes as {@value HelloWorld#BYTES} to
     * specified stream.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteStreamWritesAsManyBytesAsHelloWorldBytesToStream() throws IOException {
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
     */
    @Test
    public void assertWriteFileThrowsNullPointerExceptionWhenFileIsNull() {
        assertThrows(NullPointerException.class, () -> helloWorld.write((File) null));
    }

    /**
     * Asserts {@link HelloWorld#write(File)} method writes as many bytes as {@link HelloWorld#BYTES} to specified
     * file.
     *
     * @param file a temp file to test with.
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void assertWriteFileWritesAsManyBytesAsHelloWorldBytes(@TempFile final File file) throws IOException {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(File)} method returns the specified file.
     *
     * @param file a temp file to test with.
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void assertWriteFileReturnsSpecifiedFile(@TempFile final File file) throws IOException {
        // TODO: implement!
    }

    // ---------------------------------------------------------------------------------------------------- send(Socket)

    /**
     * Asserts {@link HelloWorld#send(Socket)} method throws a {@link NullPointerException} when the {@code socket}
     * argument is {@code null}.
     */
    @Test
    public void assertSendSocketThrowsNullPointerExceptionWhenSocketIsNull() {
        assertThrows(NullPointerException.class, () -> helloWorld.send((Socket) null));
    }

    /**
     * Asserts {@link HelloWorld#send(Socket)} method sends as many bytes as {@link HelloWorld#BYTES} to specified
     * socket.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertSendSocketSendsAsManyBytesAsHelloWorldBytes() throws IOException {
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

    // ----------------------------------------------------------------------------------------- write(RandomAccessFile)

    /**
     * Asserts {@link HelloWorld#write(RandomAccessFile)} method throws a {@link NullPointerException} when {@code file}
     * argument is {@code null}.
     */
    @DisplayName("Asserts write(RandomAccessFile) method throws NullPointerException when file is null")
    @Test
    public void assertWriteRandomAccessFileThrowsNullPointerExceptionWhenFileIsNull() {
        assertThrows(NullPointerException.class, () -> helloWorld.write((RandomAccessFile) null));
    }

    /**
     * Asserts {@link HelloWorld#write(RandomAccessFile)} method writes as many bytes as {@link HelloWorld#BYTES} to
     * specified random access file.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("Asserts write(RandomAccessFile) method writes as many bytes as BYTES")
    @Test
    public void assertWriteRandomAccessFileWritesAsManyBytesAsHelloWorldBytes() throws IOException {
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

    // ----------------------------------------------------------------------------------------------- write(DataOutput)

    /**
     * Asserts {@link HelloWorld#write(DataOutput)} method throws a {@link NullPointerException} when {@code data}
     * argument is {@code null}.
     */
    @Test
    public void assertWriteDataThrowsNullPointerExceptionWhenDataIsNull() {
        assertThrows(NullPointerException.class, () -> helloWorld.write((DataOutput) null));
    }

    /**
     * Asserts {@link HelloWorld#write(DataOutput)} method writes as many bytes as {@value HelloWorld#BYTES} to
     * specified data output.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteDataWritesAsManyBytesAsHelloWorldBytes() throws IOException {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(DataOutput)} method returns the specified data output.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("Asserts write(DataOutput) method returns specified data output")
    @Test
    public void assertWriteDataReturnsSpecifiedData() throws IOException {
        // TODO: implement!
    }

    // ------------------------------------------------------------------------------------------------- put(ByteBuffer)

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method throws a {@link NullPointerException} when {@code buffer} is
     * {@code null}.
     */
    @DisplayName("Asserts put(ByteBuffer) method throws NullPointerException when buffer is null")
    @Test
    public void assertPutBufferThrowsNullPointerExceptionWhenBufferIsNull() {
        assertThrows(NullPointerException.class, () -> helloWorld.put(null));
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method throws an {@link BufferOverflowException} when {@code buffer}'s
     * {@link ByteBuffer#remaining() remaining} is less than {@link HelloWorld#BYTES}.
     */
    @DisplayName("Asserts put(ByteBuffer) method throws BufferOverflowException when buffer.remaining is not enough")
    @Test
    public void assertPutBufferThrowsBufferOverflowExceptionWhenBufferRemainingIsLessThanHelloWorldBytes() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method puts as many bytes as {@link HelloWorld#BYTES} to specified
     * buffer. This method aims to test with a byte buffer which {@link ByteBuffer#hasArray() has a backing-array}.
     */
    @DisplayName("Asserts put(ByteBuffer) puts as many bytes as BYTES; with backing array")
    @Test
    public void assertPutBufferPutsAsManyBytesAsHelloWorldBytesToBufferWithBackingArray() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method puts as many bytes as {@link HelloWorld#BYTES} to specified
     * byte buffer. This method aims to test with a byte buffer which {@link ByteBuffer#isDirect() doesn't have a
     * backing-array}.
     */
    @DisplayName("Asserts put(ByteBuffer) puts as many bytes as BYTES; without backing array")
    @Test
    public void assertPutBufferPutsAsManyBytesAsHelloWorldBytesToBuffer() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method returns specified byte buffer.
     */
    @Test
    public void assertPutBufferReturnsSpecifiedBuffer() {
        // TODO: implement!
    }

    // -------------------------------------------------------------------------------------- write(WritableByteChannel)

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel)} method throws a {@link NullPointerException} when {@code
     * channel} argument is {@code null}.
     */
    @Test
    public void assertWriteChannelThrowsNullPointerExceptionWhenChannelIsNull() {
        assertThrows(NullPointerException.class, () -> helloWorld.write((WritableByteChannel) null));
    }

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel)} method writes as many bytes as {@link HelloWorld#BYTES} to
     * specified channel.
     *
     * @throws IOException if an I/O error occurs.
     * @see HelloWorld#write(WritableByteChannel)
     */
    @Test
    public void assertWriteChannelWritesAsManyBytesAsHelloWorldBytesToChannel() throws IOException {
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
     */
    @Test
    public void assertWritePathThrowsNullPointerExceptionWhenPathIsNull() {
        assertThrows(NullPointerException.class, () -> helloWorld.write((Path) null));
    }

    /**
     * Asserts {@link HelloWorld#write(Path)} method writes as many bytes as {@link HelloWorld#BYTES} to specified
     * path.
     *
     * @param path a temporary file to test with.
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWritePathWritesAsManyBytesAsHelloWorldBytesToPath(@TempFile final Path path) throws IOException {
        // TODO: implement!!
    }

    /**
     * Asserts {@link HelloWorld#write(Path)} method returns the specified path.
     *
     * @param expected a temporary file to test with.
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWritePathReturnsSpecifiedPath(@TempFile final Path expected) throws IOException {
        // TODO: implement!!
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Stubs {@link HelloWorld#set(byte[], int)} method of {@link Spy spied} {@code helloWorld} instance to return
     * specified {@code array} argument.
     */
    @BeforeEach
    private void stubSetArrayWithIndexToReturnSpecifiedArray() {
        when(helloWorld.set(any(byte[].class), anyInt())) // <1>
                .thenAnswer(i -> i.getArgument(0));       // <2>
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Spy // <1>
            HelloWorld helloWorld;
}
