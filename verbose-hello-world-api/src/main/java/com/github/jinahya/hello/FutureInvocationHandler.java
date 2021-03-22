package com.github.jinahya.hello;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.lang.reflect.Proxy.newProxyInstance;
import static java.util.Objects.requireNonNull;

class FutureInvocationHandler<V> implements InvocationHandler {

    @SuppressWarnings({"unchecked"})
    static <V> Future<V> newProxyInstanceFor(final Future<V> future) {
        requireNonNull(future, "future is null");
        return (Future<V>) newProxyInstance(future.getClass().getClassLoader(), new Class<?>[] {Future.class},
                                            new FutureInvocationHandler<>(future));
    }

    FutureInvocationHandler(final Future<V> future) {
        super();
        this.future = requireNonNull(future, "future is null");
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        try {
            return method.invoke(future, args);
        } catch (final InvocationTargetException ite) {
            final Throwable cause = ite.getCause();
            if (method.getDeclaringClass() == Future.class && method.getName().equals("get")) {
                if (cause instanceof ExecutionException) {
                    final Throwable cause2 = cause.getCause();
                    if (cause2 instanceof InterruptedException) {
                        throw (InterruptedException) cause2;
                    }
                }
            }
            throw cause;
        }
    }

    private final Future<V> future;
}
