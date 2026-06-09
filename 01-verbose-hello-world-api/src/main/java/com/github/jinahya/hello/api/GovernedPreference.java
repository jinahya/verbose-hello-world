package com.github.jinahya.hello.api;

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

import java.lang.annotation.*;

/**
 * A source-only marker that records the practical preference ranking of a declaration among
 * alternatives that target the same primitive / scenario. The {@link #value()} carries a single
 * positive integer rank &mdash; {@code 1} for the most preferred (the recommended default in modern
 * practice), {@code 2} for the next, and so on. The annotation is {@link Repeatable repeatable} via
 * {@link GovernedPreferenceContainer}: stack multiple {@code @GovernedPreference} markers on the
 * same element to record ranks under more than one independent criterion (for example,
 * {@code @GovernedPreference(1) @GovernedPreference(3)} on an ECDH curve = first-choice for general
 * TLS / FIPS interoperability but third-choice when scoped to a specific policy domain).
 *
 * <p>Typical use is on test methods, constants, or factory members that enumerate competing ways
 * of reaching the same end (DH group sources, ECDH curve families, XDH API entry points, cipher
 * transformation variants, &hellip;) to flag which one is the modern default versus legacy / niche
 * alternatives. Unlike {@link LatestLTS} and {@link LatestJDK} &mdash; which document
 * <em>what the JDK platform itself requires</em> &mdash; this marker carries the author's
 * <em>editorial</em> recommendation among options that are all already valid.
 *
 * <p>Concrete usages in this codebase &mdash; ranked sets of competing options within one
 * KeyAgreement family:
 * <ul>
 *   <li>DiffieHellman (FFDH) group sources: {@code __DH} (RFC 7919 FFDHE,
 *       {@code @GovernedPreference(1)}) &gt; {@code __DH1} (RFC 3526,
 *       {@code @GovernedPreference(2)}) &gt; {@code __DH2} (Alice picks, Bob inherits,
 *       {@code (3)}) &gt; {@code __DH3} (fresh {@code AlgorithmParameterGenerator}, {@code (4)})
 *       &gt; {@code __DH4} (RFC 2409 Oakley, {@code (5)}).</li>
 *   <li>ECDH curve families: {@code __ECDH} (NIST P-curves, {@code @GovernedPreference(1)}) &gt;
 *       {@code __ECDH1} (SEC Koblitz, {@code (2)}) &gt; {@code __ECDH2} (Brainpool RFC 5639,
 *       {@code (3)}) &gt; {@code __ECDH3} (Brainpool twisted, {@code (4)}) &gt; {@code __ECDH4}
 *       (ANSI X9.62, {@code (5)}) &gt; {@code __ECDH5} (SM2, {@code (6)}).</li>
 *   <li>XDH API entry points: {@code __XDH}
 *       ({@code KeyPairGenerator.getInstance("XDH")} + {@link java.security.spec.NamedParameterSpec}
 *       static constants, {@code @GovernedPreference(1)}) &gt; {@code __XDH1} (same umbrella with
 *       {@code new NamedParameterSpec(stdName)}, {@code (2)}) &gt; {@code __XDH2} (curve name as
 *       algorithm name, {@code (3)}).</li>
 * </ul>
 * All three sets live in
 * {@code com.github.jinahya.hello.miscellaneous._Javax_Crypto_KeyAgreement__Test}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see GovernedPreferenceContainer
 * @see LatestLTS
 * @see LatestLTSContainer
 * @see LatestJDK
 * @see LatestJDKContainer
 */
@Documented
@Repeatable(GovernedPreferenceContainer.class)
@Retention(RetentionPolicy.SOURCE)
public @interface GovernedPreference {

    /**
     * The practical preference rank of the annotated declaration. Lower is more preferred;
     * {@code 1} marks the recommended default. Must be positive.
     *
     * @return the practical preference rank.
     */
    // TODO: validate that the value is positive
    int value();
}
