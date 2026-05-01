package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.util.Base64;

@Slf4j
class _Java_Security_Key_Test {

    static void __(final Key key) {
        final var encoded = key.getEncoded();
        System.out.printf(
                "%16s %10s %10d %s%n",
                key.getAlgorithm(),
                key.getFormat(),
                encoded.length,
                Base64.getEncoder().encodeToString(encoded)
        );
    }

    private _Java_Security_Key_Test() {
        throw new AssertionError("instantiation is not allowed");
    }
}