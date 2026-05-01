package com.github.jinahya.hello.api._java_security;

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import com.github.jinahya.hello.api._Java_Security_TestUtils;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Stream;

@畵蛇添足
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Update_Signature_畵蛇添足_Test
        extends HelloWorldTest {

    @TempDir
    private static File tempDir;

    @BeforeEach
    void __() throws SignatureException {
        HelloWorldTestUtils.update_signature_updates_hello_world_bytes(service());
    }

    static List<String> algorithms() {
        return _Java_Security_TestUtils.SIGNATURE_ALGORITHMS;
    }

    @DisplayName("RSASSA-PSS")
    @Nested
    class RSASSA_PSS_Test {

        static final String KEY_PAIR_ALGORITHM = "RSA";

        private static final String MGF1_ALGORITHM = "MGF1";

        private static final int TRAILER_FIELD_BC = 1; // Represents 0xBC

        static final String SIGNATURE_ALGORITHM = "RSASSA-PSS";

        static Stream<Arguments> pssTestProvider() {
            return Stream.of(2048, 3072, 4096).flatMap(k -> Stream.of(
                    Arguments.of(k, MGF1ParameterSpec.SHA256, 32),
                    Arguments.of(k, MGF1ParameterSpec.SHA384, 48)
            ));
        }

        @ParameterizedTest(name = "{0}-bit RSA with {1}")
        @MethodSource("pssTestProvider")
        void __(int keysize, final MGF1ParameterSpec mgfSpec, final int saltLen) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var keyPair = _Java_Security_TestUtils.generateKeyPair(
                    KEY_PAIR_ALGORITHM,
                    keysize
            );
            final var pssSpec = new PSSParameterSpec(
                    mgfSpec.getDigestAlgorithm(),
                    MGF1_ALGORITHM,
                    mgfSpec,
                    saltLen,
                    TRAILER_FIELD_BC
            );
            // -------------------------------------------------------------------------------- when
            final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
            // --------------------------------------------------------------- sign with private key
            instance.initSign(keyPair.getPrivate());
            instance.setParameter(pssSpec);
            service.update(instance);
            final var signature = instance.sign();
            // -------------------------------------------------------------- verify with public key
            instance.initVerify(keyPair.getPublic());
            instance.setParameter(pssSpec);
            service.update(instance);
            final var verified = instance.verify(signature);
            // -------------------------------------------------------------------------------- then
            Assertions.assertTrue(verified);
        }
    }

    /**
     * .
     * <blockquote>
     * For signature generation, if the security strength of the digest algorithm is weaker than the
     * security strength of the key used to sign the signature (for example, using (2048, 256)-bit
     * DSA keys with the SHA1withDSA signature), then the operation will fail with the error
     * message: "The security strength of SHA1 digest algorithm is not sufficient for this key
     * size.
     * </blockquote>
     *
     * @see <a href="https://docs.oracle.com/en/java/javase/25/security/oracle-providers.html"></a>
     */
    @DisplayName("SHA1withDSA")
    @Nested
    class SHA1WithDSATest {

        static final String KEY_PAIR_ALGORITHM = "DSA";

        static final String SIGNATURE_ALGORITHM = "SHA1withDSA";

        @ValueSource(ints = {
                512, 1024
        })
        @ParameterizedTest
        void __SHA1withDSA(final int keysize)
                throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = HelloWorldTestUtils.set_array_returns_the_array(service());
            final var keyPair = _Java_Security_TestUtils.generateKeyPair(KEY_PAIR_ALGORITHM,
                                                                         keysize);
            // -------------------------------------------------------------------------------- when
            final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
            // --------------------------------------------------------------- sign with private key
            instance.initSign(keyPair.getPrivate());
            service.update(instance);
            final var signature = instance.sign();
            log.debug("signature: {}", HexFormat.of().formatHex(signature));
            // -------------------------------------------------------------- verify with public key
            instance.initVerify(keyPair.getPublic());
            service.update(instance);
            final var verified = instance.verify(signature);
            // -------------------------------------------------------------------------------- then
            Assertions.assertTrue(verified);
        }
    }

    @DisplayName("SHA256withDSA")
    @Nested
    class SHA256WithDSATest {

        static final String KEY_PAIR_ALGORITHM = "DSA";

        static final String SIGNATURE_ALGORITHM = "SHA256withDSA";

        @ValueSource(ints = {
                512, 1024, 2048
        })
        @ParameterizedTest
        void __SHA1withDSA(final int keysize)
                throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = HelloWorldTestUtils.set_array_returns_the_array(service());
            final var keyPair = _Java_Security_TestUtils.generateKeyPair(KEY_PAIR_ALGORITHM,
                                                                         keysize);
            // -------------------------------------------------------------------------------- when
            final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
            // --------------------------------------------------------------- sign with private key
            instance.initSign(keyPair.getPrivate());
            service.update(instance);
            final var signature = instance.sign();
            log.debug("signature: {}", HexFormat.of().formatHex(signature));
            // -------------------------------------------------------------- verify with public key
            instance.initVerify(keyPair.getPublic());
            service.update(instance);
            final var verified = instance.verify(signature);
            // -------------------------------------------------------------------------------- then
            Assertions.assertTrue(verified);
        }
    }

    @DisplayName("SHA256withECDSA")
    @Nested
    class SHA256WithECDSA_Test {

        static final String KEY_PAIR_ALGORITHM = "EC";

        static final String SIGNATURE_ALGORITHM = "SHA256withECDSA";

        static final String CURVE_NAME = "secp256r1"; // Standard for P-256

        @Test
        void __()
                throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = HelloWorldTestUtils.set_array_returns_the_array(service());
            final var keyPair = _Java_Security_TestUtils.generateKeyPair(KEY_PAIR_ALGORITHM,
                                                                         new ECGenParameterSpec(
                                                                                 CURVE_NAME));
            // -------------------------------------------------------------------------------- when
            final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
            // --------------------------------------------------------------- sign with private key
            instance.initSign(keyPair.getPrivate());
            service.update(instance);
            final var signature = instance.sign();
            log.debug("signature: {}", HexFormat.of().formatHex(signature));
            // -------------------------------------------------------------- verify with public key
            instance.initVerify(keyPair.getPublic());
            service.update(instance);
            final var verified = instance.verify(signature);
            // -------------------------------------------------------------------------------- then
            Assertions.assertTrue(verified);
        }
    }

    @DisplayName("SHA384withECDSA")
    @Nested
    class SHA384withECDSA_Test {

        static final String KEY_PAIR_ALGORITHM = "EC";

        static final String SIGNATURE_ALGORITHM = "SHA384withECDSA";

        static final String CURVE_NAME = "secp384r1";

        @Test
        void __()
                throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = HelloWorldTestUtils.set_array_returns_the_array(service());
            final var keyPair = _Java_Security_TestUtils.generateKeyPair(KEY_PAIR_ALGORITHM,
                                                                         new ECGenParameterSpec(
                                                                                 CURVE_NAME));
            // -------------------------------------------------------------------------------- when
            final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
            // --------------------------------------------------------------- sign with private key
            instance.initSign(keyPair.getPrivate());
            service.update(instance);
            final var signature = instance.sign();
            log.debug("signature: {}", HexFormat.of().formatHex(signature));
            // -------------------------------------------------------------- verify with public key
            instance.initVerify(keyPair.getPublic());
            service.update(instance);
            final var verified = instance.verify(signature);
            // -------------------------------------------------------------------------------- then
            Assertions.assertTrue(verified);
        }
    }

    @DisplayName("SHA1withRSA")
    @Nested
    class SHA1withRSA_Test {

        static final String KEY_PAIR_ALGORITHM = "RSA";

        static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

        @ValueSource(ints = {
                1024, 2048, 2072, 4096
        })
        @ParameterizedTest
        void __(final int keysize)
                throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = HelloWorldTestUtils.set_array_returns_the_array(service());
            final var keyPair = _Java_Security_TestUtils.generateKeyPair(KEY_PAIR_ALGORITHM,
                                                                         keysize);
            // -------------------------------------------------------------------------------- when
            final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
            // --------------------------------------------------------------- sign with private key
            instance.initSign(keyPair.getPrivate());
            service.update(instance);
            final var signature = instance.sign();
            log.debug("signature: {}", HexFormat.of().formatHex(signature));
            // -------------------------------------------------------------- verify with public key
            instance.initVerify(keyPair.getPublic());
            service.update(instance);
            final var verified = instance.verify(signature);
            // -------------------------------------------------------------------------------- then
            Assertions.assertTrue(verified);
        }
    }

    @DisplayName("SHA256withRSA")
    @Nested
    class SHA256withRSA_Test {

        static final String KEY_PAIR_ALGORITHM = "RSA";

        static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

        @ValueSource(ints = {
                2048, 3072, 4096
        })
        @ParameterizedTest
        void __(final int keysize)
                throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = HelloWorldTestUtils.set_array_returns_the_array(service());
            final var keyPair = _Java_Security_TestUtils.generateKeyPair(KEY_PAIR_ALGORITHM,
                                                                         keysize);
            // -------------------------------------------------------------------------------- when
            final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
            // --------------------------------------------------------------- sign with private key
            instance.initSign(keyPair.getPrivate());
            service.update(instance);
            final var signature = instance.sign();
            log.debug("signature: {}", HexFormat.of().formatHex(signature));
            // -------------------------------------------------------------- verify with public key
            instance.initVerify(keyPair.getPublic());
            service.update(instance);
            final var verified = instance.verify(signature);
            // -------------------------------------------------------------------------------- then
            Assertions.assertTrue(verified);
        }
    }

    @DisplayName("SHA384withRSA")
    @Nested
    class SHA384withRSA_Test {

        static final String KEY_PAIR_ALGORITHM = "RSA";

        static final String SIGNATURE_ALGORITHM = "SHA384withRSA";

        @ValueSource(ints = {
                2048, 3072, 4096
        })
        @ParameterizedTest
        void __(final int keysize)
                throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = HelloWorldTestUtils.set_array_returns_the_array(service());
            final var keyPair = _Java_Security_TestUtils.generateKeyPair(KEY_PAIR_ALGORITHM,
                                                                         keysize);
            // -------------------------------------------------------------------------------- when
            final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
            // --------------------------------------------------------------- sign with private key
            instance.initSign(keyPair.getPrivate());
            service.update(instance);
            final var signature = instance.sign();
            log.debug("signature: {}", HexFormat.of().formatHex(signature));
            // -------------------------------------------------------------- verify with public key
            instance.initVerify(keyPair.getPublic());
            service.update(instance);
            final var verified = instance.verify(signature);
            // -------------------------------------------------------------------------------- then
            Assertions.assertTrue(verified);
        }
    }
}
