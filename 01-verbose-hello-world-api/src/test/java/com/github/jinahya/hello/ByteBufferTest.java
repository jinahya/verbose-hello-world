package com.github.jinahya.hello;

import com.github.jinahya.hello.api.util.JavaNioByteBufferUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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
}
