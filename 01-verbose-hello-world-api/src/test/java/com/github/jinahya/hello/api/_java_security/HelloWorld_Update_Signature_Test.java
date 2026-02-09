package com.github.jinahya.hello.api._java_security;

import com.github.jinahya.hello.api.HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * .
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/security/Signature.html">java.security.Signature</a>
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/security/KeyPairGenerator.html">java.security.KeyPairGenerator</a>
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/specs/security/standard-names.html#signature-algorithms">Signature
 * Algorithms</a>
 */
@Slf4j
class HelloWorld_Update_Signature_Test extends HelloWorldTest {

    static final List<String> ALGORITHMS = List.of(
            "RSASSA-PSS",
            "SHA1withDSA",
            "SHA256withDSA",
            "SHA256withECDSA",
            "SHA384withECDS",
            "SHA1withRSA",
            "SHA256withRSA",
            "SHA384withRSA"
    );

    static List<String> algorithms() {
        return ALGORITHMS;
    }

    static KeyPair generateKeyPair(final String algorithm, final int keysize)
            throws NoSuchAlgorithmException {
        final var generator = KeyPairGenerator.getInstance(algorithm);
        final var random = SecureRandom.getInstanceStrong();
        generator.initialize(keysize, random);
        return generator.generateKeyPair();
    }

    static KeyPair generateKeyPair(final String algorithm, final AlgorithmParameterSpec spec)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        final var generator = KeyPairGenerator.getInstance(algorithm);
        final var random = SecureRandom.getInstanceStrong();
        generator.initialize(spec, random);
        return generator.generateKeyPair();
    }

    static <R> R applyKeyPair(final String algorithm, final int keysize,
                              final Function<? super KeyPair, ? extends R> function)
            throws NoSuchAlgorithmException {
        Objects.requireNonNull(function, "function is null");
        final var generator = KeyPairGenerator.getInstance(algorithm);
        final var random = SecureRandom.getInstanceStrong();
        generator.initialize(keysize, random);
        final var generated = generator.generateKeyPair();
        return function.apply(generated);
    }

    static <R> R applyKeyPair(
            final String algorithm, final int keysize,
            final BiFunction<? super PublicKey, ? super PrivateKey, ? extends R> function)
            throws NoSuchAlgorithmException {
        return applyKeyPair(
                algorithm,
                keysize,
                p -> function.apply(p.getPublic(), p.getPrivate())
        );
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
        void __(int keysize, final MGF1ParameterSpec mgfSpec, final int saltLen)
                throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var keyPair = generateKeyPair(KEY_PAIR_ALGORITHM, keysize);
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
    @Nested
    class SHA1WithDSATest {

        static final String KEY_PAIR_ALGORITHM = "DSA";

        static final String SIGNATURE_ALGORITHM = "SHA1withDSA";

        @ValueSource(ints = {
                512, 1024
        })
        @ParameterizedTest
        void __SHA1withDSA(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var keyPair = generateKeyPair(KEY_PAIR_ALGORITHM, keysize);
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

    @Nested
    class SHA256WithDSATest {

        static final String KEY_PAIR_ALGORITHM = "DSA";

        static final String SIGNATURE_ALGORITHM = "SHA256withDSA";

        @ValueSource(ints = {
                512, 1024, 2048
        })
        @ParameterizedTest
        void __SHA1withDSA(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var keyPair = generateKeyPair(KEY_PAIR_ALGORITHM, keysize);
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

    @Nested
    class SHA256WithECDSA_Test {

        static final String KEY_PAIR_ALGORITHM = "EC";

        static final String SIGNATURE_ALGORITHM = "SHA256withECDSA";

        static final String CURVE_NAME = "secp256r1"; // Standard for P-256

        @Test
        void __() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var keyPair = generateKeyPair(KEY_PAIR_ALGORITHM,
                                                new ECGenParameterSpec(CURVE_NAME));
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

    @Nested
    class SHA384withECDSA_Test {

        static final String KEY_PAIR_ALGORITHM = "EC";

        static final String SIGNATURE_ALGORITHM = "SHA384withECDSA";

        static final String CURVE_NAME = "secp384r1";

        @Test
        void __() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var keyPair = generateKeyPair(KEY_PAIR_ALGORITHM,
                                                new ECGenParameterSpec(CURVE_NAME));
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

    @Nested
    class SHA1withRSA_Test {

        static final String KEY_PAIR_ALGORITHM = "RSA";

        static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

        @ValueSource(ints = {
                1024, 2048, 2072, 4096
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var keyPair = generateKeyPair(KEY_PAIR_ALGORITHM, keysize);
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

    @Nested
    class SHA256withRSA_Test {

        static final String KEY_PAIR_ALGORITHM = "RSA";

        static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

        @ValueSource(ints = {
                2048, 3072, 4096
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var keyPair = generateKeyPair(KEY_PAIR_ALGORITHM, keysize);
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

    @Nested
    class SHA384withRSA_Test {

        static final String KEY_PAIR_ALGORITHM = "RSA";

        static final String SIGNATURE_ALGORITHM = "SHA384withRSA";

        @ValueSource(ints = {
                2048, 3072, 4096
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var keyPair = generateKeyPair(KEY_PAIR_ALGORITHM, keysize);
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
