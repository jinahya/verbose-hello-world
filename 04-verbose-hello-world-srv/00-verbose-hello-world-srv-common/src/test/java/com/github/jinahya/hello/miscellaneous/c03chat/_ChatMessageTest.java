package com.github.jinahya.hello.miscellaneous.c03chat;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class _ChatMessageTest {

    private static Stream<String> getMessageStream() {
        return Stream.of(
                "У нього було сиве волосся.",
                "Trovu, kiu vizitas vian profilon.",
                "There is no public toilet.",
                "Ur zmireɣ ad d-sɣeɣ tasewlaft yelhan am tin-nnem.",
                "La réunion s'est tenue hier.",
                "私はあなたのご両親にかなり気に入られているようです。",
                "汝英語會話係啵？",
                "어떻게 그의 아버지가 저 남자의 정체를 간파한 것인지 알 수 없어요.",
                """
                        その問いかけに答えるチャンスを今、私たちは手にしました。
                        今この時こそが、私たちの瞬間です。
                        今この時にこそ、私たちは人々がまた仕事につけるようにしなくてはなりません。
                        子供たちのために、チャンスの扉を開かなくてはなりません。
                        繁栄を取り戻し、平和を推進しなくてはなりません。
                        今この時にこそ、アメリカの夢を取り戻し、基本的な真理を再確認しなくてはなりません。
                        大勢の中にあって、私たちはひとつなのだと。
                        息をし続ける限り、私たちは希望をもち続けるのだと。
                        そして疑り深く悲観し否定する声に対しては、そんなことできないという人たちに対しては、ひとつ国民の魂を端的に象徴するあの不朽の信条でもって、必ずやこう答えましょう。"""
        );
    }

    @RepeatedTest(1)
    void int__Array() {
        var array = new byte[Integer.BYTES];
        var length = ThreadLocalRandom.current().nextInt(array.length) + 1;
        var offset = Integer.BYTES - length;
        var expected = ThreadLocalRandom.current().nextInt() >>> (offset * Byte.SIZE);
        _ChatMessage.setInt(array, offset, length, expected);
        var actual = _ChatMessage.getInt(array, offset, length);
        assertEquals(expected, actual);
    }

    @RepeatedTest(1)
    void int__Buffer() {
        var array = ByteBuffer.allocate(Integer.BYTES);
        var length = ThreadLocalRandom.current().nextInt(array.capacity()) + 1;
        var offset = Integer.BYTES - length;
        var expected = ThreadLocalRandom.current().nextInt() >>> (offset * Byte.SIZE);
        _ChatMessage.setInt(array, offset, length, expected);
        var actual = _ChatMessage.getInt(array, offset, length);
        assertEquals(expected, actual);
    }

    @RepeatedTest(1)
    void long__Array() {
        var array = new byte[Long.BYTES];
        var length = ThreadLocalRandom.current().nextInt(array.length) + 1;
        var offset = Long.BYTES - length;
        var expected = ThreadLocalRandom.current().nextLong() >>> (offset * Byte.SIZE);
        _ChatMessage.setLong(array, offset, length, expected);
        var actual = _ChatMessage.getLong(array, offset, length);
        assertEquals(expected, actual);
    }

    @RepeatedTest(1)
    void long__Buffer() {
        var array = ByteBuffer.allocate(Long.BYTES);
        var length = ThreadLocalRandom.current().nextInt(array.capacity()) + 1;
        var offset = Long.BYTES - length;
        var expected = ThreadLocalRandom.current().nextLong() >>> (offset * Byte.SIZE);
        _ChatMessage.setLong(array, offset, length, expected);
        var actual = _ChatMessage.getLong(array, offset, length);
        assertEquals(expected, actual);
    }

    @Test
    void getTimestamp__Array() {
        var array = new byte[_ChatMessage.BYTES];
        var actual = _ChatMessage.getTimestamp(array);
        assertEquals(0, actual);
    }

    @Test
    void getTimestamp__Buffer() {
        var array = ByteBuffer.allocate(_ChatMessage.BYTES);
        var actual = _ChatMessage.getTimestamp(array);
        assertEquals(0, actual);
    }

    @Test
    void setTimestamp__Array() {
        var array = new byte[_ChatMessage.BYTES];
        var timestamp = System.currentTimeMillis();
        _ChatMessage.setTimestamp(array, timestamp);
        assertEquals(timestamp, _ChatMessage.getTimestamp(array));
    }

    @Test
    void setTimestamp__Buffer() {
        var buffer = ByteBuffer.allocate(_ChatMessage.BYTES);
        var timestamp = System.currentTimeMillis();
        _ChatMessage.setTimestamp(buffer, timestamp);
        assertEquals(timestamp, _ChatMessage.getTimestamp(buffer));
    }

    @Test
    void getMessage_Blank_Array() {
        var array = new byte[_ChatMessage.BYTES];
        var message = _ChatMessage.getMessage(array);
        assertTrue(message.isBlank());
    }

    @Test
    void getMessage_Blank_Buffer() {
        var array = ByteBuffer.allocate(_ChatMessage.BYTES);
        var message = _ChatMessage.getMessage(array);
        assertTrue(message.isBlank());
    }

    @DisplayName("setMessage(array, message)")
    @MethodSource({"getMessageStream"})
    @ParameterizedTest
    void setMessage__Array(String message) {
        var array = _ChatMessage.newArray();
        _ChatMessage.setMessage(array, message);
        assertTrue(message.startsWith(_ChatMessage.getMessage(array)));
    }

    @DisplayName("setMessage(buffer, message)")
    @MethodSource({"getMessageStream"})
    @ParameterizedTest
    void setMessage__Buffer(String expected) {
        var buffer = _ChatMessage.newBuffer();
        _ChatMessage.setMessage(buffer, expected);
        var actual = _ChatMessage.getMessage(buffer);
        assertTrue(expected.startsWith(actual));
    }

    @Test
    void toString__Array() {
        var array = _ChatMessage.newArray();
        var string = _ChatMessage.toString(array);
        assertFalse(string.isBlank());
    }

    @Test
    void toString__Buffer() {
        var buffer = _ChatMessage.newBuffer();
        var string = _ChatMessage.toString(buffer);
        assertFalse(string.isBlank());
    }
}
