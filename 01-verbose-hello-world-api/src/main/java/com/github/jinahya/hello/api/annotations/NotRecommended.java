package com.github.jinahya.hello.api.annotations;

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
 * A source-only marker for declarations whose algorithm / parameter / pattern is officially
 * discouraged by a standards body (NIST, IETF, IANA, BSI, &hellip;) or otherwise widely considered
 * obsolete &mdash; even when the underlying API still accepts it. The {@link #value()} carries a
 * short free-text qualifier (typically the citing document, e.g., {@code "NIST SP 800-56A r3"},
 * {@code "RFC 8247"}, {@code "FIPS 186-5"}) so each marker explains <em>why</em> the declaration is
 * flagged. The annotation is {@link Repeatable repeatable} via {@link NotRecommendedContainer}:
 * stack multiple {@code @NotRecommended} markers when more than one authority discourages the same
 * item.
 *
 * <p>Typical use is on test methods, constants, or factory members that intentionally cover a
 * legacy / non-mainstream option (e.g., RFC 2409 Oakley DH groups, {@code ECMQV}, sub-floor
 * Brainpool / Koblitz curves) so readers see at a glance that the case is retained for
 * <em>historical or interoperability reference</em>, not as a present-day recommendation. Unlike
 * {@link _LatestLTS} / {@link _LatestJDK} (which document <em>what the JDK platform provides</em>)
 * and {@link GovernedPreference} (which records the author's <em>editorial preference among valid
 * alternatives</em>), this marker reports an <em>external standards-body verdict</em>.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see NotRecommendedContainer
 * @see _LatestLTS
 * @see _LatestJDK
 * @see GovernedPreference
 * @see GovernedPreferenceContainer
 */
@Documented
@Repeatable(NotRecommendedContainer.class)
@Retention(RetentionPolicy.SOURCE)
public @interface NotRecommended {

    /**
     * A short free-text qualifier explaining why the annotated declaration is flagged &mdash;
     * typically the citing standards document or a brief reason (e.g.,
     * {@code "NIST SP 800-56A r3"}, {@code "IETF RFC 8247"},
     * {@code "below NIST 112-bit security floor"}).
     *
     * @return the qualifier; defaults to an empty string.
     */
    String value() default "";
}
