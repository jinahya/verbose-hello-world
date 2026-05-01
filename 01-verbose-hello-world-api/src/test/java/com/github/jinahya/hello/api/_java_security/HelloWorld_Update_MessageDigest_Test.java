package com.github.jinahya.hello.api._java_security;

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.security.MessageDigest;

/**
 * A class for testing {@link com.github.jinahya.hello.api.HelloWorld#update(MessageDigest)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/security/MessageDigest.html">java.security.MessageDigest</a>
 */
@Slf4j
class HelloWorld_Update_MessageDigest_Test
        extends HelloWorldTest {

    // ---------------------------------------------------------------------------------------------
    @Test
    void _ThrowNullPointerException_DigestIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final MessageDigest digest = null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(NullPointerException.class, () -> service.update(digest));
    }

    @DisplayName("digest.update(set(byte[12]))")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.set_array_returns_the_array(service());
        final var digest = Mockito.mock(MessageDigest.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.update(digest);
        // ------------------------------------------------------------------------------------ then
        final var array = HelloWorldTestUtils.set_array12_invoked_once(service);
//        Mockito.verify(digest, Mockito.times(1)).update(array);
        Assertions.assertEquals(digest, result);
    }
}
