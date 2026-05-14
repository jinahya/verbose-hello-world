package com.github.jinahya.hello.miscellaneous;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.security.Security;

@Slf4j
class _Java_Security_Security_Test {

    @Test
    void getProviders__() {
        for (final var provider : Security.getProviders()) {
            System.out.printf("%s%n", provider);
            for (final var service : provider.getServices()) {
                final String type = service.getType();
                final String algorithm = service.getAlgorithm();
                System.out.printf("\t%s / %s%n", type, algorithm);
            }
        }
    }
}
