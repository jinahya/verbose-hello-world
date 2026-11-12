package com.github.jinahya.hello.api.annotations;

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
 * alternatives. Unlike {@link _LatestLTS} and {@link _LatestJDK} &mdash; which document
 * <em>what the JDK platform itself requires</em> &mdash; this marker carries the author's
 * <em>editorial</em> recommendation among options that are all already valid.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see GovernedPreferenceContainer
 * @see _LatestLTS
 * @see _LatestJDK
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
