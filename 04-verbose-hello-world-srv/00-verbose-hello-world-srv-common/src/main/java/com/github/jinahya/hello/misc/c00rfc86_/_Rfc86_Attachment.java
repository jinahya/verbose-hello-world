package com.github.jinahya.hello.misc.c00rfc86_;

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

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * An abstract attachment class for {@link com.github.jinahya.hello.misc.c01rfc863} package and
 * {@link com.github.jinahya.hello.misc.c02rfc862} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@SuppressWarnings({
        "java:S101" // _AbstractRfc86_...
})
public abstract class _Rfc86_Attachment implements Closeable {

    /**
     * Creates a new instance with specified arguments.
     *
     * @param bytes     an initial value for the {@code bytes} property.
     * @param algorithm a message digest algorithm.
     * @param printer   a function for printing digest.
     * @throws IllegalArgumentException when the {@code algorithm} is unknown.
     * @throws NullPointerException     when the {@code printer} is {@code null}.
     */
    protected _Rfc86_Attachment(final int bytes, final String algorithm,
                                Function<? super byte[], ? extends CharSequence> printer) {
        super();
        this.bytes = bytes;
        buffer = _Rfc86_Utils.newBuffer();
        assert buffer.hasArray();
        slice = buffer.slice();
        assert slice.hasArray();
        assert slice.array() == buffer.array();
        try {
            digest = MessageDigest.getInstance(algorithm);
        } catch (final NoSuchAlgorithmException nsae) {
            throw new IllegalArgumentException("unknown algorithm(" + algorithm + ')', nsae);
        }
        this.printer = Objects.requireNonNull(printer, "printer is null");
    }

    // --------------------------------------------------------------------------- java.io.Closeable

    /**
     * Closes this attachment, and releases any resources associated with it.
     *
     * @throws IOException           if an I/O error occurs.
     * @throws IllegalStateException if this attachment already has been closed.
     */
    @Override
    public void close() throws IOException {
        if (closed.getAndSet(true)) {
            throw new IllegalStateException("already closed");
        }
    }

    /**
     * Closes this attachment while re-throwing the {@link IOException}, if any thrown, as wrapped
     * in an instance of {@link UncheckedIOException}.
     *
     * @see #close()
     */
    public final void closeUnchecked() {
        try {
            close();
        } catch (final IOException ioe) {
            throw new UncheckedIOException("failed to close", ioe);
        }
    }

    // -------------------------------------------------------------------------------------- closed

    /**
     * Checks whether this attachment is closed.
     *
     * @return {@code true} if this attachment is closed; {@code false} otherwise.
     */
    public final boolean isClosed() {
        return closed.get();
    }

    // --------------------------------------------------------------------------------------- bytes

    /**
     * Returns current value of {@link #bytes} property.
     *
     * @return current value of {@link #bytes} property.
     */
    protected final int getBytes() {
        return bytes;
    }

    /**
     * Replaces current value of {@code bytes} property with specified value.
     *
     * @param bytes new value for the {@code bytes} property; must be not negative.
     */
    void setBytes(final int bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("bytes(" + bytes + ") < 0");
        }
        this.bytes = bytes;
    }

    /**
     * Increases current value of {@code bytes} property by specified value.
     *
     * @param delta the value to be added to current value of the {@code bytes} property; must be
     *              greater than or equal to zero.
     * @return update value of the {@code bytes} property.
     * @throws IllegalArgumentException when {@code delta} is negative.
     */
    public final int increaseBytes(final int delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("delta(" + delta + ") is negative");
        }
        setBytes(getBytes() + delta);
        return getBytes();
    }

    /**
     * Decreases current value of {@code bytes} property by specified value.
     *
     * @param delta the value to be added to current value of the {@code bytes} property; must be
     *              greater than or equal to zero.
     * @return updated value of the {@code bytes} property.
     * @throws IllegalArgumentException when {@code delta} is negative or greater than current value
     *                                  of the {@code bytes} property.
     */
    public final int decreaseBytes(final int delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("delta(" + delta + ") is negative");
        }
        if (delta > getBytes()) {
            throw new IllegalArgumentException("delta(" + delta + ") > bytes(" + bytes + ")");
        }
        setBytes(getBytes() - delta);
        return getBytes();
    }

    // -------------------------------------------------------------------------------------- buffer
    protected boolean hasRemaining() {
        return buffer.hasRemaining();
    }

    protected ByteBuffer getBuffer(final UnaryOperator<ByteBuffer> operator) {
        if (operator == null) {
            return buffer;
        }
        return operator.apply(buffer);
    }

    protected ByteBuffer getBuffer() {
        return getBuffer(null);
    }

    protected <R> R applyBuffer(final Function<? super ByteBuffer, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(getBuffer());
    }

    // -------------------------------------------------------------------------------------- digest

    /**
     * Updates specified number of bytes in {@code buffer} preceding {@code buffer}'s current
     * position to {@code digest}.
     *
     * @param bytes the number of bytes to be updated to the {@code digest}.
     * @return given {@code bytes}.
     * @throws IllegalArgumentException if {@code bytes} is greater than {@code buffer.position}.
     */
    public final int updateDigest(final int bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("bytes(" + bytes + ") is negative");
        }
        if (bytes > buffer.position()) {
            throw new IllegalArgumentException(
                    "bytes(" + bytes + ") > buffer.position(" + buffer.position() + ")"
            );
        }
        digest.update(
                slice.position(buffer.position() - bytes)
                        .limit(buffer.position())
        );
        return bytes;
    }

    /**
     * Logs out {@code digest}.
     */
    public final void logDigest() {
        _Rfc86_Utils.logDigest(digest, printer);
    }

    // ---------------------------------------------------------------------------------------------
    private final AtomicBoolean closed = new AtomicBoolean();

    private int bytes; // number of bytes to send or received so far

    protected final ByteBuffer buffer;

    private final ByteBuffer slice;

    private final MessageDigest digest;

    private final Function<? super byte[], ? extends CharSequence> printer;
}
