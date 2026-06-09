package com.github.jinahya.hello.api;

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

import java.lang.annotation.*;

/**
 * The {@link Repeatable} container annotation that holds multiple {@link GovernedPreference}
 * markers on the same element. Each contained marker records a preference rank under an
 * independent criterion (for example, one rank for general-interoperability use and another for a
 * specific policy domain). Parallel to {@link LatestLTSContainer} and {@link LatestJDKContainer},
 * which serve the same role for their respective markers.
 *
 * <p>Concrete sites where this container would surface &mdash; one rank set per KeyAgreement
 * family in {@code com.github.jinahya.hello.miscellaneous._Javax_Crypto_KeyAgreement__Test}:
 * <ul>
 *   <li>DiffieHellman (FFDH) group sources &mdash; {@code __DH} / {@code __DH1} / {@code __DH2} /
 *       {@code __DH3} / {@code __DH4} (ranks 1 → 5).</li>
 *   <li>ECDH curve families &mdash; {@code __ECDH} / {@code __ECDH1} / {@code __ECDH2} /
 *       {@code __ECDH3} / {@code __ECDH4} / {@code __ECDH5} (ranks 1 → 6).</li>
 *   <li>XDH API entry points &mdash; {@code __XDH} / {@code __XDH1} / {@code __XDH2}
 *       (ranks 1 → 3).</li>
 * </ul>
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see GovernedPreference
 * @see LatestLTS
 * @see LatestLTSContainer
 * @see LatestJDK
 * @see LatestJDKContainer
 */
@Documented
public @interface GovernedPreferenceContainer {

    /**
     * Returns the contained {@link GovernedPreference} markers.
     *
     * @return the contained {@link GovernedPreference} markers; never {@code null}.
     */
    GovernedPreference[] value();
}
