package com.github.jinahya.hello.javax.crypto;

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

import com.github.jinahya.hello.java.security._JavaSecurityTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;

import javax.crypto.Cipher;
import java.security.Provider;
import java.util.stream.Stream;

/**
 * A class for testing classes defined in {@link java.security} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://bit.ly/486gLAe">Java Security Standard Algorithm Names</a>
 * @see <a href="https://bit.ly/417yswU"><code>MesssageDigest</code> Algorithms</a>
 */
@DisplayName("javax.crypto.Cipher")
@Slf4j
class _JavaxCryptoTestUtils {

    static Stream<Provider> providers() {
        final var type = Cipher.class.getSimpleName();
        return _JavaSecurityTestUtils.providers()
                .filter(p -> p.getServices().stream().anyMatch(s -> s.getType().equals(type)));
    }
}
