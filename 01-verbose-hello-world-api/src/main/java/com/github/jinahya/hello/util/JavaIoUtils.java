package com.github.jinahya.hello.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public final class JavaIoUtils {

    // ---------------------------------------------------------------------------------------- File

    /**
     * Writes some(possibly zero) bytes to specified file, and return the file.
     *
     * @param file the file to which bytes are written.
     * @return given {@code file}.
     * @throws IOException if an I/O error occurs
     */
    @SuppressWarnings({
            "java:S127" // assigning the loop counter from within the loop body
    })
    public static File writeSome(final File file)
            throws IOException {
        if (!Objects.requireNonNull(file, "file is null").isFile()) {
            throw new IllegalArgumentException("not a normal file: " + file);
        }
        try (var stream = new FileOutputStream(file)) {
            final var array = new byte[1024];
            for (var bytes = ThreadLocalRandom.current().nextInt(8193); bytes > 0; ) {
                ThreadLocalRandom.current().nextBytes(array);
                final var length = Math.min(array.length, bytes);
                stream.write(array, 0, length);
                bytes -= length; // java:S127
            }
            stream.flush();
        }
        return file;
    }

    /**
     * Creates a new empty file in specified directory, writes some(possibly zero) bytes to the
     * file, and return the file.
     *
     * @param dir the directory in which a new empty file is created.
     * @return a new file created in {@code dir} with some(possibly zero) bytes written.
     * @throws IOException if an I/O error occurs
     * @see File#createTempFile(String, String, File)
     * @see #writeSome(File)
     */
    public static File createTempFileInAndWriteSome(final File dir)
            throws IOException {
        if (!Objects.requireNonNull(dir, "dir is null").isDirectory()) {
            throw new IllegalArgumentException("not a directory: " + dir);
        }
        final var file = File.createTempFile("tmp", "tmp", dir);
        return writeSome(file);
    }

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
    public static File hexl(final File file)
            throws IOException {
        if (!Objects.requireNonNull(file, "file is null").isFile()) {
            throw new IllegalArgumentException("not a normal file: " + file);
        }
        return JavaNioFileUtils.hexl(file.toPath()).toFile();
    }

    private JavaIoUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
