package com.github.jinahya.hello.api.spi;

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

import com.github.jinahya.hello.api.HelloWorld;

import java.util.Objects;
import java.util.function.Function;

/**
 * An interface for providing an instance of {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public interface HelloWorldServiceProvider {

    /**
     * Returns an instance of {@link HelloWorld} interface.
     *
     * @return an instance of {@link HelloWorld} interface.
     */
    HelloWorld getService();

    /**
     * Applies an instance of {@link HelloWorld} interface, returned from {@link #getService()}
     * method, to specified function, and returns the result.
     *
     * @param function the function.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @see #getService()
     */
    default <R> R applyService(final Function<? super HelloWorld, ? extends R> function) {
        return Objects.requireNonNull(function, "function is null")
                .apply(getService());
    }
}
