package com.github.jinahya.hello.util.java.security;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.security.Security;

@Slf4j
class MessageDigestUtilsTest {

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//        Security.insertProviderAt(new gnu.crypto.jce.GnuCrypto(), 1);
//        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    @ValueSource(strings = {
            "SHA-1",
            "SHA-256"
    })
    @ParameterizedTest
    void getProviders__(final String algorithm) {
        for (final var provider : MessageDigestUtils.getProviders(algorithm)) {
            log.debug("provider: {}", provider);
        }
    }
}
