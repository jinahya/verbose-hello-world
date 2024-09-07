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

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A class serves {@code hello, world} to clients.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public abstract class AbstractHelloWorldServer implements HelloWorldServer {

    /**
     * Creates a new instance.
     */
    protected AbstractHelloWorldServer() {
        super();
    }

    /**
     * Opens this server binding to specified socket address and writes the port number, on which
     * this server is listening, to a file of predefined name in specified directory.
     *
     * @param endpoint an endpoint to bind.
     * @param dir      the directory to which the file is created; may be {@code null} in which case
     *                 the file is not written.
     * @throws IOException if an I/O error occurs.
     */
    protected abstract void openInternal(SocketAddress endpoint, Path dir)
            throws IOException;

    /**
     * {@inheritDoc}
     *
     * @param endpoint {@inheritDoc}
     * @param dir      {@inheritDoc}
     * @throws IOException {@inheritDoc}
     * @implSpec Invokes {@link #openInternal(SocketAddress, Path)} method.
     */
    @Override
    public void open(SocketAddress endpoint, Path dir)
            throws IOException {
        try {
            lock.lock();
            close();
            openInternal(endpoint, dir);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Closes this server and releases any system resources associated with it.
     *
     * @throws IOException if an I/O error occurs.
     */
    protected abstract void closeInternal() throws IOException;

    /**
     * {@inheritDoc}
     *
     * @throws IOException {@inheritDoc}
     * @implSpec Invokes {@link #closeInternal()} method.
     */
    @Override
    public void close() throws IOException {
        try {
            lock.lock();
            closeInternal();
        } finally {
            lock.unlock();
        }
    }

    private final Lock lock = new ReentrantLock();
}
