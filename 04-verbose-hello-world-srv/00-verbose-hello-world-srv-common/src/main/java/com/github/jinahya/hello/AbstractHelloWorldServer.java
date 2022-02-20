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
abstract class AbstractHelloWorldServer
        implements IHelloWorldServer {

    /**
     * Creates a new instance.
     */
    AbstractHelloWorldServer() {
        super();
    }

    abstract void openInternal(final SocketAddress endpoint, final Path dir)
            throws IOException;

    @Override
    public void open(final SocketAddress endpoint, final Path dir)
            throws IOException {
        try {
            lock.lock();
            close();
            openInternal(endpoint, dir);
        } finally {
            lock.unlock();
        }
    }

    abstract void closeInternal() throws IOException;

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
