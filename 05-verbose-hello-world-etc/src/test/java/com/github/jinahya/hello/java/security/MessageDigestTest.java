package com.github.jinahya.hello.java.security;

import com.github.jinahya.hello.util.java.io._JavaIoUtils;
import com.github.jinahya.hello.util.java.nio.JavaNioUtils;
import com.github.jinahya.hello.util.java.security.MessageDigestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * A class for testing {@link MessageDigest}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/21/docs/specs/security/standard-names.html#messagedigest-algorithms"><code>MesssageDigest</code>
 * Algorithms</a> (Java Security Standard Algorithm Names)
 */
@DisplayName("MessageDigest")
@Slf4j
class MessageDigestTest {

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//        Security.insertProviderAt(new gnu.crypto.jce.GnuCrypto(), 1);
//        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    static Stream<String> algorithmsRequiredToBeSupported() {
        return Stream.of("SHA-1", "SHA-256");
    }

    static Stream<Arguments> algorithmsRequiredToBeSupportedWithProviders() {
        return algorithmsRequiredToBeSupported()
                .flatMap(a -> {
                    return Arrays.stream(MessageDigestUtils.getProviders(a))
                            .map(p -> Arguments.of(a, p));
                });
    }

    static Stream<String> algorithms() {
        return Stream.of(
                "MD2",
                "MD5",
                "SHA-1", "SHA-224", "SHA-256", "SHA-384", "SHA-512/224", "SHA-512/256",
                "SHA3-224", "SHA3-256", "SHA3-384", "SHA3-512"
        );
    }

    static Stream<Arguments> algorithmsWithProviders() {
        return algorithms()
                .flatMap(a -> {
                    return Arrays.stream(MessageDigestUtils.getProviders(a))
                            .map(p -> Arguments.of(a, p));
                });
    }

    @DisplayName("getInstance(algorithm-required-to-be-supported)DoesNotThrow")
    @MethodSource({"algorithmsRequiredToBeSupported"})
    @ParameterizedTest
    void getInstance_DoesNotThrow_RequiredToBeSupported(final String algorithm) {
        assertDoesNotThrow(() -> MessageDigest.getInstance(algorithm));
    }

    @DisplayName("getInstance(algorithm-required-to-be-supported, provider)DoesNotThrow")
    @MethodSource({"algorithmsRequiredToBeSupportedWithProviders"})
    @ParameterizedTest
    void getInstance_DoesNotThrow_RequiredToBeSupported(final String algorithm,
                                                        final Provider provider) {
        assertDoesNotThrow(() -> MessageDigest.getInstance(algorithm, provider));
    }

    @DisplayName("getInstance(algorithm)")
    @MethodSource({"algorithms"})
    @ParameterizedTest
    void getInstance__(final String algorithm) {
        try {
            final var instance = MessageDigest.getInstance(algorithm);
            log.debug("supported; algorithm: {}, provider: {}", algorithm, instance.getProvider());
        } catch (final NoSuchAlgorithmException nsae) {
            log.warn("not supported: algorithm: {}", algorithm, nsae);
            return;
        }
    }

    @DisplayName("getInstance(algorithm, provider)")
    @MethodSource({"algorithmsWithProviders"})
    @ParameterizedTest(name = "[{index}] algorithm: {0}, provider: {1}")
    void getInstance__(final String algorithm, final Provider provider) {
        try {
            final var instance = MessageDigest.getInstance(algorithm, provider);
            log.debug("supported; algorithm: {}, provider: {}", algorithm, provider);
        } catch (final NoSuchAlgorithmException nsae) {
            log.warn("not supported: algorithm: {}, provider: {}", algorithm, provider, nsae);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("digest(algorithm-required-to-be-supported)")
    @Test
    void __(@TempDir final File dir)
            throws IOException, NoSuchAlgorithmException {
        final var file = _JavaIoUtils.createTempFileInAndWriteSome(dir);
        final var array = new byte[1024];
        final Iterable<String> algorithms = () -> algorithmsRequiredToBeSupported().iterator();
        for (final var algorithm : algorithms) {
            final var instance = MessageDigest.getInstance(algorithm);
            try (var stream = new FileInputStream(file)) {
                for (int r; (r = stream.read(array)) != -1; ) {
                    instance.update(array, 0, r);
                }
            }
            log.debug("digest: {}, algorithm: {}, provider: {}",
                      HexFormat.of().formatHex(instance.digest()), algorithm,
                      instance.getProvider());
        }
    }

    @DisplayName("digest(algorithm-required-to-be-supported)")
    @Test
    void __(@TempDir final Path dir)
            throws IOException, NoSuchAlgorithmException {
        final var path = JavaNioUtils.createTempFileInAndWriteSome(dir);
        final var buffer = ByteBuffer.allocate(1024);
        final Iterable<String> algorithms = () -> algorithmsRequiredToBeSupported().iterator();
        for (final var algorithm : algorithms) {
            final var instance = MessageDigest.getInstance(algorithm);
            try (var channel = FileChannel.open(path, StandardOpenOption.READ)) {
                if (channel.read(buffer.clear()) == -1) {
                    break;
                }
                instance.update(buffer.flip());
            }
            log.debug("digest: {}, algorithm: {}, provider: {}",
                      HexFormat.of().formatHex(instance.digest()), algorithm,
                      instance.getProvider());
        }
    }
}
