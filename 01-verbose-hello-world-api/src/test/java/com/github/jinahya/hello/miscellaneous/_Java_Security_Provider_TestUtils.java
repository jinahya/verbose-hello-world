package com.github.jinahya.hello.miscellaneous;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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

import lombok.extern.slf4j.*;
import org.bouncycastle.jce.provider.*;
import org.junit.jupiter.api.*;

import java.security.*;
import java.util.*;
import java.util.function.*;

@DisplayName("Security")
@Slf4j
class _Java_Security_Provider_TestUtils {

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    static void acceptEachProvider(final Consumer<? super Provider> consumer) {
        Arrays.stream(Security.getProviders()).forEach(consumer);
    }

    static void acceptProviderAndService(
            final BiConsumer<? super Provider, ? super Provider.Service> consumer) {
        acceptEachProvider(p -> {
            p.getServices().forEach(s -> consumer.accept(p, s));
        });
    }

    static void applyProviderAndAcceptEachService(
            final Function<
                    ? super Provider,
                    ? extends Consumer<? super Provider.Service>> function) {
        acceptProviderAndService((p, s) -> function.apply(p).accept(s));
    }

    private _Java_Security_Provider_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
