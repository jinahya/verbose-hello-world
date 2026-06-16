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
 * The {@link Repeatable} container annotation that holds multiple {@link DocumentedPreference}
 * markers on the same element. Each contained marker records an API-style rank under an independent
 * criterion (for example, one rank for type-safety and another for migration cost). Parallel to
 * {@link GovernedPreferenceContainer}, which serves the same role for its respective marker.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see DocumentedPreference
 * @see GovernedPreference
 * @see GovernedPreferenceContainer
 * @see _LatestLTS
 * @see _LatestJDK
 */
@Documented
public @interface DocumentedPreferenceContainer {

    /**
     * Returns the contained {@link DocumentedPreference} markers.
     *
     * @return the contained {@link DocumentedPreference} markers; never {@code null}.
     */
    DocumentedPreference[] value();
}
