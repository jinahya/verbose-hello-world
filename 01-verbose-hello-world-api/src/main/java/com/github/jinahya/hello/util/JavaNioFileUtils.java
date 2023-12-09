package com.github.jinahya.hello.util;

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

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HexFormat;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utilities for {@link java.nio.file} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public final class JavaNioFileUtils {

    public static Path fillRandom(final Path path, int bytes, long position)
            throws IOException {
        Objects.requireNonNull(path, "path is null");
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") < 0L");
        }
        try (var channel = FileChannel.open(path, StandardOpenOption.CREATE,
                                            StandardOpenOption.WRITE)) {
            final var src = ByteBuffer.allocate(128);
            assert src.hasArray();
            src.position(src.limit());
            for (int w; bytes > 0; bytes -= w) {
                if (!src.hasRemaining()) {
                    ThreadLocalRandom.current().nextBytes(src.array());
                    src.clear().limit(Math.min(src.limit(), bytes));
                }
                w = channel.write(src, position);
                assert w >= 0;
                position += w;
            }
            channel.force(false);
        }
        return path;
    }

    public static Path hexl(final Path path, final OpenOption... options)
            throws IOException {
        if (!Files.isRegularFile(Objects.requireNonNull(path, "path is null"))) {
            throw new IllegalArgumentException("not a regular file: " + path);
        }
        final var capacity = 32;
        final var format = "%1$" + -(capacity << 1) + "s";
        try (var channel = FileChannel.open(path, options)) {
            final var buffer = ByteBuffer.allocate(capacity);
            assert buffer.hasArray();
            for (var position = 0L; position < channel.size(); position += capacity) {
                final var r = channel.read(buffer.clear(), position);
                if (r == -1) {
                    break;
                }
                log.atDebug()
                        .setMessage("{} {}")
                        .addArgument(String.format(
                                format,
                                HexFormat.of().formatHex(buffer.array(), 0, buffer.position())
                        ))
                        .addArgument(
                                new String(buffer.array(), 0, buffer.position(),
                                           StandardCharsets.US_ASCII)
                                        .replaceAll("\\p{Cntrl}", " ")
                        )
                        .log();
            }
        }
        return path;
    }

    private JavaNioFileUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
