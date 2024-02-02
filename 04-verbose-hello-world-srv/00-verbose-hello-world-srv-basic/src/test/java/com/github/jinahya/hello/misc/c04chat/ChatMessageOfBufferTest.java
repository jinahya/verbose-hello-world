package com.github.jinahya.hello.misc.c04chat;

import org.junit.jupiter.api.Nested;

@Nested
class ChatMessageOfBufferTest extends ChatMessageTest<ChatMessage.OfBuffer> {

    ChatMessageOfBufferTest() {
        super(ChatMessage.OfBuffer.class);
    }
}
