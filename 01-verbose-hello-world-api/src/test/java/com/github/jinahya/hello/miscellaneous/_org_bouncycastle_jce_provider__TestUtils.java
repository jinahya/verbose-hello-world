package com.github.jinahya.hello.miscellaneous;

import org.bouncycastle.jce.provider.*;

import java.security.*;

public class _org_bouncycastle_jce_provider__TestUtils {

    public static void addBouncyCastleProvider() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    private _org_bouncycastle_jce_provider__TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
