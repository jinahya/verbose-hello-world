package com.github.jinahya.hello.misc;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class JavaLangTest {

    @DisplayName("byte to char")
    @RepeatedTest(1024)
    void byteToChar() {
        byte b = (byte) ThreadLocalRandom.current().nextInt();
        char c = (char) b;
        assertEquals(b, (byte) c);
    }
}
