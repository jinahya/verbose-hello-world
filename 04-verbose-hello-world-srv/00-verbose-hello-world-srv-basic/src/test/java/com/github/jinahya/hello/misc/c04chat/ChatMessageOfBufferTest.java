package com.github.jinahya.hello.misc.c04chat;

import org.junit.jupiter.api.Nested;

@Nested
class ChatMessageOfBufferTest extends ChatMessageTest<_ChatMessage.OfBuffer> {

    ChatMessageOfBufferTest() {
        super(_ChatMessage.OfBuffer.class);
    }
}
