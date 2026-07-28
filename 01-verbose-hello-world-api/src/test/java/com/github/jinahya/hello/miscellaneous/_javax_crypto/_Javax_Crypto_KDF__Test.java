package com.github.jinahya.hello.miscellaneous._javax_crypto;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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

import com.github.jinahya.hello.api.annotations.*;
import com.github.jinahya.hello.miscellaneous._org_bouncycastle_jce_provider.*;
import lombok.*;
import org.junit.jupiter.api.*;

import javax.crypto.*;

/**
 * A placeholder exploration test class for {@link KDF}. The JDK 26 standard names spec lists only
 * three algorithms ({@code HKDF-SHA256} / {@code HKDF-SHA384} / {@code HKDF-SHA512}), all of which
 * are already covered by {@link _Javax_Crypto_KDF_Test}. There is no notable non-mandatory KDF
 * algorithm left to explore here, so this class is intentionally empty for now.
 *
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/specs/security/standard-names.html#kdf-algorithms">JDK
 * 26 JCA Standard Algorithm Names &mdash; KDF Algorithms</a>
 */
@_HideNameFromPublishing
@DisplayName("javax.crypto.KDF")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class _Javax_Crypto_KDF__Test {

    static {
        _org_bouncycastle_jce_provider__TestUtils.addBouncyCastleProvider();
    }
}
