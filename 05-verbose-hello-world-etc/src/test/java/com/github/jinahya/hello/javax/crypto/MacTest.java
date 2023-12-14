package com.github.jinahya.hello.javax.crypto;

import com.github.jinahya.hello.util.java.io.FileUtils;
import com.github.jinahya.hello.util.java.nio.file.PathUtils;
import com.github.jinahya.hello.util.javax.crypto.MacUtils;
import com.github.jinahya.hello.util.javax.crypto.spec.SecretKeySpecUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.crypto.Mac;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.HexFormat;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/21/docs/specs/security/standard-names.html#mac-algorithms"><code>Mac</code>
 * Algorithm</code></a>
 */
@Slf4j
class MacTest {

    static {
//        Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
        Security.insertProviderAt(new gnu.crypto.jce.GnuCrypto(), 1);
//        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    private static Stream<String> algorithmsRequiredToBeSupported() {
        return Stream.of(
                "HmacSHA1",
                "HmacSHA256"
        );
    }

    private static Stream<String> algorithms() {
        return Stream.of(
                "HmacMD5",
                // ---------------------------------------------------------------------------------
                "HmacSHA1", "HmacSHA224", "HmacSHA256", "HmacSHA384", "HmacSHA512",
                "HmacSHA512/224", "HmacSHA512/256", "HmacSHA3-224", "HmacSHA3-256", "HmacSHA3-384",
                "HmacSHA3-512",
                // ---------------------------------------------------------------------------------
                //"PBEWith<mac>",
                // ---------------------------------------------------------------------------------
                "HmacPBESHA1", "HmacPBESHA224", "HmacPBESHA256", "HmacPBESHA384", "HmacPBESHA512",
                "HmacPBESHA512/224", "HmacPBESHA512/256"
        );
    }

    private static Stream<String> algorithmPBEWith() {
        return algorithms().map(a -> "PBEWith" + a);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that the {@link Mac#getInstance(String)} method does not throw any exception when
     * invoked with an algorithm required to be supported.
     *
     * @param algorithm the algorithm required to be supported.
     * @see #algorithmsRequiredToBeSupported()
     */
    @DisplayName("getInstance(algorithm-required-to-be-supported)DoesNotThrow")
    @MethodSource({"algorithmsRequiredToBeSupported"})
    @ParameterizedTest(name = "[{index}] algorithm: {0}")
    void getInstance_DoesNotThrow_AlgorithmRequiredToBeSupported(final String algorithm) {
        assertThatCode(() -> {
            Mac.getInstance(algorithm);
        }).doesNotThrowAnyException();
    }

    @DisplayName("getInstance(algorithm)")
    @MethodSource({"algorithms"})
    @ParameterizedTest(name = "[{index}] algorithm: {0}")
    void getInstance__(final String algorithm) {
        try {
            final var instance = Mac.getInstance(algorithm);
            log.debug("supported; algorithm: {}, provider: {}", algorithm, instance.getProvider());
        } catch (final NoSuchAlgorithmException nsae) {
            log.error("not supported; algorithm: {}", algorithm, nsae);
        }
    }

    // ---------------------------------------------------------------------------------------------
    private static IntStream aesKeysizes() {
        return IntStream.of(
                128,
                192,
                256
        );
    }

    @DisplayName("HmacSHA1")
    @Nested
    class HmacSHA1Test {

        private static IntStream keysizes() {
            return aesKeysizes();
        }

        @DisplayName("__(keysize, dir)")
        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keysize, @TempDir final File dir)
                throws IOException, NoSuchAlgorithmException, InvalidKeyException {
            // ------------------------------------------------------------------------------- files
            final var file = File.createTempFile("tmp", "tmp", dir);
            FileUtils.writeRandomBytes(
                    file,
                    false,
                    ThreadLocalRandom.current().nextInt(8192),
                    new byte[1024]
            );
            log.debug("file.length: {}", file.length());
            // --------------------------------------------------------------------------------- mac
            final var algorithm = "HmacSHA1";
            final var mac = Mac.getInstance(algorithm);
            // --------------------------------------------------------------------------------- key
            final var key = SecretKeySpecUtils.newAesKey(keysize);
            // -------------------------------------------------------------------------------- mac1
            mac.init(key);
            final var mac1 = MacUtils.get(mac, file, new byte[1024]);
            log.debug("mac1: {}, algorithm: {}, keysize: {}", HexFormat.of().formatHex(mac1),
                      algorithm, keysize);
            assertThat(mac1).hasSize(mac.getMacLength());
            // -------------------------------------------------------------------------------- mac2
            mac.reset();
            final var mac2 = MacUtils.get(mac, file, new byte[1024]);
            log.debug("mac2: {}, algorithm: {}, keysize: {}", HexFormat.of().formatHex(mac2),
                      algorithm, keysize);
            // ------------------------------------------------------------------------------ verify
            assertThat(mac2).hasSize(mac.getMacLength()).isEqualTo(mac1);
        }

        @DisplayName("__(keysize, dir)")
        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keysize, @TempDir final Path dir)
                throws IOException, NoSuchAlgorithmException, InvalidKeyException {
            // ------------------------------------------------------------------------------- paths
            final var path = Files.createTempFile(dir, null, null);
            PathUtils.writeRandomBytes(
                    path,
                    ThreadLocalRandom.current().nextInt(8192),
                    ByteBuffer.allocate(1024),
                    0L
            );
            // --------------------------------------------------------------------------------- mac
            final var algorithm = "HmacSHA1";
            final var mac = Mac.getInstance(algorithm);
            log.debug("provider: {}", mac.getProvider());
            // --------------------------------------------------------------------------------- key
            final var key = SecretKeySpecUtils.newAesKey(keysize);
            // -------------------------------------------------------------------------------- mac1
            mac.init(key);
            final var mac1 = MacUtils.get(mac, path, ByteBuffer.allocate(1024));
            assertThat(mac1).hasSize(mac.getMacLength());
            log.debug("mac1: {}, algorithm: {}, keysize: {}", HexFormat.of().formatHex(mac1),
                      algorithm, keysize);
            // -------------------------------------------------------------------------------- mac2
            mac.reset();
            final var mac2 = MacUtils.get(mac, path, ByteBuffer.allocate(1024));
            log.debug("mac2: {}, algorithm: {}, keysize: {}", HexFormat.of().formatHex(mac2),
                      algorithm, keysize);
            // ------------------------------------------------------------------------------ verify
            assertThat(mac2).hasSize(mac.getMacLength()).isEqualTo(mac1);
        }
    }

