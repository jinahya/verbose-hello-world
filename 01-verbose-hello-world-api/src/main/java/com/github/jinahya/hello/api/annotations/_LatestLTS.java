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
 * A source-only marker for declarations whose value, list, or shape reflects the latest Long-Term
 * Support release of the Java platform. Used on constants such as algorithm/transformation tables
 * to flag which entries are mandated by the current LTS JDK's API documentation.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see _LatestJDK
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
public @interface _LatestLTS {

    /**
     * An optional note (e.g., the LTS version number or a free-text qualifier).
     *
     * @return the note; defaults to an empty string.
     */
    String value() default "";
}
