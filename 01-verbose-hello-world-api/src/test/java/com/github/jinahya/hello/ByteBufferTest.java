package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.github.jinahya.hello.api.util.JavaNioBufferUtils;
import com.github.jinahya.hello.api.util.JavaNioByteBufferUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class ByteBufferTest {

    @DisplayName("32")
    @Test
    void __() {
        final var buffer = ByteBuffer.allocate(32);
        JavaNioByteBufferUtils.print(buffer);
        buffer.limit(25);
        JavaNioByteBufferUtils.print(buffer);
        buffer.position(4);
        JavaNioByteBufferUtils.print(buffer);
    }

    @DisplayName("오늘 태어난 아기는 0살이다")
    @Test
    void __ZeroCapacity() {
        final var buffer = ByteBuffer.allocate(0);
        JavaNioByteBufferUtils.print(buffer);
    }

    @Test
    void __AbsRel() {
        final var buffer = ByteBuffer.allocate(10);
        JavaNioByteBufferUtils.print(buffer);
        {
            final var b = buffer.get();
            JavaNioByteBufferUtils.print(buffer);
        }
        {
            final var b = buffer.get(buffer.position());
            JavaNioByteBufferUtils.print(buffer);
        }
        {
            buffer.put(new byte[2]);
            JavaNioByteBufferUtils.print(buffer);
        }
        {
            buffer.get(1, new byte[5]);
            JavaNioByteBufferUtils.print(buffer);
        }
    }

    @DisplayName("calculateAge(person) should return calculateAgeAt(person, now())")
    @Test
    void calculateAge_InvokeCalculateAgeAtWithPersonAndNow_() {
        final var now = LocalDate.now();
        try (var mockedStatic = Mockito.mockStatic(LocalDate.class)) {
            mockedStatic.when(LocalDate::now).thenReturn(now);
            // given
            final Mockito_Test.Person person = Mockito.mock();
            BDDMockito.given(person.getBirthDate()).willReturn(now);                  // <1>
            final Mockito_Test.PersonService service = Mockito.spy();
            final var expected = ThreadLocalRandom.current().nextInt(128);
            BDDMockito.willReturn(expected).given(service)                            // <2>
                    .calculateAgeAt(ArgumentMatchers.any(), ArgumentMatchers.any());
            // when
            final var age = service.calculateAge(person);
            // then
            final var dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
            BDDMockito.then(service).should(Mockito.times(1))                         // <3>
                    .calculateAgeAt(ArgumentMatchers.same(person), dateCaptor.capture());
            Assertions.assertEquals(now, dateCaptor.getValue());
            Assertions.assertEquals(expected, age);
        }
    }

    @Test
    void __29() {
        final var buffer = ByteBuffer.allocate(10).position(1).limit(9);
//        JavaNioByteBufferUtils.print(buffer);
        JavaNioBufferUtils.print(buffer);
        buffer.position(buffer.position() + 3);
        JavaNioBufferUtils.print(buffer);
    }

    @Nested
    class IntBufferTest {

        @Test
        void __() {
            final var bbuf = ByteBuffer.allocate(Integer.BYTES);
            JavaNioByteBufferUtils.print(bbuf);
            bbuf.put(0, new byte[] {(byte) 0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
            JavaNioByteBufferUtils.print(bbuf);
            final var ibuf = bbuf.asIntBuffer();
            assert ibuf.position() == 0;
            assert ibuf.capacity() == bbuf.remaining() / Integer.BYTES;
            assert ibuf.limit() == bbuf.remaining() / Integer.BYTES;
            JavaNioBufferUtils.print(ibuf);
            final var i = ibuf.get();
            assert i == Integer.MAX_VALUE;
            JavaNioBufferUtils.print(ibuf);
        }
    }

    @Nested
    class ManipulationTest {

        @Test
        void __clear() {
            final var b = ByteBuffer.allocate(10);
            JavaNioBufferUtils.print(b);
            b.limit(7).position(4).mark().position(6);
            JavaNioBufferUtils.print(b);
            b.clear();
            JavaNioBufferUtils.print(b);
        }

        @Test
        void __flip() {
            final var b = ByteBuffer.allocate(10);
            JavaNioBufferUtils.print(b);
            b.put(new byte[5]);
            JavaNioBufferUtils.print(b);
            b.flip();
            JavaNioBufferUtils.print(b);
        }

        @Test
        void __rewind() {
            final var b = ByteBuffer.allocate(10).limit(7).position(4);
            JavaNioBufferUtils.print(b);
            b.rewind();
            JavaNioBufferUtils.print(b);
        }

        @Test
        void __compact1() {
            final var b = ByteBuffer.allocate(10);
            b.put(new byte[8]);
            JavaNioBufferUtils.print(b);
            b.flip();
            JavaNioBufferUtils.print(b);
            b.position(3);
            JavaNioBufferUtils.print(b);
            b.compact();
            JavaNioBufferUtils.print(b);
        }

        @Test
        void __compact2() {
            final var b = ByteBuffer.allocate(10);
            b.put(new byte[8]);                        // position=8, limit=10
            b.flip();                                  // position=0, limit=8
            JavaNioBufferUtils.print(b);
            b.position(8);
            JavaNioBufferUtils.print(b);
            b.compact();
            JavaNioBufferUtils.print(b);
        }
    }

    @Nested
    class MarkResetTest {

        @Test
        void __markReset() {
            final var b = ByteBuffer.allocate(10);
            JavaNioBufferUtils.print(b);
            b.position(3).mark().position(7);
            JavaNioBufferUtils.print(b);
            b.reset();
            JavaNioBufferUtils.print(b);
        }
    }

    @Test
    void __positionLimit() {
        final var b = ByteBuffer.allocate(10);
        JavaNioBufferUtils.print(b);
        b.limit(7).position(3);
        JavaNioBufferUtils.print(b);
    }

    @Test
    void __remainingHasRemaining() {
        final var b = ByteBuffer.allocate(10).limit(8).position(3);
        JavaNioBufferUtils.print(b);
        b.position(8);
        JavaNioBufferUtils.print(b);
    }
}
