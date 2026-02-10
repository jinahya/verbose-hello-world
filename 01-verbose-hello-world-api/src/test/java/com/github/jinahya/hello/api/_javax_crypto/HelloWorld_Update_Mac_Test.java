package com.github.jinahya.hello.api._javax_crypto;

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.Key;
import java.util.HexFormat;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

/**
 * Tests for {@link com.github.jinahya.hello.api.HelloWorld#update(Mac)} method.
 *
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/Mac.html">javax.crypto.Mac</a>
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/specs/security/standard-names.html#mac-algorithms">Mac
 * Algorithms</a> (Java Security Standard Algorithm Names)
 */
@Slf4j
class HelloWorld_Update_Mac_Test extends HelloWorldTest {

    // https://docs.oracle.com/en/java/javase/25/docs/specs/security/standard-names.html#mac-algorithms
    // HmacMD5
    // HmacSHA1
    // HmacSHA224
    // HmacSHA256
    // HmacSHA384
    // HmacSHA512
    // HmacSHA512/224
    // HmacSHA512/256
    // HmacSHA3-224
    // HmacSHA3-256
    // HmacSHA3-384
    // HmacSHA3-512
    // ---------------------------------------------------------------------------------------------
    // PBEWithHmac<digest>
    // ---------------------------------------------------------------------------------------------
    // HmacPBESHA1
    // HmacPBESHA224
    // HmacPBESHA256
    // HmacPBESHA384
    // HmacPBESHA512
    // HmacPBESHA512/224
    // HmacPBESHA512/256

    private static Key generateSecretKey(final String algorithm) throws Exception {
        final var generator = KeyGenerator.getInstance(algorithm);
        return generator.generateKey();
    }

    private static Key generatePBESecretKey(final String keyFactoryAlgorithm) throws Exception {
        final var password = "password".toCharArray();
        final var keySpec = new PBEKeySpec(password);
        final var factory = SecretKeyFactory.getInstance(keyFactoryAlgorithm);
        return factory.generateSecret(keySpec);
    }

    private static PBEParameterSpec generatePBEParameterSpec() {
        final var salt = new byte[16];
        ThreadLocalRandom.current().nextBytes(salt);
        final var iterationCount = 1000;
        return new PBEParameterSpec(salt, iterationCount);
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() {
        HelloWorldTestUtils.stub_set_array_will_set_actual_hello_world_bytes(service());
    }

    // =============================================================================================
    // Hmac<digest>
    // =============================================================================================
    private static Stream<String> getHmacAlgorithmsStream() {
        return Stream.of(
                "HmacMD5",
                "HmacSHA1",
                "HmacSHA224",
                "HmacSHA256",
                "HmacSHA384",
                "HmacSHA512",
                "HmacSHA512/224",
                "HmacSHA512/256",
                "HmacSHA3-224",
                "HmacSHA3-256",
                "HmacSHA3-384",
                "HmacSHA3-512"
        );
    }

    @DisplayName("Hmac<digest>")
    @Nested
    class Hmac_Test {

        @MethodSource(
                "com.github.jinahya.hello.api._javax_crypto.HelloWorld_Update_Mac_Test#getHmacAlgorithmsStream")
        @ParameterizedTest
        void __(final String algorithm) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var key = generateSecretKey(algorithm);
            final var mac = Mac.getInstance(algorithm);
            mac.init(key);
            // -------------------------------------------------------------------------------- when
            final var result = service.update(mac);
            // -------------------------------------------------------------------------------- then
            Assertions.assertSame(mac, result);
            final var finalized = mac.doFinal();
            log.debug("{}: [{}] ({} bytes, {} bits)", algorithm,
                      HexFormat.of().formatHex(finalized), finalized.length >> 3, finalized.length);
        }
    }

    // =============================================================================================
    // PBEWithHmac<digest> / HmacPBE<digest>
    // =============================================================================================
    private static Stream<String> getPbeDigestsStream() {
        return Stream.of(
                "SHA1",
                "SHA224",
                "SHA256",
                "SHA384",
                "SHA512",
                "SHA512/224",
                "SHA512/256"
        );
    }

    @DisplayName("PBEWithHmac<digest>")
    @Nested
    class PBEWithHmac_Test {

        @MethodSource(
                "com.github.jinahya.hello.api._javax_crypto.HelloWorld_Update_Mac_Test#getPbeDigestsStream")
        @ParameterizedTest
        void __(final String digest) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var algorithm = "PBEWithHmac" + digest;
            final var keyFactoryAlgorithm = algorithm + "AndAES_256";
            final var key = generatePBESecretKey(keyFactoryAlgorithm);
            final var params = generatePBEParameterSpec();
            final var mac = Mac.getInstance(algorithm);
            mac.init(key, params);
            // -------------------------------------------------------------------------------- when
            final var result = service.update(mac);
            // -------------------------------------------------------------------------------- then
            Assertions.assertSame(mac, result);
            final var finalized = mac.doFinal();
            log.debug("{}: [{}] ({} bytes, {} bits)", algorithm,
                      HexFormat.of().formatHex(finalized), finalized.length >> 3, finalized.length);
        }
    }

    @DisplayName("HmacPBE<digest>")
    @Nested
    class HmacPBE_Test {

        @MethodSource(
                "com.github.jinahya.hello.api._javax_crypto.HelloWorld_Update_Mac_Test#getPbeDigestsStream")
        @ParameterizedTest
        void __(final String digest) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var algorithm = "HmacPBE" + digest;
            final var keyFactoryAlgorithm = "PBEWithHmac" + digest + "AndAES_256";
            final var key = generatePBESecretKey(keyFactoryAlgorithm);
            final var params = generatePBEParameterSpec();
            final var mac = Mac.getInstance(algorithm);
            mac.init(key, params);
            // -------------------------------------------------------------------------------- when
            final var result = service.update(mac);
            // -------------------------------------------------------------------------------- then
            Assertions.assertSame(mac, result);
            final var finalized = mac.doFinal();
            log.debug("{}: [{}] ({} bytes, {} bits)", algorithm,
                      HexFormat.of().formatHex(finalized), finalized.length >> 3, finalized.length);
        }
    }
}
