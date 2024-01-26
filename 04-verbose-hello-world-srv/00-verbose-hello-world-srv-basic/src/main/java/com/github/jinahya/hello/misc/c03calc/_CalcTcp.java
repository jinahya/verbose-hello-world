package com.github.jinahya.hello.misc.c03calc;

/*-
 * #%L
 * verbose-hello-world-srv-common
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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@SuppressWarnings({
        "java:S101" // class _Calc...
})
abstract class _CalcTcp extends _Calc {

    // ------------------------------------------------------------------------------- server/client
    static final int SERVER_BACKLOG = 50;

    // ---------------------------------------------------------------------------------------------
    static ExecutorService newExecutorForServer(final String namePrefix) {
        return Executors.newFixedThreadPool(
                SERVER_THREADS,
                Thread.ofVirtual().name(namePrefix, 0L).factory()
        );
    }

    static ExecutorService newExecutorForClient(final String namePrefix) {
        return Executors.newFixedThreadPool(
                CLIENT_THREADS,
                Thread.ofVirtual().name(namePrefix, 0L).factory()
        );
    }
}
