package com.github.jinahya.hello.miscellaneous.c03chat;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
final class _ChatConstants {

    static final int PORT = 7 + 40000;

    static final String USER_NAME =
            Optional.ofNullable(System.getProperty("user.name")).orElse("unknown");

    private _ChatConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
