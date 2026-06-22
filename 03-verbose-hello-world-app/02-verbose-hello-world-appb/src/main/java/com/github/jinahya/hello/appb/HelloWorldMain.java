package com.github.jinahya.hello.appb;

/*-
 * #%L
 * verbose-hello-world-appb
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

import com.github.jinahya.hello.api.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

/**
 * A program whose {@link #main()} method obtains a {@link HelloWorld} via the
 * {@link ServiceLoader Service Provider Interface} and writes {@code hello, world} to
 * {@link System#out} through {@link HelloWorld#write(WritableByteChannel) write(channel)}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ServiceLoader#load(Class)
 * @see HelloWorld#write(WritableByteChannel)
 */
@SuppressWarnings({
        "java:S106" // Standard outputs should not be used directly to log anything
})
class HelloWorldMain {

    /**
     * Loads the first registered {@link HelloWorld} provider through {@link ServiceLoader}, wraps
     * {@link System#out} into a {@link WritableByteChannel} via
     * {@link Channels#newChannel(java.io.OutputStream)}, writes {@code hello, world} to that
     * channel via {@link HelloWorld#write(WritableByteChannel) write(channel)} (which returns the
     * channel), and then writes the {@link System#lineSeparator() line separator} bytes by chaining
     * a {@link WritableByteChannel#write(ByteBuffer)} call on the returned channel.
     *
     * @throws IOException if an I/O error occurs while writing.
     * @see ServiceLoader#load(Class)
     * @see HelloWorld#write(WritableByteChannel)
     */
    static void main() throws IOException {
        ServiceLoader.load(HelloWorld.class)
                .iterator()
                .next()
                .write(Channels.newChannel(System.out))
                .write(ByteBuffer.wrap(System.lineSeparator().getBytes()));
    }

    /**
     * Suppresses external instantiation; always throws an {@link AssertionError}.
     */
    private HelloWorldMain() {
        throw new AssertionError("instantiation is not allowed");
    }
}
