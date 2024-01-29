package com.github.jinahya.hello.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.InetAddress;
import java.net.UnknownHostException;

@DisplayName("java.net.InetAddress")
@Slf4j
class JavaNetInetAddressTest {

    @DisplayName("getByName(host)")
    @Nested
    class GetByNameTest {

        @DisplayName("getByName(null)")
        @Test
        void __Null() {
            try {
                final var host = InetAddress.getByName(null);
                log.debug("getByName(null): {}", host);
            } catch (final UnknownHostException uhe) {
                log.error("failed to get InetAddress for null", uhe);
            }
        }

        @DisplayName("getByName(empty)")
        @ValueSource(strings = {
                "",
                " "
        })
        @ParameterizedTest
        void __Empty(final String name) {
            try {
                final var host = InetAddress.getByName(name);
                log.debug("getByName(null): {}", host);
            } catch (final UnknownHostException uhe) {
                log.error("failed to get InetAddress for '{}'}", name, uhe);
            }
        }

        @DisplayName("getByName(non-null)")
        @ValueSource(strings = {
                "",
                "sun.com",
                "java.sun.com",
                "microsoft.com",
                "www.microsoft.com"
        })
        @ParameterizedTest
        void __(final String name) {
            try {
                final var host = InetAddress.getByName(name);
                log.debug("getByName(\"{}\"): {}", name, host);
            } catch (final UnknownHostException uhe) {
                log.error("failed to get InetAddress for `{}'", name, uhe);
            }
        }
    }

    @DisplayName("getLoopbackAddress()")
    @Nested
    class LoopbackAddressTest {

        @DisplayName("getLoopbackAddress()")
        @Test
        void __() {
            final var loopbackAddress = InetAddress.getLoopbackAddress();
            log.debug("loopbackAddress: {}", loopbackAddress);
        }

        @DisplayName("127.0.0.1/0:0:0:0:0:0:0:1/::1")
        @ValueSource(strings = {
                "127.0.0.1",
                "0:0:0:0:0:0:0:1",
                "::1"
        })
        @ParameterizedTest
        void __NotNull(final String hostName) {
            try {
                final var host = InetAddress.getByName(hostName);
                Assertions.assertTrue(host.isLoopbackAddress());
            } catch (final UnknownHostException uhe) {
                log.error("failed to get InetAddress for '{}'", hostName, uhe);
            }
        }
    }
}
