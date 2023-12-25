package com.github.jinahya.hello;

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

import jakarta.inject.Inject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A test class injects using Guice.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://github.com/google/guice">Guice</a>
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldDiGuiceTest
        extends HelloWorldDiTest {

    @BeforeEach
    void _beforeEach() {
        final var injector = Guice.createInjector(new HelloWorldDiGuiceModule());
        log.debug("injector: {}", injector);
        injector.injectMembers(this);
        log.debug("members injected: {}", this);
    }

    @Override
    HelloWorld service() {
        return switch (ThreadLocalRandom.current().nextInt(6)) {
            case 4 -> bindingQualifiedDemo;
            case 5 -> bindingQualifiedImpl;
            default -> super.service();
        };
    }

    @_BindingQualifiedDemo
    @Inject
    private HelloWorld bindingQualifiedDemo;

    @_BindingQualifiedImpl
    @Inject
    private HelloWorld bindingQualifiedImpl;
}
