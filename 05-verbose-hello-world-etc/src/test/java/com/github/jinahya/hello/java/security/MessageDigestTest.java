package com.github.jinahya.hello.java.security;

/*-
 * #%L
 * verbose-hello-world-etc
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.github.jinahya.hello.util.java.io.FileUtils;
import com.github.jinahya.hello.util.java.nio.file.PathUtils;
import com.github.jinahya.hello.util.java.security.MessageDigestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
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
        Security.addProvider(new gnu.crypto.jce.GnuCrypto());
//        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    static Stream<String> algorithmsRequiredToBeSupported() {
        return Stream.of("SHA-1", "SHA-256");
    }

    static Stream<Arguments> algorithmsRequiredToBeSupportedWithProviders() {
        return algorithmsRequiredToBeSupported()
                .flatMap(a -> Arrays.stream(MessageDigestUtils.getProviders(a))
                        .map(p -> Arguments.of(a, p)));
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
                .flatMap(a -> Arrays.stream(MessageDigestUtils.getProviders(a))
                        .map(p -> Arguments.of(a, p)));
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
    @DisplayName("digest(algorithm, provider)")
    @MethodSource({"algorithmsWithProviders"})
    @ParameterizedTest(name = "[{index}] algorithm: {0}, provider: {1}")
    void __(final String algorithm, final Provider provider, @TempDir final File dir)
            throws IOException, NoSuchAlgorithmException {
        final var file = File.createTempFile("tmp", "tmp", dir);
        FileUtils.writeRandomBytes(
                file,
                false,
                ThreadLocalRandom.current().nextInt(8192),
                new byte[1024]
        );
        final var instance = MessageDigest.getInstance(algorithm, provider);
        assertThat(instance.getProvider()).isSameAs(provider);
        final var digest = MessageDigestUtils.getDigest(algorithm, file, new byte[1024]);
        log.debug("algorithm: {}, provider: {}, digest: {}", algorithm, provider,
                  HexFormat.of().formatHex(instance.digest()));
    }

    @DisplayName("digest(algorithm, provider)")
    @MethodSource({"algorithmsWithProviders"})
    @ParameterizedTest(name = "[{index}] algorithm: {0}, provider: {1}")
    void __(final String algorithm, final Provider provider, @TempDir final Path dir)
            throws IOException, NoSuchAlgorithmException {
        final var path = Files.createTempFile(dir, null, null);
        PathUtils.writeRandomBytes(
                path,
                ThreadLocalRandom.current().nextInt(8192),
                ByteBuffer.allocate(1024),
                0L
        );
        final var instance = MessageDigest.getInstance(algorithm, provider);
        assertThat(instance.getProvider()).isSameAs(provider);
        final var digest = MessageDigestUtils.getDigest(algorithm, path, ByteBuffer.allocate(1024));
        log.debug("algorithm: {}, provider: {}, digest: {}",
                  algorithm, provider, HexFormat.of().formatHex(digest));
    }
}
