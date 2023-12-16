package com.github.jinahya.hello.util.java.security;

import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;

import java.security.MessageDigest;

public final class MessageDigestConstants {

    public static final String CRYPTO_SERVICE = MessageDigest.class.getSimpleName();

    public static final String ALGORITHM_MD5 = "MD5";

    public static final String ALGORITHM_SHA_1 = "SHA-1";

    // ---------------------------------------------------------------------------------------------
    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private MessageDigestConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
