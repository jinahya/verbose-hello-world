package com.github.jinahya.hello.api._java_security;

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

@畵蛇添足
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Update_MessageDigest_畵蛇添足_Test
        extends HelloWorldTest {

    static final List<String> ALGORITHMS = List.of(
            "SHA-1",
            "SHA-256",
            "SHA-384"
    );

    static List<String> algorithms() {
        return ALGORITHMS;
    }

    @MethodSource({"algorithms"})
    @ParameterizedTest
    void __(final String algorithm) throws NoSuchAlgorithmException {
        final var service = HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(service());
        final var digest = MessageDigest.getInstance(algorithm);
        final var digested = service.update(digest).digest();
        System.out.printf("%10s: %s (%d)%n", algorithm, HexFormat.of().formatHex(digested),
                          digested.length);
    }
}
