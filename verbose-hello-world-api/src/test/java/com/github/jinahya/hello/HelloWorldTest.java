package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * A class for testing {@link HelloWorld} class.
 */
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith({MockitoExtension.class})
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
     * Asserts {@link HelloWorld#write(OutputStream)} method returns specified {@code stream} argument.
     */
    @Test
    public void assertWriteStreamReturnsSpecifiedStream() {
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
    private void assertSetArrayWitnIndexReturnsSpecifiedArray() {
        final byte[] array = current().nextBoolean() ? null : new byte[current().nextInt(HelloWorld.SIZE << 1)];
        final int index = current().nextInt();
        assertEquals(array, helloWorld.set(array, index));
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Spy
    private HelloWorld helloWorld;
}
