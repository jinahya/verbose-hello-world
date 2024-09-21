package com.github.jinahya.hello.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public final class JavaLangReflectUtils {

    public static Object loggingProxy(final Set<Class<?>> interfaceClasses, final Object obj) {
        return Proxy.newProxyInstance(
                obj.getClass().getClassLoader(),
                interfaceClasses.toArray(new Class<?>[0]),
                (p, m, a) -> {
                    if (interfaceClasses.contains(m.getDeclaringClass())) {
                        final var s = Optional.ofNullable(a)
                                .filter(v -> v.length > 0)
                                .map(v -> Arrays.stream(v).map(Object::toString)
                                        .collect(Collectors.joining(", ")))
                                .orElse("");
                        log.debug("{}.{}({})", obj, m.getName(), s);
                    }
                    return m.invoke(obj, a);
                }
        );
    }

    private JavaLangReflectUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
