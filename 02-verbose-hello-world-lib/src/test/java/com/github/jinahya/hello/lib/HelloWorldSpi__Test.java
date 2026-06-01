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
import lombok.*;
import lombok.extern.slf4j.*;

import java.util.*;
import java.util.stream.*;

/**
 * An abstract base class for {@code HelloWorldSpi_*_Test} subclasses that exercise
 * {@link HelloWorld} instances supplied by {@link HelloWorldServiceProvider}s loaded via
 * {@link ServiceLoader}. Subclasses override {@link #services()} (inherited from
 * {@link HelloWorld__Test}) to select which subset of providers — all, qualified, or unqualified —
 * feeds the inherited {@link HelloWorld#set(byte[], int) set(array, index)} contract assertions.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
abstract class HelloWorldSpi__Test extends HelloWorld__Test {

    /**
     * Returns a {@link Stream} of every {@link HelloWorldServiceProvider} discovered by
     * {@link ServiceLoader#load(Class) ServiceLoader.load(HelloWorldServiceProvider.class)},
     * logging each provider as it is observed. Subclasses typically filter and map this stream to
     * expose a stream of {@link HelloWorld} services from {@link #services()}.
     *
     * @return a {@link Stream} of every loaded {@link HelloWorldServiceProvider}.
     */
    static Stream<HelloWorldServiceProvider> providers() {
        final var provider = ServiceLoader.load(HelloWorldServiceProvider.class);
        final var iterator = provider.iterator();
        final var spliterator = Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED);
        return StreamSupport.stream(spliterator, false)
                .peek(p -> log.debug("provider: {}", p));
    }
}
