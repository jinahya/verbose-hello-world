package com.github.jinahya.hello.api._javax_crypto;

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.crypto.Mac;

/**
 * A class for testing {@link com.github.jinahya.hello.api.HelloWorld#update(Mac)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/Mac.html">javax.crypto.Mac</a>
 */
@Slf4j
class HelloWorld_Update_Mac_Test
        extends HelloWorldTest {

    // ---------------------------------------------------------------------------------------------
    @Test
    void _ThrowNullPointerException_MacIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final Mac mac = null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(NullPointerException.class, () -> service.update(mac));
    }

    @DisplayName("mac.update(set(byte[12]))")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.set_array_returns_the_array(service());
        final var mac = Mockito.mock(Mac.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.update(mac);
        // ------------------------------------------------------------------------------------ then
        final var array = HelloWorldTestUtils.set_array12_invoked_once(service);
        Mockito.verify(mac, Mockito.times(1)).update(array);
        Assertions.assertSame(mac, result);
    }
}
