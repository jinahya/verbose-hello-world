package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-srv1
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

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

@Slf4j
abstract class HelloWorldClient {

    private static final String SYSTEM_PROPERTY_HOST = "host";

    static InetAddress host() {
        return Optional.ofNullable(System.getProperty(SYSTEM_PROPERTY_HOST))
                .map(h -> {
                    try {
                        return InetAddress.getByName(h);
                    } catch (final UnknownHostException nhe) {
                        throw new RuntimeException("failed to resolve " + h);
                    }
                })
                .orElse(null);
    }

    private static final String SYSTEM_PROPERTY_PORT = "port";

    static int port() {
        return Optional.ofNullable(System.getProperty(SYSTEM_PROPERTY_PORT))
                .map(Integer::parseInt)
                .orElse(0);
    }

    HelloWorldClient() {
        super();
    }
}
