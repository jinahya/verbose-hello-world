package com.github.jinahya.hello.util;

/*-
 * #%L
 * verbose-hello-world-srv-common
 * %%
 * Copyright (C) 2018 - 2022 Jinahya, Inc.
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

import java.io.Closeable;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * A class prints socket options of all kinds of sockets.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorldNetUtils#printSocketOptions(Class, Object)
 */
@Slf4j
class SocketOptionsPrinter {

    static final Map<Class<?>, Callable<?>> PARIS;

    static {
        var m = new LinkedHashMap<Class<?>, Callable<?>>();
        m.put(Socket.class, Socket::new);
        m.put(ServerSocket.class, ServerSocket::new);
        m.put(DatagramSocket.class, DatagramSocket::new);
        m.put(SocketChannel.class, SocketChannel::open);
        m.put(ServerSocketChannel.class, ServerSocketChannel::open);
        m.put(DatagramChannel.class, DatagramChannel::open);
        m.put(AsynchronousSocketChannel.class, AsynchronousSocketChannel::open);
        m.put(AsynchronousServerSocketChannel.class, AsynchronousServerSocketChannel::open);
        PARIS = Collections.unmodifiableMap(m);
    }

    private static <T extends Closeable> void print(final Class<T> clazz,
                                                    final Callable<? extends T> initializer)
            throws Exception {
        Objects.requireNonNull(clazz, "clazz is null");
        Objects.requireNonNull(initializer, "initializer is null");
        System.out.printf("%n%1$s%n", clazz.getName());
        System.out.println("---------------------------------------------------------------------");
        try (T object = Objects.requireNonNull(initializer.call(), "null called")) {
            HelloWorldNetUtils.printSocketOptions(clazz, object);
        }
    }

    private static <T extends Closeable> void printHelper(final Class<T> clazz,
                                                          final Callable<?> initializer)
            throws Exception {
        print(clazz, () -> clazz.cast(initializer.call()));
    }

    public static void main(String... args) throws Exception {
        for (final Map.Entry<Class<?>, Callable<?>> pair : PARIS.entrySet()) {
            printHelper(pair.getKey().asSubclass(Closeable.class), pair.getValue());
        }
    }
}
