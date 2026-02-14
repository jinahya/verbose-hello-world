package com.github.jinahya.hello.api._java_security;

import com.github.jinahya.hello.api.HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

/**
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorld_Update_Digest_Test extends HelloWorldTest {

    static final List<String> ALGORITHMS = List.of(
            "SHA-1",
            "SHA-256",
            "SHA-384"
    );

    static List<String> algorithms() {
        return ALGORITHMS;
    }

    // ---------------------------------------------------------------------------------------------
    @Test
    void _ThrowNullPointerException_DigestIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final MessageDigest digest = null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(NullPointerException.class, () -> service.update(digest));
    }

    @MethodSource({"algorithms"})
    @ParameterizedTest
    void __(final String algorithm) throws NoSuchAlgorithmException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var digest = Mockito.spy(MessageDigest.getInstance(algorithm));
        // ----------------------------------------------------------------------------- when / then
        final var result = service.update(digest);
        // ------------------------------------------------------------------------------------ then
        final var array = verify_set_array12_invoked_once();
        Mockito.verify(digest, Mockito.times(1)).update(array);
        Assertions.assertEquals(digest, result);
        {
            final var digested = digest.digest();
            log.debug("{}: [{}] ({} bytes, {} bits)", String.format("%1$7s", algorithm),
                      HexFormat.of().formatHex(digested), digested.length, digested.length << 3);
        }
    }
}
