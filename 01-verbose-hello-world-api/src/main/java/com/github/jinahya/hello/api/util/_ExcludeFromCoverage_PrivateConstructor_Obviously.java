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
 * A documentation-only marker for the conventional, never-invoked {@code private} constructor of a
 * utility class — the one whose body is
 * {@code throw new AssertionError("instantiation is not allowed");} — so coverage tooling does not
 * flag the trivially-unreachable line. Retained at {@link RetentionPolicy#RUNTIME} so the marker is
 * visible to Javadoc and to reflective inspection.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see _ExcludeFromCoverage
 * @see _ExcludeFromCoverage_FailingCase
 */
//@Retention(RetentionPolicy.CLASS)
@Retention(RetentionPolicy.RUNTIME) // javadoc
@Target({ElementType.CONSTRUCTOR})
public @interface _ExcludeFromCoverage_PrivateConstructor_Obviously {

}
