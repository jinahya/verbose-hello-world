package com.github.jinahya.hello.miscellaneous;

import com.github.jinahya.hello.api._Javax_Crypto_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;

@Slf4j
class _Javax_Crypto_KeyGenerator_Test {

    // ELSIE PREPARE TO MEET THY GOD
    @Test
    void __() throws NoSuchAlgorithmException {
        for (var e : _Javax_Crypto_TestUtils.KEY_GENERATOR_ALGORITHMS_AND_KEYSIZES.entrySet()) {
            final var algorithm = e.getKey();
            final var keysizes = e.getValue();
            final var generator = KeyGenerator.getInstance(algorithm);
            if (keysizes.isEmpty()) {
                final var generated = generator.generateKey();
                _Java_Security_Key_Test.__(generated);
            } else {
                for (var keysize : keysizes) {
                    generator.init(keysize);
                    final var generated = generator.generateKey();
                    _Java_Security_Key_Test.__(generated);
                }
            }
        }
    }
}