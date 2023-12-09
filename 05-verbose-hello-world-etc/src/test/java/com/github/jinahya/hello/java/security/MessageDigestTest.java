package com.github.jinahya.hello.java.security;

import com.github.jinahya.hello.util.JavaIoUtils;
import com.github.jinahya.hello.util.JavaNioUtils;
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
import static org.junit.jupiter.params.provider.Arguments.arguments;

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

    static Stream<Provider> providers() {
        final var type = MessageDigest.class.getSimpleName();
        return _SecurityTestUtils.providers()
                .filter(p -> p.getServices().stream().anyMatch(s -> s.getType().equals(type)));
    }

    static Stream<String> algorithmsRequiredToBeSupported() {
        return Stream.of("SHA-1", "SHA-256");
    }

    static Stream<String> algorithms() {
        return Stream.of(
                "MD2",
                "MD5",
                "SHA-1", "SHA-224", "SHA-256", "SHA-384", "SHA-512/224", "SHA-512/256",
                "SHA3-224", "SHA3-256", "SHA3-384", "SHA3-512"
        );
    }

    private static Stream<Arguments> providersAndRequiredToBeSupportedAlgorithms() {
        return providers()
                .flatMap(p -> algorithmsRequiredToBeSupported().map(a -> arguments(p, a)));
    }

    private static Stream<Arguments> providersAndAlgorithms() {
        return providers()
                .flatMap(p -> algorithms().map(a -> arguments(p, a)));
    }

    @DisplayName("getInstance(required-to-be-supported)DoesNotThrow")
    @MethodSource({"providersAndRequiredToBeSupportedAlgorithms"})
    @ParameterizedTest
    void getInstance_DoesNotThrow_RequiredToBeSupported(final Provider provider,
                                                        final String algorithm) {
        assertDoesNotThrow(() -> MessageDigest.getInstance(algorithm, provider));
    }

    @DisplayName("getInstance(algorithm)")
    @MethodSource({"providersAndAlgorithms"})
    @ParameterizedTest
    void getInstance__(final Provider provider, final String algorithm) {
        try {
            final var instance = MessageDigest.getInstance(algorithm, provider);
            log.debug("supported; provider: {}, algorithm: {}", provider, algorithm);
        } catch (final NoSuchAlgorithmException nsae) {
            log.warn("not supported: provider: {}, algorithm: {}", provider, algorithm, nsae);
            return;
        }
    }

    @DisplayName("digest(required-to-be-supported-algorithm, provider)")
    @Test
    void digest__(@TempDir final File dir)
            throws IOException, NoSuchAlgorithmException {
        final var file = JavaIoUtils.createTempFileInAndWriteSome(dir);
        final var array = new byte[1024];
        final Iterable<String> algorithms = () -> algorithmsRequiredToBeSupported().iterator();
        final Iterable<Provider> providers = () -> providers().iterator();
        for (final var algorithm : algorithms) {
            for (final var provider : providers) {
                final var instance = MessageDigest.getInstance(algorithm, provider);
                try (var stream = new FileInputStream(file)) {
                    for (int r; (r = stream.read(array)) != -1; ) {
                        instance.update(array, 0, r);
                    }
                }
                log.debug("digest: {}, algorithm: {}, provider: {}",
                          HexFormat.of().formatHex(instance.digest()), algorithm, provider);
            }
        }
    }

    @DisplayName("digest(required-to-be-supported-algorithm, provider)")
    @Test
    void digest__(@TempDir final Path dir)
            throws IOException, NoSuchAlgorithmException {
        final var path = JavaNioUtils.createTempFileInAndWriteSome(dir);
        final var buffer = ByteBuffer.allocate(1024);
        final Iterable<String> algorithms = () -> algorithmsRequiredToBeSupported().iterator();
        final Iterable<Provider> providers = () -> providers().iterator();
        for (final var algorithm : algorithms) {
            for (final var provider : providers) {
                final var instance = MessageDigest.getInstance(algorithm, provider);
                try (var channel = FileChannel.open(path, StandardOpenOption.READ)) {
                    if (channel.read(buffer.clear()) == -1) {
                        break;
                    }
                    instance.update(buffer.flip());
                }
                log.debug("digest: {}, algorithm: {}, provider: {}",
                          HexFormat.of().formatHex(instance.digest()), algorithm, provider);
            }
        }
    }
}
