package com.github.jinahya.hello.api.util;

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
 * A documentation-only marker indicating that the annotated type, constructor, or method is
 * intentionally excluded from coverage measurements, with the {@link #value() value} carrying a
 * short reason. Retained at {@link RetentionPolicy#CLASS} since coverage tooling reads it from the
 * compiled classfiles (not via reflection at runtime).
 * <p>
 * For the two common, narrower exclusion shapes, prefer the dedicated markers:
 * {@link _ExcludeFromCoverage_FailingCase} (a test method that asserts a failure path) and
 * {@link _ExcludeFromCoverage_PrivateConstructor_Obviously} (the conventional uninstantiable
 * private constructor of a utility class). Use this generic marker when neither fits.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see _ExcludeFromCoverage_FailingCase
 * @see _ExcludeFromCoverage_PrivateConstructor_Obviously
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface _ExcludeFromCoverage {

    /**
     * A short human-readable reason explaining why the annotated element is excluded from
     * coverage.
     *
     * @return the exclusion reason; never {@code null}.
     */
    String value();
}
