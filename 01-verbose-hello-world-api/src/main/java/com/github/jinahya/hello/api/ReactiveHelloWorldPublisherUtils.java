package com.github.jinahya.hello.api;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

/**
 * Package-private utilities shared by the
 * <a href="ReactiveHelloWorldPublishers.html">Reactive Streams publisher</a> implementations
 * ({@link ReactiveHelloWorldBytePublisher}, {@link ReactiveHelloWorldArrayPublisher}, and
 * {@link ReactiveHelloWorldStringPublisher}).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class ReactiveHelloWorldPublisherUtils {

    /**
     * Acquires the specified {@link Lock} and returns the value produced by the specified
     * {@link Callable}, releasing the lock when the callable returns or throws.
     * <p>
     * This is the primitive variant — checked exceptions thrown by the callable propagate as-is to
     * the caller. The {@link #lockAndGet(Lock, Supplier)} and {@link #lockAndRun(Lock, Runnable)}
     * overloads delegate to this one and wrap any checked exception as an unchecked
     * {@link RuntimeException}.
     *
     * @param lock     the {@link Lock} to {@linkplain Lock#lock() acquire} before invoking the
     *                 callable; must not be {@code null}.
     * @param callable the {@link Callable} whose value to return while the lock is held; must not
     *                 be {@code null}.
     * @param <V>      the type of the result produced by the {@code callable}.
     * @return the value returned by {@code callable.call()} invoked while the {@code lock} is
     * held.
     * @throws NullPointerException if either argument is {@code null}.
     * @throws Exception            if {@code callable.call()} throws.
     */
    static <V> V lockAndCall(final Lock lock, final Callable<? extends V> callable)
            throws Exception {
        lock.lock();
        try {
            return callable.call();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Acquires the specified {@link Lock} and returns the value supplied by the specified
     * {@link Supplier}, releasing the lock when the supplier returns or throws.
     * <p>
     * Typical use is to wrap a critical section that produces a single value — most often a
     * subscriber-signal site that must run under the publisher's signal-serialization lock
     * (Reactive Streams Rule 1.3). Delegates to {@link #lockAndCall(Lock, Callable)}.
     *
     * @param lock     the {@link Lock} to {@linkplain Lock#lock() acquire} before invoking the
     *                 supplier; must not be {@code null}.
     * @param supplier the {@link Supplier} whose value to return while the lock is held; must not
     *                 be {@code null}.
     * @param <T>      the type of the result produced by the {@code supplier}.
     * @return the value returned by {@code supplier.get()} invoked while the {@code lock} is
     * held.
     * @throws NullPointerException if either argument is {@code null}.
     * @throws RuntimeException     if {@code supplier.get()} throws; the original exception is the
     *                              {@linkplain Throwable#getCause() cause}.
     */
    static <T> T lockAndGet(final Lock lock, final Supplier<? extends T> supplier) {
        try {
            return lockAndCall(lock, supplier::get);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Acquires the specified {@link Lock} and runs the specified {@link Runnable}, releasing the
     * lock when the runnable returns or throws.
     * <p>
     * Typical use is to wrap a critical section that performs a side-effect without producing a
     * value — most often a subscriber-signal site (e.g.,
     * {@link org.reactivestreams.Subscriber#onNext(Object) onNext},
     * {@link org.reactivestreams.Subscriber#onError(Throwable) onError},
     * {@link org.reactivestreams.Subscriber#onComplete() onComplete}) that must run under the
     * publisher's signal-serialization lock (Reactive Streams Rule 1.3). Delegates to
     * {@link #lockAndCall(Lock, Callable)}.
     *
     * @param lock     the {@link Lock} to {@linkplain Lock#lock() acquire} before invoking the
     *                 runnable; must not be {@code null}.
     * @param runnable the {@link Runnable} to invoke while the lock is held; must not be
     *                 {@code null}.
     * @throws NullPointerException if either argument is {@code null}.
     * @throws RuntimeException     if {@code runnable.run()} throws; the original exception is the
     *                              {@linkplain Throwable#getCause() cause}.
     */
    static void lockAndRun(final Lock lock, final Runnable runnable) {
        try {
            lockAndCall(lock, () -> {
                runnable.run();
                return null;
            });
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ReactiveHelloWorldPublisherUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
