package com.github.jinahya.hello.miscellaneous;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ChatTcpConstants {

    static final int MESSAGE_ADDR_LENGTH_OFFSET = 0;

    static final int MESSAGE_ADDR_LENGTH_LENGTH = 1;

    static final int MESSAGE_ADDR_OFFSET = MESSAGE_ADDR_LENGTH_OFFSET + MESSAGE_ADDR_LENGTH_LENGTH;

    static final int MESSAGE_ADDR_LENGTH = 16;

    static final int MESSAGE_PORT_OFFSET = MESSAGE_ADDR_OFFSET + MESSAGE_ADDR_LENGTH;

    static final int MESSAGE_PORT_LENGTH = 2;

    static final int MESSAGE_CONTENT_LENGTH_OFFSET = MESSAGE_PORT_OFFSET + MESSAGE_PORT_LENGTH;

    static final int MESSAGE_CONTENT_LENGTH_LENGTH = 1;

    static final int MESSAGE_CONTENT_OFFSET = MESSAGE_CONTENT_LENGTH_OFFSET
                                              + MESSAGE_CONTENT_LENGTH_LENGTH;

    static final int MESSAGE_CONTENT_LENGTH = 255;

    public static final int MESSAGE_LENGTH = MESSAGE_ADDR_LENGTH_LENGTH
                                             + MESSAGE_ADDR_LENGTH
                                             + MESSAGE_PORT_LENGTH
                                             + MESSAGE_CONTENT_LENGTH_LENGTH
                                             + MESSAGE_CONTENT_LENGTH;

    public static final int PORT = 7 + 40000;

    private ChatTcpConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
