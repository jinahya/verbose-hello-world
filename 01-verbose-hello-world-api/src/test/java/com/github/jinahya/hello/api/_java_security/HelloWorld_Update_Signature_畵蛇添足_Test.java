package com.github.jinahya.hello.api._java_security;

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import com.github.jinahya.hello.api._Java_Security_TestUtils;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
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
import org.opentest4j.TestAbortedException;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

// https://docs.oracle.com/en/java/javase/25/security/oracle-providers.html
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

    private static void printf(final Object keyPairParameter,
                               final @Nullable Object signatureParameter,
                               final int iteration,
                               final byte[] signature) {
        final var encoded = Base64.getEncoder().encodeToString(signature);
        System.out.printf("%10s %20s #%d (%4d) %s...%s%n", keyPairParameter,
                          Optional.ofNullable(signatureParameter).orElse(""),
                          iteration, signature.length,
                          encoded.substring(0, 8),
                          encoded.substring(encoded.length() - 8));
    }

    private static void printf(final int keysize, final int iteration, final byte[] signature) {
        System.out.printf("%4d %d (%d) %s%n", keysize, iteration,
                          signature.length, Base64.getEncoder().encodeToString(signature));
    }

    @DisplayName("RSASSA-PSS")
    @Nested
    class RSASSA_PSS_Test {

        private static final String KEY_PAIR_ALGORITHM = "RSASSA-PSS";

        private static final String MGF1_ALGORITHM = "MGF1";

        private static final String SIGNATURE_ALGORITHM = "RSASSA-PSS";

        private static Stream<Arguments> pssTestProvider() {
            return Stream.of(2048, 3072, 4096)
                    .flatMap(k -> Stream.of(
                            Arguments.of(k, MGF1ParameterSpec.SHA256, 256 >> 3),
                            Arguments.of(k, MGF1ParameterSpec.SHA384, 384 >> 3)
                    ));
        }

        @ParameterizedTest(name = "{0}-bit RSA with {1}")
        @MethodSource({"pssTestProvider"})
        void __(int keysize, final MGF1ParameterSpec mgfSpec, final int saltLen) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var keyPair = _Java_Security_TestUtils.generateKeyPair(
                    KEY_PAIR_ALGORITHM,
                    keysize
            );
            final var pssSpec = new PSSParameterSpec(
                    mgfSpec.getDigestAlgorithm(),     // <mdName>
                    MGF1_ALGORITHM,                   // <mgfName>
                    mgfSpec,                          // <mgfSpec>
                    saltLen,                          // <saltLen>
                    PSSParameterSpec.TRAILER_FIELD_BC // <trailerField>
            );
            final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
            for (int i = 0; i < 2; i++) {
                // ----------------------------------------------------------- sign with private key
                try {
                    instance.initSign(keyPair.getPrivate());
                } catch (final InvalidKeyException ike) {
                    throw new TestAbortedException(ike.getMessage(), ike);
                }
                instance.setParameter(pssSpec);
                service().update(instance);
                final var signature = instance.sign();
                printf(keysize, pssSpec, i, signature);
                // ---------------------------------------------------------- verify with public key
                instance.initVerify(keyPair.getPublic());
                instance.setParameter(pssSpec);
                service().update(instance);
                final var verified = instance.verify(signature);
                // ---------------------------------------------------------------------------- then
                Assertions.assertTrue(verified);
            }
        }

        @Test
        void __file() throws Exception {
            final var file = HelloWorldTestUtils.writeSome(
                    File.createTempFile("tmp", null, tempDir)
            );
            // -------------------------------------------------------------------------------------
            final var mgfSpec = MGF1ParameterSpec.SHA384;
            final var pssSpec = new PSSParameterSpec(
                    mgfSpec.getDigestAlgorithm(),
                    MGF1_ALGORITHM,
                    mgfSpec,
                    384 >> 3,
                    PSSParameterSpec.TRAILER_FIELD_BC
            );
            final PublicKey publicKey;
            final byte[] signature;
            {
                final var keyPair = _Java_Security_TestUtils.generateKeyPair(
                        KEY_PAIR_ALGORITHM,
                        4096
                );
                publicKey = keyPair.getPublic();
                final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
                instance.initSign(keyPair.getPrivate());
                instance.setParameter(pssSpec);
                try (var stream = new FileInputStream(file)) {
                    final var b = new byte[128];
                    for (int r; (r = stream.read(b)) != -1; ) {
                        instance.update(b, 0, r);
                    }
                }
                signature = instance.sign();
            }
            {
                final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
                instance.initVerify(publicKey);
                instance.setParameter(pssSpec);
                try (var channel = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {
                    for (final var b = ByteBuffer.allocate(128); channel.read(b.clear()) != -1; ) {
                        instance.update(b.flip());
                    }
                }
                final var verified = instance.verify(signature);
                Assertions.assertTrue(verified);
            }
        }
    }

    // https://docs.oracle.com/en/java/javase/25/security/oracle-providers.html
    @DisplayName("SHA1withDSA")
    @Nested
    class SHA1WithDSATest {

        private static final String KEY_PAIR_ALGORITHM = "DSA";

        private static final String SIGNATURE_ALGORITHM = "SHA1withDSA";

        @ValueSource(ints = {
                1024, 2048
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var keyPair = _Java_Security_TestUtils.generateKeyPair(
                    KEY_PAIR_ALGORITHM,
                    keysize
            );
            final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
            for (int i = 0; i < 2; i++) {
                // ----------------------------------------------------------- sign with private key
                try {
                    instance.initSign(keyPair.getPrivate());
                } catch (final InvalidKeyException ike) {
                    throw new TestAbortedException(ike.getMessage(), ike);
                }
                service().update(instance);
                final var signature = instance.sign();
                printf(keysize, null, i, signature);
                // ---------------------------------------------------------- verify with public key
                instance.initVerify(keyPair.getPublic());
                service().update(instance);
                final var verified = instance.verify(signature);
                // ---------------------------------------------------------------------------- then
                Assertions.assertTrue(verified);
            }
        }
    }

    @DisplayName("SHA256withDSA")
    @Nested
    class SHA256WithDSATest {

        private static final String KEY_PAIR_ALGORITHM = "DSA";

        private static final String SIGNATURE_ALGORITHM = "SHA256withDSA";

        @ValueSource(ints = {
                1024, 2048
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var keyPair = _Java_Security_TestUtils.generateKeyPair(
                    KEY_PAIR_ALGORITHM,
                    keysize
            );
            final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
            for (int i = 0; i < 2; i++) {
                // ----------------------------------------------------------- sign with private key
                try {
                    instance.initSign(keyPair.getPrivate());
                } catch (final InvalidKeyException ike) {
                    throw new TestAbortedException(ike.getMessage(), ike);
                }
                service().update(instance);
                final var signature = instance.sign();
                printf(keysize, i, signature);
                // ---------------------------------------------------------- verify with public key
                instance.initVerify(keyPair.getPublic());
                service().update(instance);
                final var verified = instance.verify(signature);
                // ---------------------------------------------------------------------------- then
                Assertions.assertTrue(verified);
            }
        }
    }

    @DisplayName("SHA256withECDSA")
    @Nested
    class SHA256WithECDSA_Test {

        private static final String KEY_PAIR_ALGORITHM = "EC";

        private static final String SIGNATURE_ALGORITHM = "SHA256withECDSA";

        private static final String CURVE_NAME = "secp256r1"; // Standard for P-256

        @Test
        void __() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var keyPairSpec = new ECGenParameterSpec(CURVE_NAME);
            final var keyPair = _Java_Security_TestUtils.generateKeyPair(
                    KEY_PAIR_ALGORITHM,
                    keyPairSpec
            );
            final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
            for (int i = 0; i < 2; i++) {
                // ----------------------------------------------------------- sign with private key
                instance.initSign(keyPair.getPrivate());
                service().update(instance);
                final var signature = instance.sign();
                printf(CURVE_NAME, null, i, signature);
                // ---------------------------------------------------------- verify with public key
                instance.initVerify(keyPair.getPublic());
                service().update(instance);
                final var verified = instance.verify(signature);
                // ---------------------------------------------------------------------------- then
                Assertions.assertTrue(verified);
            }
        }
    }

    @DisplayName("SHA384withECDSA")
    @Nested
    class SHA384withECDSA_Test {

        private static final String KEY_PAIR_ALGORITHM = "EC";

        private static final String SIGNATURE_ALGORITHM = "SHA384withECDSA";

        private static final String CURVE_NAME = "secp384r1";

        @Test
        void __() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var keyPairSpec = new ECGenParameterSpec(CURVE_NAME);
            final var keyPair = _Java_Security_TestUtils.generateKeyPair(
                    KEY_PAIR_ALGORITHM,
                    keyPairSpec
            );
            final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
            for (int i = 0; i < 2; i++) {
                // --------------------------------------------------------------- sign with private key
                instance.initSign(keyPair.getPrivate());
                service().update(instance);
                final var signature = instance.sign();
                printf(CURVE_NAME, null, i, signature);
                // -------------------------------------------------------------- verify with public key
                instance.initVerify(keyPair.getPublic());
                service().update(instance);
                final var verified = instance.verify(signature);
                // -------------------------------------------------------------------------------- then
                Assertions.assertTrue(verified);
            }
        }

        @Test
        void __file() throws Exception {
            final var file = HelloWorldTestUtils.writeSome(
                    File.createTempFile("tmp", null, tempDir)
            );
            // -------------------------------------------------------------------------------------
            final var keyPairSpec = new ECGenParameterSpec(CURVE_NAME);
            final PublicKey publicKey;
            final byte[] signature;
            {
                final var keyPair = _Java_Security_TestUtils.generateKeyPair(
                        KEY_PAIR_ALGORITHM,
                        keyPairSpec
                );
                publicKey = keyPair.getPublic();
                final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
                instance.initSign(keyPair.getPrivate());
                try (var channel = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {
                    for (final var b = ByteBuffer.allocate(128); channel.read(b.clear()) != -1; ) {
                        instance.update(b.flip());
                    }
                }
                signature = instance.sign();
            }
            {
                final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
                instance.initVerify(publicKey);
                try (var stream = new FileInputStream(file)) {
                    final var b = new byte[128];
                    for (int r; (r = stream.read(b)) != -1; ) {
                        instance.update(b, 0, r);
                    }
                }
                final var verified = instance.verify(signature);
                Assertions.assertTrue(verified);
            }
        }
    }

    @DisplayName("SHA1withRSA")
    @Nested
    class SHA1withRSA_Test {

        private static final String KEY_PAIR_ALGORITHM = "RSA";

        private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

        @ValueSource(ints = {
                1024, 2048, 3072, 4096
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var keyPair = _Java_Security_TestUtils.generateKeyPair(
                    KEY_PAIR_ALGORITHM,
                    keysize
            );
            final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
            for (int i = 0; i < 2; i++) {
                // ----------------------------------------------------------- sign with private key
                try {
                    instance.initSign(keyPair.getPrivate());
                } catch (final InvalidKeyException ike) {
                    throw new TestAbortedException(ike.getMessage(), ike);
                }
                service().update(instance);
                final var signature = instance.sign();
                printf(keysize, null, i, signature);
                // ---------------------------------------------------------- verify with public key
                instance.initVerify(keyPair.getPublic());
                service().update(instance);
                final var verified = instance.verify(signature);
                // ---------------------------------------------------------------------------- then
                Assertions.assertTrue(verified);
            }
        }
    }

    @DisplayName("SHA256withRSA")
    @Nested
    class SHA256withRSA_Test {

        private static final String KEY_PAIR_ALGORITHM = "RSA";

        private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

        @ValueSource(ints = {
                1024, 2048, 3072, 4096
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var keyPair = _Java_Security_TestUtils.generateKeyPair(
                    KEY_PAIR_ALGORITHM,
                    keysize
            );
            final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
            for (int i = 0; i < 2; i++) {
                // ----------------------------------------------------------- sign with private key
                try {
                    instance.initSign(keyPair.getPrivate());
                } catch (final InvalidKeyException ike) {
                    throw new TestAbortedException(ike.getMessage(), ike);
                }
                service().update(instance);
                final var signature = instance.sign();
                printf(keysize, null, i, signature);
                // ---------------------------------------------------------- verify with public key
                instance.initVerify(keyPair.getPublic());
                service().update(instance);
                final var verified = instance.verify(signature);
                // ---------------------------------------------------------------------------- then
                Assertions.assertTrue(verified);
            }
        }
    }

    @DisplayName("SHA384withRSA")
    @Nested
    class SHA384withRSA_Test {

        private static final String KEY_PAIR_ALGORITHM = "RSA";

        private static final String SIGNATURE_ALGORITHM = "SHA384withRSA";

        @ValueSource(ints = {
                1024, 2048, 3072, 4096
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var keyPair = _Java_Security_TestUtils.generateKeyPair(
                    KEY_PAIR_ALGORITHM,
                    keysize
            );
            // -------------------------------------------------------------------------------- when
            final var instance = Signature.getInstance(SIGNATURE_ALGORITHM);
            for (int i = 0; i < 2; i++) {
                // ----------------------------------------------------------- sign with private key
                try {
                    instance.initSign(keyPair.getPrivate());
                } catch (final InvalidKeyException ike) {
                    throw new TestAbortedException(ike.getMessage(), ike);
                }
                service().update(instance);
                final var signature = instance.sign();
                printf(keysize, null, i, signature);
                // ---------------------------------------------------------- verify with public key
                instance.initVerify(keyPair.getPublic());
                service().update(instance);
                final var verified = instance.verify(signature);
                // ---------------------------------------------------------------------------- then
                Assertions.assertTrue(verified);
            }
        }
    }
}
