package com.github.jinahya.hello.util.javax.crypto;

/*-
 * #%L
 * verbose-hello-world-api
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

import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;

import javax.crypto.Mac;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

/**
 * Utilities for {@link Mac}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class MacUtils {

    public static long update(final Mac mac, final InputStream stream, final byte[] buffer)
            throws IOException {
        Objects.requireNonNull(mac, "mac is null");
        Objects.requireNonNull(stream, "stream is null");
        if (Objects.requireNonNull(buffer, "buffer is null").length == 0) {
            throw new IllegalArgumentException(
                    "zero-length buffer: " + Objects.toString(buffer));
        }
        var count = 0L;
        for (int r; (r = stream.read(buffer)) != -1; count += r) {
            mac.update(buffer, 0, r);
        }
        return count;
    }

    public static byte[] get(final Mac mac, final File file, final byte[] buffer)
            throws IOException {
        try (var stream = new FileInputStream(file)) {
            final var count = update(mac, stream, buffer);
            assert count == file.length();
        }
        return mac.doFinal();
    }

    public static long update(final Mac mac, final ReadableByteChannel channel,
                              final ByteBuffer buffer)
            throws IOException {
        Objects.requireNonNull(mac, "mac is null");
        Objects.requireNonNull(channel, "channel is null");
        if (Objects.requireNonNull(buffer, "buffer is null").capacity() == 0) {
            throw new IllegalArgumentException("zero-capacity buffer: " + buffer);
        }
        var count = 0L;
        for (int r; (r = channel.read(buffer.clear())) != -1; count += r) {
            mac.update(buffer.flip());
        }
        return count;
    }

    public static byte[] get(final Mac mac, final Path path, final ByteBuffer buffer)
            throws IOException {
        try (var channel = FileChannel.open(path, StandardOpenOption.READ)) {
            final var count = update(mac, channel, buffer);
            assert count == Files.size(path);
        }
        return mac.doFinal();
    }

    // ---------------------------------------------------------------------------------------------
    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private MacUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
