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
 * The {@link Repeatable} container annotation that holds multiple {@link GovernedPreference}
 * markers on the same element. Each contained marker records a preference rank under an independent
 * criterion (for example, one rank for general-interoperability use and another for a specific
 * policy domain).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see GovernedPreference
 * @see _LatestLTS
 * @see _LatestJDK
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
