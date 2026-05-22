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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class BDDMockito_Test {

    @DisplayName("오늘 태어난 아기는 0살이다")
    @Test
    void _AgeZero_BornToday() {
        // given
        final Mockito_Test.Person person = Mockito.mock();
        final var now = LocalDate.now();
        BDDMockito.given(person.getBirthDate()).willReturn(now);           // <1>
        // when
        final var age = new Mockito_Test.PersonService().calculateAgeAt(person, now);
        // then
        Assertions.assertEquals(0, age);
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
