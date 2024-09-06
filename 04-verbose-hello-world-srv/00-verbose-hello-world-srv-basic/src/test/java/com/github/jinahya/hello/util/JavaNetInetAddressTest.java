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

        @DisplayName("getByName(blank)")
        @ValueSource(strings = {
                "",
                " "
        })
        @ParameterizedTest(name = "[{index}] hostName: \"{0}\"")
        void __Blank(final String hostName) {
            try {
                final var host = InetAddress.getByName(hostName);
                log.debug("getByName(null): {}", host);
            } catch (final UnknownHostException uhe) {
                log.error("failed to get InetAddress for '{}'", hostName, uhe);
            }
        }

        @DisplayName("getByName(!blank)")
        @ValueSource(strings = {
                "sun.com",
                "java.sun.com",
                "microsoft.com",
                "www.microsoft.com"
        })
        @ParameterizedTest(name = "[{index}] hostName: \"{0}\"")
        void __(final String hostName) {
            try {
                final var host = InetAddress.getByName(hostName);
                log.debug("host {}", host);
            } catch (final UnknownHostException uhe) {
                log.error("failed to get InetAddress for '{}'", hostName, uhe);
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
            Assertions.assertTrue(loopbackAddress.isLoopbackAddress());
        }

        @DisplayName("127.0.0.1/0:0:0:0:0:0:0:1/::1")
        @ValueSource(strings = {
                "localhost",
                "127.0.0.1",
                "0:0:0:0:0:0:0:1",
                "::1"
        })
        @ParameterizedTest(name = "[{index}] hostName: \"{0}\"")
        void __NotNull(final String hostName) {
            try {
                final var host = InetAddress.getByName(hostName);
                log.debug("host: {}", host);
                log.debug("hostName: {}", host.getHostName());
                Assertions.assertTrue(host.isLoopbackAddress());
            } catch (final UnknownHostException uhe) {
                log.error("failed to get InetAddress by name: '{}'", hostName, uhe);
            }
        }
    }
}
