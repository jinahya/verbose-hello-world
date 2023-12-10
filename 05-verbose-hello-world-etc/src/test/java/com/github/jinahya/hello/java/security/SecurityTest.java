package com.github.jinahya.hello.java.security;

/*-
 * #%L
 * verbose-hello-world-api
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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.security.Security;
import java.util.Optional;

/**
 * A class for testing classes defined in {@link java.security} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://bit.ly/486gLAe">Java Security Standard Algorithm Names</a>
 * @see <a href="https://bit.ly/417yswU"><code>MesssageDigest</code> Algorithms</a>
 */
@DisplayName("java.security.Provider")
@Slf4j
class SecurityTest {

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//        Security.insertProviderAt(new gnu.crypto.jce.GnuCrypto(), 1);
//        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    @DisplayName("getProviders(filter)")
    @Nested
    class GetProvidersWithFilterTest {

        @ValueSource(strings = {
                "MessageDigest.MD5"
        })
        @ParameterizedTest
        void __(final String filter) {
            Optional.ofNullable(Security.getProviders(filter)).ifPresent(providers -> {
                for (final var provider : providers) {
                    log.debug("provider: {}", provider);
                }
            });
        }
    }

    @DisplayName("getProviders()")
    @Test
    void getProviders__() {
        for (final var provider : Security.getProviders()) {
            log.debug("provider: {}", provider);
            for (final var service : provider.getServices()) {
                log.debug("\tservice: {}", service);
            }
        }
    }
}
