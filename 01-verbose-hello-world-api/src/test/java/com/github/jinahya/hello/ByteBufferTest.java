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

import com.github.jinahya.hello.miscellaneous.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.nio.*;
import java.time.*;
import java.util.concurrent.*;

/**
 * A class for exploring the state of {@link ByteBuffer} instances under various operations.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("ByteBuffer")
@Slf4j
class ByteBufferTest {

    @DisplayName(
            "should print state of a <ByteBuffer> after <allocate(32)>, <limit(25)>, <position(4)>")
    @Test
    void __() {
        final var buffer = ByteBuffer.allocate(32);
        _Java_Nio_ByteBuffer_TestUtils.print(buffer);
        buffer.limit(25);
        _Java_Nio_ByteBuffer_TestUtils.print(buffer);
        buffer.position(4);
        _Java_Nio_ByteBuffer_TestUtils.print(buffer);
    }

    @DisplayName("should print state of a <ByteBuffer> with <capacity> of <0>")
    @Test
    void __ZeroCapacity() {
        final var buffer = ByteBuffer.allocate(0);
        _Java_Nio_ByteBuffer_TestUtils.print(buffer);
    }

    @DisplayName("should print state changes across absolute and relative <get>/<put> operations")
    @Test
    void __AbsRel() {
        final var buffer = ByteBuffer.allocate(10);
        _Java_Nio_ByteBuffer_TestUtils.print(buffer);
        {
            final var b = buffer.get();
            _Java_Nio_ByteBuffer_TestUtils.print(buffer);
        }
        {
            final var b = buffer.get(buffer.position());
            _Java_Nio_ByteBuffer_TestUtils.print(buffer);
        }
        {
            buffer.put(new byte[2]);
            _Java_Nio_ByteBuffer_TestUtils.print(buffer);
        }
        {
            buffer.get(1, new byte[5]);
            _Java_Nio_ByteBuffer_TestUtils.print(buffer);
        }
    }

    @DisplayName("should delegate <calculateAge(person)> to <calculateAgeAt(person, now())>")
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

    @DisplayName("""
            should print state of a <ByteBuffer> after <position(1)>,
            <limit(9)>, then <position += 3>""")
    @Test
    void __29() {
        final var buffer = ByteBuffer.allocate(10).position(1).limit(9);
//        JavaNioByteBufferUtils.print(buffer);
        _Java_Nio_Buffer_TestUtils.print(buffer);
        buffer.position(buffer.position() + 3);
        _Java_Nio_Buffer_TestUtils.print(buffer);
    }

    @DisplayName("int buffer view")
    @Nested
    class IntBufferTest {

        @DisplayName("should view a <ByteBuffer> as an <IntBuffer> and read <Integer.MAX_VALUE>")
        @Test
        void __() {
            final var bbuf = ByteBuffer.allocate(Integer.BYTES);
            _Java_Nio_ByteBuffer_TestUtils.print(bbuf);
            bbuf.put(0, new byte[] {(byte) 0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
            _Java_Nio_ByteBuffer_TestUtils.print(bbuf);
            final var ibuf = bbuf.asIntBuffer();
            assert ibuf.position() == 0;
            assert ibuf.capacity() == bbuf.remaining() / Integer.BYTES;
            assert ibuf.limit() == bbuf.remaining() / Integer.BYTES;
            _Java_Nio_Buffer_TestUtils.print(ibuf);
            final var i = ibuf.get();
            assert i == Integer.MAX_VALUE;
            _Java_Nio_Buffer_TestUtils.print(ibuf);
        }
    }

    @DisplayName("manipulation")
    @Nested
    class ManipulationTest {

        @DisplayName("should reset position and limit when <clear()> is invoked")
        @Test
        void __clear() {
            final var b = ByteBuffer.allocate(10);
            _Java_Nio_Buffer_TestUtils.print(b);
            b.limit(7).position(4).mark().position(6);
            _Java_Nio_Buffer_TestUtils.print(b);
            b.clear();
            _Java_Nio_Buffer_TestUtils.print(b);
        }

        @DisplayName("should swap position and limit when <flip()> is invoked")
        @Test
        void __flip() {
            final var b = ByteBuffer.allocate(10);
            _Java_Nio_Buffer_TestUtils.print(b);
            b.put(new byte[5]);
            _Java_Nio_Buffer_TestUtils.print(b);
            b.flip();
            _Java_Nio_Buffer_TestUtils.print(b);
        }

        @DisplayName("should reset position to <0> when <rewind()> is invoked")
        @Test
        void __rewind() {
            final var b = ByteBuffer.allocate(10).limit(7).position(4);
            _Java_Nio_Buffer_TestUtils.print(b);
            b.rewind();
            _Java_Nio_Buffer_TestUtils.print(b);
        }

        @DisplayName("""
                should shift remaining bytes to the start
                when <compact()> is invoked with <position> below <limit>""")
        @Test
        void __compact1() {
            final var b = ByteBuffer.allocate(10);
            b.put(new byte[8]);
            _Java_Nio_Buffer_TestUtils.print(b);
            b.flip();
            _Java_Nio_Buffer_TestUtils.print(b);
            b.position(3);
            _Java_Nio_Buffer_TestUtils.print(b);
            b.compact();
            _Java_Nio_Buffer_TestUtils.print(b);
        }

        @DisplayName("""
                should leave buffer empty
                when <compact()> is invoked with <position> equal to <limit>""")
        @Test
        void __compact2() {
            final var b = ByteBuffer.allocate(10);
            b.put(new byte[8]);                        // position=8, limit=10
            b.flip();                                  // position=0, limit=8
            _Java_Nio_Buffer_TestUtils.print(b);
            b.position(8);
            _Java_Nio_Buffer_TestUtils.print(b);
            b.compact();
            _Java_Nio_Buffer_TestUtils.print(b);
        }
    }

    @DisplayName("mark and reset")
    @Nested
    class MarkResetTest {

        @DisplayName("should restore position to the marked value when <reset()> is invoked")
        @Test
        void __markReset() {
            final var b = ByteBuffer.allocate(10);
            _Java_Nio_Buffer_TestUtils.print(b);
            b.position(3).mark().position(7);
            _Java_Nio_Buffer_TestUtils.print(b);
            b.reset();
            _Java_Nio_Buffer_TestUtils.print(b);
        }
    }

    @DisplayName("should reflect explicit <position> and <limit> assignments")
    @Test
    void __positionLimit() {
        final var b = ByteBuffer.allocate(10);
        _Java_Nio_Buffer_TestUtils.print(b);
        b.limit(7).position(3);
        _Java_Nio_Buffer_TestUtils.print(b);
    }

    @DisplayName(
            "should reflect <remaining> and <hasRemaining> as position advances toward <limit>")
    @Test
    void __remainingHasRemaining() {
        final var b = ByteBuffer.allocate(10).limit(8).position(3);
        _Java_Nio_Buffer_TestUtils.print(b);
        b.position(8);
        _Java_Nio_Buffer_TestUtils.print(b);
    }
}
