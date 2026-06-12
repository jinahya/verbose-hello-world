package com.github.jinahya.hello.miscellaneous;

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
import java.util.*;

import static com.github.jinahya.hello.miscellaneous._Java__TestUtils.*;

/**
 * A class providing test utilities for {@link java.io} usages.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@SuppressWarnings({"java:S101"})
public final class _Java_Io__TestUtils {

    /**
     * Copies all bytes from the given input stream to the given output stream, using the specified
     * buffer.
     *
     * @param b   the buffer to use; must not be {@code null} or empty.
     * @param in  the input stream to read from.
     * @param out the output stream to write to.
     * @return the total number of bytes copied.
     * @throws IOException if an I/O error occurs.
     */
    public static long copy(final byte[] b, final InputStream in, final OutputStream out)
            throws IOException {
        if (Objects.requireNonNull(b, "b is null").length == 0) {
            throw new IllegalArgumentException("b.length is zero");
        }
        requireNotSame(in, out);
        long count = 0L;
        for (int r; (r = in.read(b, 0, b.length)) != -1; count += r) {
            out.write(b, 0, r);
        }
        Arrays.fill(b, (byte) 0); // @@?
        return count;
    }

    private _Java_Io__TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
