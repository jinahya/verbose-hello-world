package com.github.jinahya.hello.util.java.security;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
