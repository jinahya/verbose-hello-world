package com.github.jinahya.hello.java.security;

/*-
 * #%L
 * verbose-hello-world-etc
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

import com.github.jinahya.hello.util.java.security.PrivateKeyUtils;
import com.github.jinahya.hello.util.java.security.PublicKeyUtils;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static org.assertj.core.api.Assertions.assertThat;

class KeyPairTest {

    @ValueSource(strings = {"RSA"})
    @ParameterizedTest
    void __(final String algorithm, @TempDir final Path tempDir)
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        final var generator = KeyPairGenerator.getInstance(algorithm);
        final var keyPair = generator.generateKeyPair();
//        final var publicKey = keyPair.getPublic();
//        final var privateKey = keyPair.getPrivate();
        final var keyFactory = KeyFactory.getInstance(algorithm);
        {
            final var path = Files.createTempFile(tempDir, null, null);
            Files.write(path, keyPair.getPublic().getEncoded());
        }
        {
            final var keySpec = new X509EncodedKeySpec(keyPair.getPublic().getEncoded());
            final var publicKey1 = keyFactory.generatePublic(keySpec);
            assertThat(publicKey1).isEqualTo(keyPair.getPublic());
            final var path = Files.createTempFile(tempDir, null, null);
            PublicKeyUtils.writeEncoded(keyPair.getPublic(), path);
            Files.write(path, keySpec.getEncoded());
            final var publicKey2 = PublicKeyUtils.readEncoded(path, keyFactory);
            assertThat(publicKey2).isEqualTo(publicKey1);
        }
        {
            final var keySpec = new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded());
            final var privateKey1 = keyFactory.generatePrivate(keySpec);
            assertThat(privateKey1).isEqualTo(keyPair.getPrivate());
            final var path = Files.createTempFile(tempDir, null, null);
            PrivateKeyUtils.writeEncoded(keyPair.getPrivate(), path);
            final var privateKey2 = PrivateKeyUtils.readEncoded(path, keyFactory);
            assertThat(privateKey2).isEqualTo(privateKey1);
        }
    }
}
