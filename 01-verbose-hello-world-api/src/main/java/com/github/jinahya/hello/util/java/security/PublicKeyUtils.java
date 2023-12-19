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

import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.function.Function;

public final class PublicKeyUtils {

    private static final Function<byte[], EncodedKeySpec> X509 = X509EncodedKeySpec::new;

    public static void writeEncoded(
            final PublicKey key, final Function<? super byte[], ? extends EncodedKeySpec> function,
            final Path path)
            throws IOException {
        KeyUtils.writeEncoded(key, function, path);
    }

    public static void writeEncoded(final PublicKey key, final Path path)
            throws IOException {
        writeEncoded(key, X509, path);
    }

    public static PublicKey readEncoded(
            final Path path, final Function<? super byte[], ? extends EncodedKeySpec> function,
            final KeyFactory factory)
            throws IOException {
        return KeyUtils.readEncoded(
                path,
                function,
                ks -> {
                    try {
                        return factory.generatePublic(ks);
                    } catch (InvalidKeySpecException ikse) {
                        throw new RuntimeException(
                                "failed to generate public key with " + ks,
                                ikse
                        );
                    }
                }
        );
    }

    public static PublicKey readEncoded(final Path path, final KeyFactory factory)
            throws IOException {
        return readEncoded(path, X509, factory);
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private PublicKeyUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
