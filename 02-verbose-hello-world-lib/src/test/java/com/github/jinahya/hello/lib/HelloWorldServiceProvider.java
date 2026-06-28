package com.github.jinahya.hello.lib;

/*-
 * #%L
 * verbose-hello-world-lib
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
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

import com.github.jinahya.hello.api.*;

/**
 * A test-only {@link java.util.ServiceLoader ServiceLoader} SPI in which each provider supplies a
 * {@link HelloWorld} instance and classifies it as either <em>qualified</em>
 * ({@link #isQualified()} {@code == true}) or <em>unqualified</em> (the complementary set). The
 * {@code HelloWorldSpi_*_Test} family uses this partition to exercise the
 * {@link HelloWorld#set(byte[], int)} contract against different service subsets.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
interface HelloWorldServiceProvider {

    /**
     * Indicates whether the service supplied by this provider belongs to the "qualified" subset.
     *
     * @return {@code true} if the supplied service is qualified; {@code false} otherwise.
     */
    boolean isQualified();

    /**
     * Returns a {@link HelloWorld} instance to be exercised by the tests. Implementations should
     * return a fresh instance on each invocation rather than caching a singleton, so callers
     * receive an isolated service and the SPI makes no implicit assumption that the supplied
     * service is stateless.
     *
     * @return a {@link HelloWorld} instance; never {@code null}.
     */
    HelloWorld getService();
}
