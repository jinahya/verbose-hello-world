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

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Mockito_Test {

    interface Person {

        @PastOrPresent
        @NotNull
        LocalDate getBirthDate();
    }

    static class PersonService {

        @PositiveOrZero
        int calculateAgeAt(final @NotNull Person person, final @NotNull LocalDate date) {
            return Period.between(person.getBirthDate(), date).getYears();
        }

        @PositiveOrZero
        int calculateAge(final @NotNull Person person) {
            return calculateAgeAt(person, LocalDate.now());
        }
    }

    @Nested
    class HowOldIs_Test {

        @Test
        void __() {
            {
                final var now = LocalDate.now();
                final var baby = new Person() {
                    @Override
                    public LocalDate getBirthDate() {
                        return now.minusDays(
                                ThreadLocalRandom.current().nextLong(
                                        ChronoUnit.DAYS.between(
                                                now.minusYears(4),
                                                now
                                        )
                                )
                        );
                    }
                };
                assert Period.between(baby.getBirthDate(), now).getYears() < 4;
                final var age = new PersonService().calculateAgeAt(baby, now);
                Assertions.assertTrue(age < 4);
            }
            // today
            {
                // given
                final Person person = Mockito.mock();
                Mockito.when(person.getBirthDate()).thenReturn(LocalDate.now());
                // when
                final var age = new PersonService().calculateAge(person);
                // then
                Assertions.assertEquals(0, age);
            }
        }
    }

    @DisplayName("오늘 태어난 아기는 영 살이다")
    @Test
    void _AgeZero_BornToday() {
        // given
        final Person person = Mockito.mock();
        final var now = LocalDate.now();
        Mockito.when(person.getBirthDate()).thenReturn(now);
        // when
        final var age = new PersonService().calculateAgeAt(person, now);
        // then
        Assertions.assertEquals(0, age);
    }

    @DisplayName("1년 전 태어난 아기는 한 살이다")
    @Test
    void _AgeOne_YearPrior() {
        // given
        final Person person = Mockito.mock();
        final var now = LocalDate.now();
        Mockito.when(person.getBirthDate()).thenReturn(now.minusYears(1L));
        // when
        final var age = new PersonService().calculateAgeAt(person, now);
        // then
        Assertions.assertEquals(1, age);
    }

    @DisplayName("calculateAge(person) should invoke calculateAgeAt(person, )")
    @Test
    void calculateAge_InvokeCalculateAgeAtWithPerson_() {
        // given
        final Person person = Mockito.mock();
        Mockito.when(person.getBirthDate()).thenReturn(LocalDate.now());
        final PersonService service = Mockito.spy();
        final var expected = ThreadLocalRandom.current().nextInt(128);
        Mockito.doReturn(expected)
                .when(service)
                .calculateAgeAt(ArgumentMatchers.any(), ArgumentMatchers.any());
        // when
        final var actual = service.calculateAge(person);
        // then
        Mockito.verify(service, Mockito.times(1))
                .calculateAgeAt(ArgumentMatchers.same(person), ArgumentMatchers.notNull());
        Assertions.assertEquals(expected, actual);
    }

    @DisplayName("calculateAge(person) should return calculateAgeAt(person, now())")
    @Test
    void calculateAge_InvokeCalculateAgeAtWithPersonAndNow_MayFail() {
        // given
        final Person person = Mockito.mock();
        Mockito.when(person.getBirthDate()).thenReturn(LocalDate.now());
        final PersonService service = Mockito.spy();
        final var expected = ThreadLocalRandom.current().nextInt(128);
        Mockito.doReturn(expected).when(service)
                .calculateAgeAt(ArgumentMatchers.any(), ArgumentMatchers.any());
        // when
        final var age = service.calculateAge(person);
        // then
        final var dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        Mockito.verify(service, Mockito.times(1))
                .calculateAgeAt(ArgumentMatchers.same(person), dateCaptor.capture());
        Assertions.assertEquals(LocalDate.now(), dateCaptor.getValue()); // 🤔
        Assertions.assertEquals(expected, age);
    }

    @DisplayName("calculateAge(person) should return calculateAgeAt(person, now())")
    @Test
    void calculateAge_InvokeCalculateAgeAtWithPersonAndNow_() {
        final var now = LocalDate.now();
        try (var mockedStatic = Mockito.mockStatic(LocalDate.class)) {
            mockedStatic.when(LocalDate::now).thenReturn(now);
            // given
            final Person person = Mockito.mock();
            Mockito.when(person.getBirthDate()).thenReturn(now);
            final PersonService service = Mockito.spy();
            final var expected = ThreadLocalRandom.current().nextInt(128);
            Mockito.doReturn(expected).when(service)
                    .calculateAgeAt(ArgumentMatchers.any(), ArgumentMatchers.any());
            // when
            final var age = service.calculateAge(person);
            // then
            final var dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
            Mockito.verify(service, Mockito.times(1))
                    .calculateAgeAt(ArgumentMatchers.same(person), dateCaptor.capture());
            Assertions.assertEquals(now, dateCaptor.getValue());
            Assertions.assertEquals(expected, age);
        }
    }
}
