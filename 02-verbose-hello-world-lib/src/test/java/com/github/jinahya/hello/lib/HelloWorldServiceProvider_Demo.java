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
 * A {@link HelloWorldServiceProvider} that supplies {@link HelloWorldDemo} instances. The supplied
 * service is classified as <em>unqualified</em> ({@link #isQualified()} returns
 * {@code false}).
 *
 * <p>{@code public} so {@link java.util.ServiceLoader ServiceLoader}'s classpath-mode
 * reflection can invoke its implicit no-arg constructor.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class HelloWorldServiceProvider_Demo implements HelloWorldServiceProvider {

    /**
     * {@inheritDoc}
     *
     * @return {@code false}.
     */
    @Override
    public boolean isQualified() {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @return a new {@link HelloWorldDemo} instance.
     */
    @Override
    public HelloWorld getService() {
        return new HelloWorldDemo();
    }
}
