package com.github.jinahya.hello.util.java.security;

import com.github.jinahya.hello.util.ExcludeFromCoverage_PrivateConstructor_Obviously;

import java.security.MessageDigest;

public final class MessageDigestConstants {

    public static final String CRYPTO_SERVICE = MessageDigest.class.getSimpleName();

    // ---------------------------------------------------------------------------------------------
    @ExcludeFromCoverage_PrivateConstructor_Obviously
    private MessageDigestConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
