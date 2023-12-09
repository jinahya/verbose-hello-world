package com.github.jinahya.hello.util.java.security;

import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.stream.Stream;

public final class SecurityUtils {

    static Stream<Provider> providerStream() {
        return Arrays.stream(Security.getProviders());
    }

    private SecurityUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
