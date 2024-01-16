package com.github.jinahya.hello._05_util_concurrent;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2024 Jinahya, Inc.
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

import com.github.jinahya.hello.HelloWorldExecutor;
import com.github.jinahya.hello._HelloWorldTest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.mockito.Spy;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PACKAGE)
abstract class __HelloWorldExecutorTest extends _HelloWorldTest {

    static final Executor EXECUTOR = Executors.newSingleThreadExecutor(
            Thread.ofVirtual().name("executor-", 0L).factory()
    );

    @Spy
    @Accessors(fluent = true)
    @Getter(AccessLevel.PROTECTED)
    private HelloWorldExecutor executor;
}
