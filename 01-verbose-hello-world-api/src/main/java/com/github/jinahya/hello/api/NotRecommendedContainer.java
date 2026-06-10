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
 * The {@link Repeatable} container annotation that holds multiple {@link NotRecommended} markers on
 * the same element. Each contained marker records one standards-body verdict; stacking is useful
 * when more than one authority discourages the same algorithm / parameter (e.g., both NIST and IETF
 * flag the same DH group). Parallel to {@link LatestLTSContainer}, {@link LatestJDKContainer}, and
 * {@link GovernedPreferenceContainer}, which serve the same role for their respective markers.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see NotRecommended
 * @see LatestLTS
 * @see LatestLTSContainer
 * @see LatestJDK
 * @see LatestJDKContainer
 * @see GovernedPreference
 * @see GovernedPreferenceContainer
 */
@Documented
public @interface NotRecommendedContainer {

    /**
     * Returns the contained {@link NotRecommended} markers.
     *
     * @return the contained {@link NotRecommended} markers; never {@code null}.
     */
    NotRecommended[] value();
}
