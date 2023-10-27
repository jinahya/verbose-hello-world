package com.github.jinahya.hello.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Slf4j
public final class JavaIoUtils {

    public static File fillRandom(final File file, final int bytes, final long position)
            throws IOException {
        Objects.requireNonNull(file, "file is null");
        return JavaNioFileUtils.fillRandom(file.toPath(), bytes, position).toFile();
    }

    /**
     * Print content of specified file.
     *
     * @param file the file whose content is printed.
     * @return given {@code file}.
     * @throws IOException if an I/O error occurs.
     */
    public static File hexl(final File file) throws IOException {
        if (!Objects.requireNonNull(file, "file is null").isFile()) {
            throw new IllegalArgumentException("not a normal file: " + file);
        }
        return JavaNioFileUtils.hexl(file.toPath()).toFile();
    }

    private JavaIoUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
