package com.github.jinahya.hello.miscellaneous._java_nio_channels;

/*-
 * #%L
 * verbose-hello-world-api
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

import lombok.extern.slf4j.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import static com.github.jinahya.hello.miscellaneous._java_nio.__Java_Nio_TestUtils.*;
import static com.github.jinahya.hello.miscellaneous._Java__TestUtils.*;

/**
 * A class providing test utilities for {@link java.nio.channels} usages.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public final class _Channels_TestUtils {

    // ---------------------------------------------------------------------------------------------
    public static long copy1(final ByteBuffer b, final ReadableByteChannel in,
                             final WritableByteChannel out)
            throws IOException {
        requireNonZeroCapacity(b);
        long count = 0L;
        while (in.read(b.clear()) != -1) {
            for (b.flip(); b.hasRemaining(); ) {
                count += out.write(b);
            }
        }
        return count;
    }

    public static long copy2(final ByteBuffer b, final ReadableByteChannel in,
                             final WritableByteChannel out)
            throws IOException {
        requireNonZeroCapacity(b);
        b.clear();
        long count = 0L;
        while (in.read(b) != -1) {
            count += out.write(b.flip());
            b.compact();
        }
        for (b.flip(); b.hasRemaining(); ) {
            count += out.write(b);
        }
        return count;
    }

    public static long copy1(final int capacity, final ReadableByteChannel in,
                             final WritableByteChannel out)
            throws IOException {
        if (capacity <= 0) {
            throw new IllegalArgumentException("non-positive capacity: " + capacity);
        }
        final var b = ByteBuffer.allocate(capacity);
        return copy1(b, in, out);
    }

    public static long copy2(final int capacity, final ReadableByteChannel in,
                             final WritableByteChannel out)
            throws IOException {
        if (capacity <= 0) {
            throw new IllegalArgumentException("non-positive capacity: " + capacity);
        }
        final var b = ByteBuffer.allocate(capacity);
        return copy2(b, in, out);
    }

    // ---------------------------------------------------------------------------------------------
    public static <A> void copy1(final ByteBuffer b, final AsynchronousByteChannel in,
                                 final AsynchronousByteChannel out, final A attachment,
                                 final CompletionHandler<Long, ? super A> handler) {
        requireNonZeroCapacity(b);
        requireNotSame(in, out);
        Objects.requireNonNull(handler, "handler is null");
        final var count = new LongAdder();
        final var readerReference = new AtomicReference<CompletionHandler<Integer, Object>>();
        final var writeHandler = new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(final Integer result, final Object y) {
                if (!b.hasRemaining()) {
                    final var r = readerReference.get();
                    if (r != null) {
                        in.read(b.clear(), null, r);
                    } else {
                        handler.completed(count.longValue(), attachment);
                    }
                    return;
                }
                out.write(b, null, this);
            }

            @Override
            public void failed(final Throwable exc, final Object y) {
                handler.failed(exc, attachment);
            }
        };
        in.read(b.clear(), null, new CompletionHandler<>() {
            @Override
            public void completed(final Integer result, final Object x) {
                if (result == -1) {
                    readerReference.set(null);
                } else {
                    readerReference.compareAndSet(null, this);
                    count.add(result);
                }
                out.write(b.flip(), null, writeHandler);
            }

            @Override
            public void failed(final Throwable exc, final Object x) {
                handler.failed(exc, attachment);
            }
        });
    }

    public static <A> void copy2(final ByteBuffer b, final AsynchronousByteChannel in,
                                 final AsynchronousByteChannel out, final A attachment,
                                 final CompletionHandler<Long, ? super A> handler) {
        requireNonZeroCapacity(b);
        requireNotSame(in, out);
        Objects.requireNonNull(handler, "handler is null");
        b.clear();
        final var count = new LongAdder();
        final var readerReference = new AtomicReference<CompletionHandler<Integer, Object>>();
        final var writeHandler = new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(final Integer result, final Object y) {
                count.add(result);
                final var r = readerReference.get();
                if (r != null) {
                    b.compact();
                    in.read(b, null, r);
                } else if (b.hasRemaining()) {
                    out.write(b, null, this);
                } else {
                    handler.completed(count.longValue(), attachment);
                }
            }

            @Override
            public void failed(final Throwable exc, final Object y) {
                handler.failed(exc, attachment);
            }
        };
        in.read(b, null, new CompletionHandler<>() {
            @Override
            public void completed(final Integer result, final Object x) {
                if (result == -1) {
                    readerReference.set(null);
                    if (b.position() == 0) {
                        handler.completed(count.longValue(), attachment);
                        return;
                    }
                    out.write(b.flip(), null, writeHandler);
                    return;
                }
                readerReference.compareAndSet(null, this);
                out.write(b.flip(), null, writeHandler);
            }

            @Override
            public void failed(final Throwable exc, final Object x) {
                handler.failed(exc, attachment);
            }
        });
    }

    public static <A> void copy1(final int capacity, final AsynchronousByteChannel in,
                                 final AsynchronousByteChannel out, final A attachment,
                                 final CompletionHandler<Long, ? super A> handler) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("non-positive capacity: " + capacity);
        }
        copy1(ByteBuffer.allocate(capacity), in, out, attachment, handler);
    }

    public static <A> void copy2(final int capacity, final AsynchronousByteChannel in,
                                 final AsynchronousByteChannel out, final A attachment,
                                 final CompletionHandler<Long, ? super A> handler) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("non-positive capacity: " + capacity);
        }
        copy2(ByteBuffer.allocate(capacity), in, out, attachment, handler);
    }

    private _Channels_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
