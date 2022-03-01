package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-srv
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

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * A utility class for Hello World servers.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class IHelloWorldClientUtils {

    private static void validateArgumentsForReadFully(InputStream input, byte[] b, int off,
                                                      int len) {
        Objects.requireNonNull(input, "input is null");
        Objects.requireNonNull(b, "b is null");
        if (off < 0) {
            throw new IllegalArgumentException("off(" + off + ") is negative");
        }
        if (len < 0) {
            throw new IllegalArgumentException("len(" + len + ") is negative");
        }
        if (len > b.length - off) {
            throw new IllegalArgumentException(
                    "len(" + len + ") > b.len(" + b.length + ") - off(" + off + ")");
        }
    }

    static void readFully1(InputStream input, byte[] b, int off, int len) throws IOException {
        validateArgumentsForReadFully(input, b, off, len);
        while (len > 0) {
            var bytes = input.read(b, off, len);
            if (bytes == -1) {
                throw new EOFException("premature eof");
            }
            off += bytes;
            len -= bytes;
        }
    }

    static void readFully1(InputStream input, byte[] b) throws IOException {
        readFully1(input, Objects.requireNonNull(b, "b is null"));
    }

    static void readFully2(InputStream input, byte[] b, int off, int len) throws IOException {
        validateArgumentsForReadFully(input, b, off, len);
        new DataInputStream((input)).readFully(b, off, len);
    }

    static void readFully2(InputStream input, byte[] b) throws IOException {
        readFully2(input, Objects.requireNonNull(b, "b is null"));
    }

    static void readFully3(InputStream input, byte[] b, int off, int len) throws IOException {
        validateArgumentsForReadFully(input, b, off, len);
        if (input.readNBytes(b, off, len) < len) {
            throw new EOFException("premature eof");
        }
    }

    static void readFully3(InputStream input, byte[] b) throws IOException {
        readFully3(input, Objects.requireNonNull(b, "b is null"));
    }

    private IHelloWorldClientUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
