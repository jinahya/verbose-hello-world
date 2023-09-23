package com.github.jinahya.hello.misc;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;

public interface _Rfc86_Attachment extends Closeable {

    default void closeUnchecked() {
        try {
            close();
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    int getBytes();

    /**
     * Increases current value of {@code bytes} property by specified value, and returns updated
     * value of the {@code bytes} property.
     *
     * @param delta delta value for the {@code bytes} property; should be greater than or equal to
     *              zero.
     * @return updated value of the {@code bytes} property.
     */
    int increaseBytes(final int delta);

    /**
     * Decreases current value of {@code bytes} property by specified value, and returns updated
     * value of the {@code bytes} property.
     *
     * @param delta delta value for the {@code bytes} property; should be greater than or equal to
     *              zero.
     * @return updated value of the {@code bytes} property.
     */
    int decreaseBytes(final int delta);

    // -------------------------------------------------------------------------------------- buffer
//    ByteBuffer buffer(final Consumer<? super ByteBuffer> consumer);

    // -------------------------------------------------------------------------------------- digest
    int updateDigest(final int bytes);

    void logDigest();
}
