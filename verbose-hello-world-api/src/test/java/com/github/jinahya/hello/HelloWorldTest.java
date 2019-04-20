package com.github.jinahya.hello;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

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
public class HelloWorldTest {

    /**
     * Asserts the value of {@link HelloWorld#SIZE} constant equals to the length of {@code hello, world} string in form
     * of bytes encoded with {@link StandardCharsets#US_ASCII} character set.
     */
    @Test
    void assertSizeEqualsToHelloWorldBytes() {
        final int expected = "hello, world".getBytes(StandardCharsets.US_ASCII).length;
        final int actual = HelloWorld.SIZE;
        assertEquals(
                expected, actual, "HelloWorld.SIZE(" + actual + ") is not equal to " + expected);
    }

    @BeforeEach
    private void stubSetAsReturnsGivenArray() {
        when(helloWorld.set(any(), anyInt())).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    void assertSetReturnsGiven() {
        final byte[] array = current().nextBoolean() ? null : new byte[current().nextInt(0, HelloWorld.SIZE << 2)];
        final int index = current().nextInt();
        assertEquals(array, helloWorld.set(array, index));
    }

    /**
     * Asserts {@link HelloWorld#set(byte[])} throws {@code NullPointerException} when {@code array} is {@code null}.
     */
    @Test
    public void assertSetThrowsNullPointerExceptionWhenArrayIsNull() {
        // @todo: implement!
    }

    @Spy
    private HelloWorld helloWorld;
}
