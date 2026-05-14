package com.github.jinahya.hello.miscellaneous;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.ECGenParameterSpec;

@Slf4j
class _Java_Security_KeyPairGenerator_Test {

    @Test
    void __RSA() throws NoSuchAlgorithmException {
        final var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(4096);
        final var generated = generator.generateKeyPair();
        final var privateKey = generated.getPrivate();
        final var publicKey = generated.getPublic();
        _Java_Security_Key_Test.__(privateKey);
        _Java_Security_Key_Test.__(publicKey);
    }

    @Test
    void __secp256r1() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        final var generator = KeyPairGenerator.getInstance("EC");
        final var spec = new ECGenParameterSpec("secp256r1");
        generator.initialize(spec);
        final var generated = generator.generateKeyPair();
        final var privateKey = generated.getPrivate();
        final var publicKey = generated.getPublic();
        _Java_Security_Key_Test.__(privateKey);
        _Java_Security_Key_Test.__(publicKey);
    }

    @ValueSource(strings = {
            "secp256r1", "secp384r1"
    })
    @ParameterizedTest
    void __EC(final String stdName)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        final var generator = KeyPairGenerator.getInstance("EC");
        final var spec = new ECGenParameterSpec(stdName);
        generator.initialize(spec);
        final var generated = generator.generateKeyPair();
        final var privateKey = generated.getPrivate();
        final var publicKey = generated.getPublic();
        _Java_Security_Key_Test.__(privateKey);
        _Java_Security_Key_Test.__(publicKey);
    }
}
