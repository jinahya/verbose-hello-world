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
import org.junit.jupiter.api.*;

import java.util.stream.*;

/**
 * Runs the {@link HelloWorld#set(byte[], int) set(array, index)} contract inherited from
 * {@link HelloWorld__Test} against every <em>qualified</em> {@link HelloWorld} — i.e. services
 * supplied by providers whose {@link HelloWorldServiceProvider#isQualified() isQualified()} returns
 * {@code true} ({@link HelloWorldImpl}) — filtering the stream returned by the base class's
 * {@link HelloWorldSpi__Test#providers() providers()}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("HelloWorldSpi / qualified")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldSpi_Qualified_Test extends HelloWorldSpi__Test {

    static Stream<HelloWorldServiceProvider> providers() {
        return HelloWorldSpi__Test.providers()
                .filter(HelloWorldServiceProvider::isQualified);
    }

    @Override
    Stream<HelloWorld> services() {
        return providers()
                .map(HelloWorldServiceProvider::getService)
                .peek(s -> log.debug("service: {}", s));
    }
}
