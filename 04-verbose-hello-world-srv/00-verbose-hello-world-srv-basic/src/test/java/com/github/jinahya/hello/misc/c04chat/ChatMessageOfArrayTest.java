package com.github.jinahya.hello.misc.c04chat;

import org.junit.jupiter.api.Nested;

@Nested
class ChatMessageOfArrayTest extends ChatMessageTest<ChatMessage.OfArray> {

    ChatMessageOfArrayTest() {
        super(ChatMessage.OfArray.class);
    }
}
