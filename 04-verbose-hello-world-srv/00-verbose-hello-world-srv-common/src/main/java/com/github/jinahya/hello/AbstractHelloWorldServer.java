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

import java.net.SocketAddress;

import static java.util.Objects.requireNonNull;
import static java.util.ServiceLoader.load;

/**
 * A class serves {@code hello, world} to clients.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
abstract class AbstractHelloWorldServer
        implements IHelloWorldServer {

    /**
     * Creates a new instance with specified local socket address to bind.
     *
     * @param endpoint the local socket address to bind.
     */
    AbstractHelloWorldServer(final SocketAddress endpoint) {
        super();
        this.endpoint = requireNonNull(endpoint);
    }

    /**
     * Returns an instance of {@link HelloWorld} interface.
     *
     * @return an instance of {@link HelloWorld} interface.
     */
    HelloWorld helloWorld() {
        if (helloWorld == null) {
            helloWorld = load(HelloWorld.class).iterator().next();
        }
        return helloWorld;
    }

    /**
     * The local socket address to bind.
     */
    final SocketAddress endpoint;

    private HelloWorld helloWorld;
}
