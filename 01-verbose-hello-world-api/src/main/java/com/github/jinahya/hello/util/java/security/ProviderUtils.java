package com.github.jinahya.hello.util.java.security;

import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;

import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProviderUtils {

    static Stream<Provider.Service> getServiceStream() {
        return Arrays.stream(Security.getProviders())
                .flatMap(p -> p.getServices().stream());
    }

    public static Set<Provider.Service> getAllServices() {
        return getServiceStream().collect(Collectors.toSet());
    }

    static Stream<String> getServiceTypeStream() {
        return getServiceStream().map(Provider.Service::getType);
    }

    public static Set<String> getAllServiceTypes() {
        return getServiceTypeStream().collect(Collectors.toSet());
    }

//    public static String getServiceType(final Class<?> cls) {
//        Objects.requireNonNull(cls, "cls is null");
//    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private ProviderUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
