package com.github.jinahya.hello.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.SocketException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class JavaNetUtilsTest {

    @DisplayName("acceptInetAddress")
    @Nested
    class AcceptInetAddressesTest {

        @DisplayName("loopbackAddresses")
        @Test
        void __() throws SocketException {
            JavaNetUtils.acceptInetAddresses(
                    ni -> {
                        return true;
                    },
                    (ni, ia) -> {
                        return ia.isLoopbackAddress();
                    },
                    (ni, ia) -> {
                        log.debug("networkInterface: {}, inetAddress: {}", ni, ia);
                    }
            );
        }
    }

    @DisplayName("loopbackAddressStream")
    @Nested
    class LoopbackAddressStreamTest {

        @DisplayName("loopbackAddresses")
        @Test
        void __() throws SocketException {
            JavaNetUtils.loopbackAddressStream(ni -> true).forEach(la -> {
                log.debug("loopbackAddress: {}, {}", la, la.getClass());
                assertThat(la.isLoopbackAddress()).isTrue();
            });
        }

        @DisplayName("loopbackAddressesIPv4")
        @Test
        void __ipv4() throws SocketException {
            JavaNetUtils.loopbackAddressStreamIPv4(ni -> true).forEach(la -> {
                log.debug("loopbackAddressIPv4: {}, {}", la, la.getClass());
                assertThat(la.isLoopbackAddress()).isTrue();
                assertThat(la).isInstanceOf(Inet4Address.class);
            });
        }

        @DisplayName("loopbackAddressesIPv6")
        @Test
        void __ipv6() throws SocketException {
            JavaNetUtils.loopbackAddressStreamIPv6(ni -> true).forEach(la -> {
                log.debug("loopbackAddressIPv6: {}, {}", la, la.getClass());
                assertThat(la.isLoopbackAddress()).isTrue();
                assertThat(la).isInstanceOf(Inet6Address.class);
            });
        }
    }

    @DisplayName("applyFirstLoopbackAddress")
    @Nested
    class ApplyFirstLoopbackAddressTest {

        @DisplayName("applyFirstLoopbackAddressIPv4")
        @Nested
        class ApplyFirstLoopbackAddressIPv4Test {

            @Test
            void __() throws SocketException {
                JavaNetUtils.applyFirstLoopbackAddressIPv4(ia -> {
                    assertThat(ia).isInstanceOf(Inet4Address.class);
                    return null;
                });
            }
        }

        @DisplayName("applyFirstLoopbackAddressIPv6")
        @Nested
        class ApplyFirstLoopbackAddressIPv6Test {

            @Test
            void __() throws SocketException {
                JavaNetUtils.applyFirstLoopbackAddressIPv6(ia -> {
                    assertThat(ia).isInstanceOf(Inet6Address.class);
                    return null;
                });
            }
        }
    }
}
