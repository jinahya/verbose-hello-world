package com.github.jinahya.hello.api._java_util_function;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.IntConsumer;

/**
 * A class for testing {@link HelloWorld#acceptEach(IntConsumer)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("acceptEach(IntConsumer)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_AcceptEach_IntConsumer_Test
        extends HelloWorldTest {

    @DisplayName("""
            should throw a <NullPointerException>
            when the <consumer> argument is <null>""")
    @Test
    void _ThrowNullPointerException_ConsumerIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var consumer = (IntConsumer) null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.acceptEach(consumer)
        );
    }

    @DisplayName("should invoke <set(byte[])>, and <consumer.accept(b)> for each byte")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.set_array_sets_random_bytes(service());
        final var consumer = Mockito.mock(IntConsumer.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.acceptEach(consumer);
        // ------------------------------------------------------------------------------------ then
        final var array = HelloWorldTestUtils.set_array12_invoked_once(service);
        final var inOrder = Mockito.inOrder(consumer);
        for (final var b : array) {
            inOrder.verify(consumer, Mockito.calls(1)).accept(b);
        }
        inOrder.verifyNoMoreInteractions();
        Assertions.assertSame(consumer, result);
    }
}
