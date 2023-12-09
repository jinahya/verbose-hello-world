package com.github.jinahya.hello.util;

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
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An abstract class for attachments.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@SuppressWarnings({
        "java:S101" // _Attachment...
})
public abstract class _Attachment
        implements Closeable {

    /**
     * Creates a new instance.
     */
    protected _Attachment() {
        super();
    }

    // --------------------------------------------------------------------------- java.io.Closeable

    /**
     * Closes this attachment, and releases any resources associated with it.
     *
     * @throws IOException           if an I/O error occurs.
     * @throws IllegalStateException if this attachment has been already closed.
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

    // ---------------------------------------------------------------------------------------------
    private final AtomicBoolean closed = new AtomicBoolean();
}
