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
import java.nio.charset.StandardCharsets;

import static com.github.jinahya.hello.ByteBufferParameterResolver.DirectBuffer;
import static com.github.jinahya.hello.ByteBufferParameterResolver.NotEnoughRemaining;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
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
    public void assertSetArrayThrowsIndexOufOfBoundsExceptionWhenArrayLengthIsLessThanHelloWorldSize() {
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
     */
    @Test
    public void assertWriteDataMethodReturnsSpecifiedData() {
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
     * Asserts {@link HelloWorld#write(File)} method write exactly {@value HelloWorld#SIZE} bytes to specified file.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Test
    public void assertWriteFileWritesExactly12BytesToFile() throws IOException {
        // @todo: implement!
    }

    /**
     * Asserts {@link HelloWorld#write(File)} method returns specified file.
     */
    @Test
    public void assertWriteFileReturnsSpecifiedFile() {
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
    public void assertSendSocketSendsExactly12BytesToSocket() throws IOException {
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

    @Test
    public void assertPutBufferThrowsBufferOverflowExceptionWhenBufferRemainingIsLessThan12NonDirect(
            @NotEnoughRemaining final ByteBuffer buffer) {
        // @todo: implement!
    }

    @Test
    public void assertPutBufferThrowsBufferOverflowExceptionWhenBufferRemainingIsLessThan12Direct(
            @NotEnoughRemaining @DirectBuffer final ByteBuffer buffer) {
        // @todo: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method increases the {@code buffer}'s {@code position} by exactly
     * {@value HelloWorld#SIZE}.
     */
    @Test
    public void assertPutBufferIncreasesBufferPositionBy12NonDirect(final ByteBuffer buffer) {
        // @todo: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method increases the {@code buffer}'s {@code position} by exactly
     * {@value HelloWorld#SIZE}.
     */
    @Test
    public void assertPutBufferIncreasesBufferPositionBy12Direct(@DirectBuffer final ByteBuffer buffer) {
        // @todo: implement!
    }

    /**
     * Asserts {@link HelloWorld#put(ByteBuffer)} method returns specified byte buffer.
     */
    @Test
    public void assertPutBufferReturnsSpecifiedBuffer() {
        // @todo: implement!
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

    /**
     * Asserts {@link HelloWorld#set(byte[], int)} method of {@link #helloWorld} returns specified {@code array}
     * argument.
     */
    @Test
    void assertSetArrayWithIndexReturnsSpecifiedArray() {
        final byte[] array = current().nextBoolean() ? null : new byte[current().nextInt(HelloWorld.SIZE << 1)];
        final int index = current().nextInt();
        assertEquals(array, helloWorld.set(array, index));
        verify(helloWorld).set(array, index);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Spy
    private HelloWorld helloWorld;
}