package com.github.jinahya.hello.util;

/*-
 * #%L
 * verbose-hello-world-srv-basic
 * %%
 * Copyright (C) 2018 - 2024 Jinahya, Inc.
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
        void __Null() throws UnknownHostException {
            final var address = InetAddress.getByName(null);
            log.debug("address: {}", address);
            Assertions.assertTrue(address.isLoopbackAddress());
        }

        @DisplayName("getByName(zero-length)")
        @Test
        void __ZeroLength() throws UnknownHostException {
            final var address = InetAddress.getByName("");
            log.debug("address: {}", address);
        }

        @DisplayName("getByName(!blank)")
        @ValueSource(strings = {
                "sun.com",
                "java.sun.com",
                "microsoft.com",
                "www.microsoft.com"
        })
        @ParameterizedTest(name = "[{index}] host: \"{0}\"")
        void __(final String host) throws UnknownHostException {
            final var address = InetAddress.getByName(host);
            log.debug("address: {}", address);
        }
    }

    @DisplayName("getLoopbackAddress()")
    @Nested
    class LoopbackAddressTest {

        @DisplayName("getLoopbackAddress()")
        @Test
        void __() {
            final var address = InetAddress.getLoopbackAddress();
            log.debug("address: {}", address);
            Assertions.assertTrue(address.isLoopbackAddress());
        }

        @DisplayName("127.0.0.1/0:0:0:0:0:0:0:1/::1")
        @ValueSource(strings = {
                "localhost",
                "127.0.0.1",
                "0:0:0:0:0:0:0:1",
                "::1"
        })
        @ParameterizedTest(name = "[{index}] host: \"{0}\"")
        void __NotNull(final String host) throws UnknownHostException {
            final var address = InetAddress.getByName(host);
            log.debug("address: {}", address);
            log.debug("address.hostName: {}", address.getHostName());
            Assertions.assertTrue(address.isLoopbackAddress());
        }
    }
}
