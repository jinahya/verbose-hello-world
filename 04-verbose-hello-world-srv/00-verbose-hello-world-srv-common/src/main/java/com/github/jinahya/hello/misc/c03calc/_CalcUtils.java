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

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@SuppressWarnings({
        "java:S101" // _CalcUtils
})
final class _CalcUtils {

    static ExecutorService newExecutorForClients() {
        return Executors.newFixedThreadPool(_CalcConstants.CLIENT_THREADS);
    }

    static ExecutorService newExecutorForServers() {
        return Executors.newFixedThreadPool(_CalcConstants.SERVER_THREADS);
    }

    static AsynchronousChannelGroup newAsynchronousChannelGroupForClients() throws IOException {
        return AsynchronousChannelGroup.withThreadPool(newExecutorForClients());
    }

    static AsynchronousChannelGroup newChannelGroupForServers() throws IOException {
        return AsynchronousChannelGroup.withThreadPool(newExecutorForServers());
    }

    // ---------------------------------------------------------------------------------------------

    private _CalcUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
