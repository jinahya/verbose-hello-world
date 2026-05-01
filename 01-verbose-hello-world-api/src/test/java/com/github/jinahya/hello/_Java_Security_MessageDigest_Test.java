package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.security.Security;

@Slf4j
class _Java_Security_MessageDigest_Test {

    @Test
    void algorithms__() {
        for (var algorithm : Security.getAlgorithms("MessageDigest")) {
            System.out.printf("%s%n", algorithm);
        }
    }
}
