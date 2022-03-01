package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-04-srv0-common
 * %%
 * Copyright (C) 2018 - 2021 Jinahya, Inc.
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

import jakarta.validation.constraints.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.file.Path;

/**
 * An interface for Hello World servers.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public interface HelloWorldServer extends Closeable {

    /**
     * Opens this server binding to specified socket address and writes the port number, on which
     * this server is listening, to a file of predefined name in specified directory.
     *
     * @param endpoint the local socket address to bind.
     * @param dir      the directory to which the file is created; may be {@code null} in which case
     *                 the file is not written.
     * @throws IOException if an I/O error occurs.
     * @see #open(SocketAddress)
     * @see IHelloWorldServerUtils#readPortNumber(Path)
     */
    void open(@NotNull SocketAddress endpoint, Path dir) throws IOException;

    /**
     * Opens this server binding to specified socket address.
     *
     * @param endpoint the local socket address to bind.
     * @throws IOException if an I/O error occurs.
     * @see #open(SocketAddress, Path)
     */
    default void open(@NotNull SocketAddress endpoint) throws IOException {
        open(endpoint, null);
    }

    /**
     * Returns an instance of {@link HelloWorld} to serve with.
     *
     * @return an instance of {@link HelloWorld}.
     */
    default HelloWorld service() {
        return IHelloWorldServerHelper.helloWorld();
    }
}
