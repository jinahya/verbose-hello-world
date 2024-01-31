package com.github.jinahya.hello.util;

import java.util.concurrent.Executor;

public final class JavaUtilConcurrentExecutorUtils {

    /**
     * Returns an executor runs submitted commands in current thread.
     *
     * @return an executor runs commands in current thread.
     * @see <a href="https://stackoverflow.com/q/6581188/330457">Is there an ExecutorService that
     * uses the current thread?</a>
     */
    public static Executor ofCurrentThread() {
        return Runnable::run;
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private JavaUtilConcurrentExecutorUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
