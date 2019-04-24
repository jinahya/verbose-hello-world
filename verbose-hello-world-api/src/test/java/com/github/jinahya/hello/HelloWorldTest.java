package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static com.github.jinahya.hello.ByteBufferParameterResolver.DirectBuffer;
import static com.github.jinahya.hello.ByteBufferParameterResolver.NotEnoughRemaining;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * A class for testing {@link HelloWorld} class.
 */
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class, ByteBufferParameterResolver.class})
@Slf4j
public class HelloWorldTest {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Asserts the value of {@link HelloWorld#SIZE} constant equals to the length of {@code hello, world} string in form
     * of bytes encoded with {@link StandardCharsets#US_ASCII} character set.
     */
    @Test
    void assertSizeEqualsToHelloWorldBytes() {
        assertEquals("hello, world".getBytes(StandardCharsets.US_ASCII).length, HelloWorld.SIZE);
    }

    // ----------------------------------------------------------------------------------------------------- set(byte[])

    /**
     * Asserts {@link HelloWorld#set(byte[])} throws {@code NullPointerException} when {@code array} is {@code null}.
     */
    @Test
    public void assertSetArrayThrowsNullPointerExceptionWhenArrayIsNull() {
        // @todo: implement!
    }

    /**
     * Asserts {@link HelloWorld#set(byte[])} method throws {@code IndexOutOfBoundsException} when {@code array.length}
     * is less than {@link HelloWorld#SIZE}.
     */
    @Test
    public void assertSetArrayThrowsIndexOufOfBoundsExceptionWhenArrayLengthIsLessThan12() {
        // @todo: implement!
    }

    /**
     * Asserts {@link HelloWorld#set(byte[])} method returns specified array argument.
     */
    @Test
    public void assertSetArrayReturnsGivenArray() {
        // @todo: implement!
    }

    // ----------------------------------------------------------------------------------------------- write(DataOutput)

    /**
     * Asserts {@link HelloWorld#write(DataOutput)} method throws a {@code NullPointerException} when {@code data}
     * argument is {@code null}.
     */
    @Test
    public void assertWriteDataThrowsNullPointerExceptionWhenDataIsNull() {
        // @todo: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(DataOutput)} method writes exactly {@value HelloWorld#SIZE} bytes to specified
     * data output.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteDataMethodWritesExactly12BytesToData() throws IOException {
        // @todo: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(DataOutput)} method returns the specified data output.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteDataMethodReturnsSpecifiedData() throws IOException {
        // @todo: implement!
    }

    // --------------------------------------------------------------------------------------------- write(OutputStream)

    /**
     * Asserts {@link HelloWorld#write(OutputStream)} method throws {@code NullPointerException} when {@code stream}
     * argument is {@code null}.
     */
    @Test
    public void assertWriteStreamThrowsNullPointerExceptionWhenStreamIsNull() {
        // @todo: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(OutputStream)} method writes exactly {@value HelloWorld#SIZE} bytes to the
     * stream.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteStreamWritesExactly12BytesToStream() throws IOException {
        // @todo: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(OutputStream)} method returns specified {@code stream} argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteStreamReturnsSpecifiedStream() throws IOException {
        // @todo: implement!
    }

    // ----------------------------------------------------------------------------------------------------- write(File)

    /**
     * Asserts {@link HelloWorld#write(File)} method throws {@code NullPointerException} when {@code file} argument is
     * {@code null}.
     */
    @Test
    public void assertWriteFileThrowsNullPointerExceptionWhenFileIsNull() {
        // @todo: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(File)} method writes exactly {@value HelloWorld#SIZE} bytes to specified file.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteFileWritesExpectedNumberOfBytesToFile() throws IOException {
        // @todo: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(File)} method returns specified file.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteFileReturnsSpecifiedFile() throws IOException {
        // @todo: implement!
    }

    // ---------------------------------------------------------------------------------------------------- send(Socket)

    /**
     * Asserts {@link HelloWorld#send(Socket)} method throws {@code NullPointerException} when the {@code socket}
     * argument is {@code null}.
     */
    @Test
    public void assertSendSocketThrowsNullPointerExceptionWhenSocketIsNull() {
        // @todo: implement!
    }

    /**
     * Asserts {@link HelloWorld#send(Socket)} method sends exactly {@value HelloWorld#SIZE} bytes to the {@code
     * socket}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertSendSocketSendsExpectedNumberOfBytesToSocket() throws IOException {
        // @todo: implement!
    }

    /**
     * Asserts {@link HelloWorld#send(Socket)} method returns the specified {@code socket}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertSendSocketReturnsSpecifiedSocket() throws IOException {
        // @todo: implement!
    }

    // ------------------------------------------------------------------------------------------------- put(ByteBuffer)

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method throws a {@code NullPointerException} when {@code buffer}
     * argument is {@code null}.
     */
    @Test
    public void assertPutBufferThrowsNullPointerExceptionWhenBufferIsNull() {
        // @todo: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method throws a {@link java.nio.BufferOverflowException} if specified
     * buffer's {@code remaining} is less than {@link HelloWorld#SIZE}.
     *
     * @param buffer a byte buffer whose {@code remaining} is less than {@link HelloWorld#SIZE}
     */
    @Test
    public void assertPutBufferThrowsBufferOverflowExceptionWhenBufferRemainingIsLessThanHelloWorldSize(
            @NotEnoughRemaining final ByteBuffer buffer) {
        assertTrue(buffer.remaining() < HelloWorld.SIZE);
        // @todo: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method throws a {@link java.nio.BufferOverflowException} if specified
     * direct buffer's {@code remaining} is less than {@link HelloWorld#SIZE}.
     *
     * @param buffer a direct byte buffer whose {@code remaining} is less than {@link HelloWorld#SIZE}
     */
    @Test
    public void assertPutBufferThrowsBufferOverflowExceptionWhenBufferRemainingIsLessThanHelloWorldSizeDirect(
            @NotEnoughRemaining @DirectBuffer final ByteBuffer buffer) {
        assertTrue(buffer.remaining() < HelloWorld.SIZE);
        assertTrue(buffer.isDirect());
        // @todo: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method increases the {@code buffer}'s {@code position} by {@value
     * HelloWorld#SIZE}.
     *
     * @param buffer a non direct byte buffer whose {@code remaining} is equals to or greater than {@link
     *               HelloWorld#SIZE}
     */
    @Test
    public void assertPutBufferIncreasesBufferPositionByHelloWorldSize(final ByteBuffer buffer) {
        assertTrue(buffer.remaining() >= HelloWorld.SIZE);
        // @todo: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method increases specified direct buffer's {@code position} by {@value
     * HelloWorld#SIZE}.
     *
     * @param buffer a direct buffer whose {@code remaining} is equals to or greater than {@link HelloWorld#SIZE}
     */
    @Test
    public void assertPutBufferIncreasesBufferPositionByHelloWorldSizeDirect(@DirectBuffer final ByteBuffer buffer) {
        assertTrue(buffer.remaining() >= HelloWorld.SIZE);
        assertTrue(buffer.isDirect());
        // @todo: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method returns specified byte buffer.
     *
     * @param expected a non direct byte buffer whose {@code remaining} is equals to or greater than {@link
     *                 HelloWorld#SIZE}
     */
    @Test
    public void assertPutBufferReturnsSpecifiedBuffer(final ByteBuffer expected) {
        assertTrue(expected.remaining() >= HelloWorld.SIZE);
        // @todo: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method returns specified byte buffer.
     *
     * @param expected a direct buffer whose {@code remaining} is equals to or greater than {@link HelloWorld#SIZE}
     */
    @Test
    public void assertPutBufferReturnsSpecifiedBufferDirect(@DirectBuffer final ByteBuffer expected) {
        assertTrue(expected.remaining() >= HelloWorld.SIZE);
        assertTrue(expected.isDirect());
        // @todo: implement!
    }

    // -------------------------------------------------------------------------------------- write(WritableByteChannel)

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel)} method throws a {@code NullPointerException} when {@code
     * channel} argument is {@code null}.
     */
    @Test
    public void assertWriteChannelThrowsNullPointerExceptionWhenChannelIsNull() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel)} method writes {@value HelloWorld#SIZE} bytes to the {@code
     * channel} argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteChannelWritesOfHelloWorldSizeBytes() throws IOException {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel)} method writes {@value HelloWorld#SIZE} bytes to the {@code
     * channel} argument. This method uses a channel emulating the non-blocking mode.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteChannelWritesOfHelloWorldSizeBytesNonBlockingEmulated() throws IOException {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(WritableByteChannel)} method returns specified channel.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteChannelReturnsSpecifiedChannel() throws IOException {
        // TODO: implement!
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
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(Path)} method writes {@value HelloWorld#SIZE} bytes to specified path.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWritePathWritesHelloWorldSizeBytesToPath() throws IOException {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(Path)} method returns specified path.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWritePathReturnsSpecifiedPath() throws IOException {
        // TODO: implement!
    }

    // --------------------------------------------------------------------------------------------- send(SocketChannel)

    /**
     * Asserts {@link HelloWorld#send(SocketChannel)} method throws a {@code NullPointerException} when specified socket
     * channel is {@code null}.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void assertSendChannelThrowsNullPointerExceptionIfChannelIsNull() throws IOException {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#send(SocketChannel)} method sends {@link HelloWorld#SIZE} bytes to specified socket
     * channel.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void assertSendChannelSendsHelloWorldSizeBytesToSpecifiedChannel() throws IOException {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#send(SocketChannel)} method sends {@link HelloWorld#SIZE} bytes to specified socket
     * channel. This method emulates non-blocking channel.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void assertSendChannelSendsHelloWorldSizeBytesToSpecifiedChannelEmulateNonBlocking() throws IOException {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#send(SocketChannel)} method returns specified socket channel.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void assertSendChannelReturnsSpecifiedChannel() throws IOException {
        // TODO: implement!
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Stubs {@link HelloWorld#set(byte[], int)} method of {@link #helloWorld} to return specified {@code array}
     * argument.
     */
    @BeforeEach
    private void stubSetArrayWithIndexToReturnSpecifiedArray() {
        when(helloWorld.set(any(), anyInt())).thenAnswer(i -> i.getArgument(0));
    }

//    /**
//     * Asserts {@link HelloWorld#set(byte[], int)} method of {@link #helloWorld} returns specified {@code array}
//     * argument.
//     */
//    @Test
//    void assertSetArrayWithIndexReturnsSpecifiedArray() {
//        final byte[] array = current().nextBoolean() ? null : new byte[current().nextInt(HelloWorld.SIZE << 1)];
//        final int index = current().nextInt();
//        assertEquals(array, helloWorld.set(array, index));
//        verify(helloWorld).set(array, index);
//    }

    // -----------------------------------------------------------------------------------------------------------------
    @Spy
    private HelloWorld helloWorld;
}