    @DisplayName("HmacSHA256")
    @Nested
    class HmacSHA256Test {

        private static IntStream keysizes() {
            return aesKeysizes();
        }

        @DisplayName("__(keysize, dir)")
        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keysize, @TempDir final File dir)
                throws IOException, NoSuchAlgorithmException, InvalidKeyException {
            // ------------------------------------------------------------------------------- files
            final var file = File.createTempFile("tmp", "tmp", dir);
            FileUtils.writeRandomBytes(
                    file,
                    false,
                    ThreadLocalRandom.current().nextInt(8192),
                    new byte[1024]
            );
            log.debug("file.length: {}", file.length());
            // --------------------------------------------------------------------------------- mac
            final var algorithm = "HmacSHA256";
            final var mac = Mac.getInstance(algorithm);
            log.debug("provider: {}", mac.getProvider());
            // --------------------------------------------------------------------------------- key
            final var key = SecretKeySpecUtils.newAesKey(keysize);
            // -------------------------------------------------------------------------------- mac1
            mac.init(key);
            final var mac1 = MacUtils.get(mac, file, new byte[1024]);
            log.debug("mac1: {}, algorithm: {}, keysize: {}", HexFormat.of().formatHex(mac1),
                      algorithm, keysize);
            assertThat(mac1).hasSize(mac.getMacLength());
            // -------------------------------------------------------------------------------- mac2
            mac.reset();
            final var mac2 = MacUtils.get(mac, file, new byte[1024]);
            log.debug("mac2: {}, algorithm: {}, keysize: {}", HexFormat.of().formatHex(mac2),
                      algorithm, keysize);
            // ------------------------------------------------------------------------------ verify
            assertThat(mac2).hasSize(mac.getMacLength()).isEqualTo(mac1);
        }

        @DisplayName("__(keysize, dir)")
        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keysize, @TempDir final Path dir)
                throws IOException, NoSuchAlgorithmException, InvalidKeyException {
            // ------------------------------------------------------------------------------- paths
            final var path = Files.createTempFile(dir, null, null);
            PathUtils.writeRandomBytes(
                    path,
                    ThreadLocalRandom.current().nextInt(8192),
                    ByteBuffer.allocate(1024),
                    0L
            );
            // --------------------------------------------------------------------------------- mac
            final var algorithm = "HmacSHA256";
            final var mac = Mac.getInstance(algorithm);
            // --------------------------------------------------------------------------------- key
            final var key = SecretKeySpecUtils.newAesKey(keysize);
            // -------------------------------------------------------------------------------- mac1
            mac.init(key);
            final var mac1 = MacUtils.get(mac, path, ByteBuffer.allocate(1024));
            assertThat(mac1).hasSize(mac.getMacLength());
            log.debug("mac1: {}, algorithm: {}, keysize: {}", HexFormat.of().formatHex(mac1),
                      algorithm, keysize);
            // -------------------------------------------------------------------------------- mac2
            mac.reset();
            final var mac2 = MacUtils.get(mac, path, ByteBuffer.allocate(1024));
            log.debug("mac2: {}, algorithm: {}, keysize: {}", HexFormat.of().formatHex(mac2),
                      algorithm, keysize);
            // ------------------------------------------------------------------------------ verify
            assertThat(mac2).hasSize(mac.getMacLength()).isEqualTo(mac1);
        }
    }
}
