package com.github.jinahya.hello.api._java_text;

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.text.BreakIterator;

/**
 * A class for testing
 * {@link com.github.jinahya.hello.api.HelloWorld#setText(BreakIterator) setText(iterator)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see BreakIterator#setText(String)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/text/BreakIterator.html">java.text.BreakIterator</a>
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_SetText_BreakIterator_Test extends HelloWorldTest {

    @DisplayName("(null)NullPointerException")
    @Test
    void _ThrowNullPointerException_IteratorIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final BreakIterator iterator = null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.setText(iterator)
        );
    }

    @DisplayName("iterator.setText(<string from set(byte[12])>)")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(service());
        final var iterator = Mockito.mock(BreakIterator.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.setText(iterator);
        // ------------------------------------------------------------------------------------ then
        HelloWorldTestUtils.set_array12_invoked_once(service);
//        Mockito.verify(iterator, Mockito.times(1))
//                .setText(HelloWorldTestUtils.hello_world_string());
        Assertions.assertSame(iterator, result);
    }
}
