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
 * A source-only marker that records <em>where</em> a declaration's algorithm / parameter /
 * spec is documented within the JDK, ranked by how canonical the documentation is. The
 * {@link #value()} carries one of three positive integer tiers:
 * <ol>
 *   <li>specified in the JCA API spec / javadoc (e.g., {@code "DiffieHellman"} cited in
 *       {@link javax.crypto.KeyAgreement#getInstance(String)} javadoc, or the static
 *       {@link java.security.spec.NamedParameterSpec#X25519 NamedParameterSpec.X25519} /
 *       {@code .X448} constants &mdash; <strong>most canonical</strong>);</li>
 *   <li>listed in the <em>JCA Standard Algorithm Names</em> document &mdash; the JVM-supported
 *       standard names (e.g., {@code "ECDH"}, {@code "XDH"}, {@code "X25519"},
 *       {@code "secp256r1"});</li>
 *   <li>listed in a provider-specific section of the <em>JDK Providers Documentation</em> /
 *       Security Developer's Guide (e.g., Brainpool / non-NIST curve names exposed by BC).</li>
 * </ol>
 * Declarations whose algorithm / parameter is in none of the three (BC-only, niche standard
 * outside any JDK document) are simply left unannotated. The annotation is
 * {@link Repeatable repeatable} via {@link DocumentedPreferenceContainer}: stack multiple
 * {@code @DocumentedPreference} markers when a single declaration spans more than one tier.
 *
 * <p>Typical use is on test methods or constants that exercise a JDK-documented algorithm /
 * parameter, to flag at a glance whether the chosen name is the spec example, a standard JCA
 * name, or only a provider-specific name. Unlike {@link GovernedPreference} (which records the
 * author's <em>editorial</em> ranking among valid alternatives) and {@link LatestLTS} /
 * {@link LatestJDK} (which document which release the JDK platform provides the algorithm in),
 * this marker reports the <em>documentation tier</em> of the algorithm / parameter as published
 * by the JDK.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see DocumentedPreferenceContainer
 * @see GovernedPreference
 * @see GovernedPreferenceContainer
 * @see LatestLTS
 * @see LatestLTSContainer
 * @see LatestJDK
 * @see LatestJDKContainer
 */
@Documented
@Repeatable(DocumentedPreferenceContainer.class)
@Retention(RetentionPolicy.SOURCE)
public @interface DocumentedPreference {

    /**
     * The API-style preference rank of the annotated declaration. Lower is more preferred;
     * {@code 1} marks the recommended call shape. Must be positive.
     *
     * @return the API-style preference rank.
     */
    // TODO: validate that the value is positive
    int value();
}
