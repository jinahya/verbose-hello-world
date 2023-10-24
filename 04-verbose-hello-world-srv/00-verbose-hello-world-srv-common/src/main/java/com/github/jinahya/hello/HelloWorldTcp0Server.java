package com.github.jinahya.hello;

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

import com.github.jinahya.hello.util.HelloWorldServerUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import static com.github.jinahya.hello.HelloWorldServerHelper.loadService;
import static com.github.jinahya.hello.util.JavaLangUtils.readLinesAndCloseWhenTests;

@Slf4j
class HelloWorldTcp0Server {

    private static final int BACKLOG = 50; // default

    public static void main(String... args) throws Exception {
        InetAddress host;
        try {
            host = InetAddress.getByName(args[0]);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            host = InetAddress.getByName("::");
        }
        try (ServerSocket server = new ServerSocket()) {
            server.bind(new InetSocketAddress(host, HelloWorldServerConstants.PORT), BACKLOG);
            log.debug("bound to {}", server.getLocalSocketAddress());
            readLinesAndCloseWhenTests(HelloWorldServerUtils::isQuit, server);
            var service = loadService();
            while (!server.isClosed()) {
                // TODO: accept client
                // TODO: send 'hello, world'
            }
        }
    }

    private HelloWorldTcp0Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
