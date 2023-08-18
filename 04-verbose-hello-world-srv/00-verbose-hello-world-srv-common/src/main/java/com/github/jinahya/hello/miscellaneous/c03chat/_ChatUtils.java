package com.github.jinahya.hello.miscellaneous.c03chat;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
class _ChatUtils {

    static String prependUsername(String message) {
        Objects.requireNonNull(message, "message is null");
        return '[' + _ChatConstants.USER_NAME + "] " + message;
    }

    private _ChatUtils() {
        throw new IllegalArgumentException("");
    }
}
