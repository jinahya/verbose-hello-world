package com.github.jinahya.hello.miscellaneous.c03chat;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class _ChatConstants {

    public static final int PORT = 7 + 40000;

    private _ChatConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
