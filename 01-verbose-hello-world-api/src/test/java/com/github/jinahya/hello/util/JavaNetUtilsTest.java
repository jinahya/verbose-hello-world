package com.github.jinahya.hello.util;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

import static org.assertj.core.api.Assertions.assertThat;

class JavaNetUtilsTest {

    @Nested
    class ApplyLoopbackAddressesTest {

        @Test
        void __() {
            JavaNetUtils.applyLoopbackAddresses((ipv4, ipv6) -> {
                assertThat(ipv4)
                        .isInstanceOf(Inet4Address.class)
                        .extracting(InetAddress::isLoopbackAddress,
                                    InstanceOfAssertFactories.BOOLEAN).isTrue();
                assertThat(ipv6)
                        .isInstanceOf(Inet6Address.class)
                        .extracting(InetAddress::isLoopbackAddress,
                                    InstanceOfAssertFactories.BOOLEAN).isTrue();
                return null;
            });
        }
    }
}
