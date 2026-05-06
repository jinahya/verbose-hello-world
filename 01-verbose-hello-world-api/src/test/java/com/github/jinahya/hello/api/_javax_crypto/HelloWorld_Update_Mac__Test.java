package com.github.jinahya.hello.api._javax_crypto;

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Update_Mac__Test
        extends HelloWorldTest {

    private static void printf(final String algorithm, final Object parameter, final byte[] tag) {
        final var encoded = Base64.getEncoder().encodeToString(tag);
        System.out.printf("%30s %20s (%4d) %s...%s%n", algorithm,
                          Optional.ofNullable(parameter).orElse(""),
                          tag.length,
                          encoded.substring(0, 4),
                          encoded.substring(encoded.length() - 4));
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() {
        Mockito.doAnswer(i -> {
            final var mac = i.getArgument(0, Mac.class);
            mac.update(HelloWorldTestUtils.hello_world_byte_array());
            return mac;
        }).when(service()).update(ArgumentMatchers.<Mac>notNull());
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("HmacSHA1")
    @Nested
    class HmacSha1_Test {

        private static final String ALGORITHM = "HmacSHA1";

        @Test
        void __() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var generator = KeyGenerator.getInstance(ALGORITHM);
            final var key = generator.generateKey();
            final var mac = Mac.getInstance(ALGORITHM);
            mac.init(key);
            // -------------------------------------------------------------------------------- when
            service().update(mac);
            final var tag = mac.doFinal();
            // -------------------------------------------------------------------------------- then
            printf(ALGORITHM, null, tag);
        }
    }

    @DisplayName("HmacSHA256")
    @Nested
    class HmacSHA256_Test {

        private static final String ALGORITHM = "HmacSHA256";

        @Test
        void __() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var generator = KeyGenerator.getInstance(ALGORITHM);
            final var key = generator.generateKey();
            final var mac = Mac.getInstance(ALGORITHM);
            mac.init(key);
            // -------------------------------------------------------------------------------- when
            service().update(mac);
            final var tag = mac.doFinal();
            // -------------------------------------------------------------------------------- then
            printf(ALGORITHM, null, tag);
        }
    }

    @DisplayName("PBEWithHmacSHA256")
    @Nested
    class PBEWithHmacSHA256_Test {

        private static final String ALGORITHM = "PBEWithHmacSHA256";

        private static final String KEY_FACTORY_ALGORITHM = ALGORITHM + "AndAES_256";

        @Test
        void __() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var password = "password".toCharArray();
            final var keySpec = new PBEKeySpec(password);
            final var factory = SecretKeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
            final var key = factory.generateSecret(keySpec);
            final var salt = new byte[16];
            ThreadLocalRandom.current().nextBytes(salt);
            final var iterationCount = 1000;
            final var params = new PBEParameterSpec(salt, iterationCount);
            final var mac = Mac.getInstance(ALGORITHM);
            mac.init(key, params);
            // -------------------------------------------------------------------------------- when
            service().update(mac);
            final var tag = mac.doFinal();
            // -------------------------------------------------------------------------------- then
            printf(ALGORITHM, iterationCount, tag);
        }
    }
}
