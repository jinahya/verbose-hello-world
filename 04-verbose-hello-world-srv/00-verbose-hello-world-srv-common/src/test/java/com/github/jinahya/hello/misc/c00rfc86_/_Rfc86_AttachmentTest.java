package com.github.jinahya.hello.misc.c00rfc86_;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class _Rfc86_AttachmentTest<T extends _Rfc86_Attachment> {

    protected _Rfc86_AttachmentTest(final Class<T> attachmentClass) {
        super();
        this.attachmentClass = Objects.requireNonNull(attachmentClass, "attachmentClass is null");
    }

    @Test
    void decreaseBytes__() throws IOException {
        applyNewInstance(
                i -> {
                    final var bytes = ThreadLocalRandom.current().nextInt() >>> 24;
                    i.setBytes(bytes);
                    final var delta = ThreadLocalRandom.current().nextInt(bytes);
                    i.decreaseBytes(delta);
                    assertThat(i.getBytes()).isEqualTo(bytes - delta);
                    return null;
                },
                false
        );
    }

    @Test
    void increaseBytes__() throws IOException {
        applyNewInstance(
                i -> {
                    final var bytes = i.getBytes();
                    final var delta = ThreadLocalRandom.current().nextInt() >>> 24;
                    i.increaseBytes(delta);
                    assertThat(i.getBytes()).isEqualTo(bytes + delta);
                    return null;
                },
                false
        );
    }

    protected final <R> R applyNewInstanceUnchecked(
            final Function<? super T, ? extends R> function, final boolean close) {
        try {
            return applyNewInstance(function, close);
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    protected final <R> R applyNewInstance(final Function<? super T, ? extends R> function,
                                           final boolean close)
            throws IOException {
        Objects.requireNonNull(function, "function is null");
        final var instance = newInstance();
        try {
            return function.apply(instance);
        } finally {
            if (close) {
                instance.close();
            }
        }
    }

    protected T newInstance() {
        try {
            final var constructor = attachmentClass.getDeclaredConstructor();
            if (!constructor.canAccess(null)) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance();
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException("failed to construct " + attachmentClass, roe);
        }
    }

    protected final Class<T> attachmentClass;
}
