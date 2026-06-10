package com.github.jinahya.hello.miscellaneous;

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

import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A class for exploring "Producer Extends, Consumer Super" (PECS) wildcards from Effective Java
 * Item 31.
 */
@DisplayName("PECS")
@Slf4j
class _Effective_Java_PECS_Test {

    /**
     * Curriculum for Effective Java Item 31 — "Use bounded wildcards to increase API flexibility."
     * <p>
     * The {@code Zoo} class hosts paired methods: each {@code 1} variant uses an invariant type
     * (the suboptimal API) and each {@code 2} variant applies PECS (the API readers should actually
     * write). The contrast at the call site is the lesson.
     *
     * <h3>The PECS rule itself</h3>
     * <ol>
     *   <li>{@code roundUpMammal1} / {@code roundUpMammal2} —
     *       {@code Collection<? super T>}: <b>Consumer Super</b>, concrete {@code T = Mammal}.</li>
     *   <li>{@code roundUp1} / {@code roundUp2} —
     *       {@code Collection<? super T>}: <b>Consumer Super</b>, generic {@code T extends Animal}.</li>
     *   <li>{@code inspect1} / {@code inspect2} —
     *       {@code Consumer<? super T>}: Consumer Super on a <b>functional interface</b>.</li>
     *   <li>{@code capture1} / {@code capture2} (wild → zoo) —
     *       {@code Collection<? extends Animal>}: <b>Producer Extends</b>, collection.</li>
     *   <li>{@code breed1} / {@code breed2} (none → zoo) —
     *       {@code Supplier<? extends T>}: Producer Extends on a <b>functional interface</b>.</li>
     *   <li>{@code release1} / {@code release2} (zoo → wild) —
     *       {@code Collection<? super Animal>}: Consumer Super, with the "12 Monkeys" payoff
     *       (the wild is modelled by {@code Nature.wildlife}, a {@code List<Object>}).</li>
     *   <li>{@code tag1} / {@code tag2} —
     *       {@code Function<? super T, ? extends R>}: <b>BOTH PECS sides at once</b>; the climactic
     *       Item-31 example. Same input/output positions in one signature.</li>
     * </ol>
     *
     * <h3>The edges of the rule</h3>
     * <ol start="8">
     *   <li>{@code swap} — <b>Invariance is correct</b> when a parameter is <i>both</i> read and
     *       written. {@code List<T>} can be neither {@code <? extends T>} nor {@code <? super T>}
     *       in {@code list.set(i, list.set(j, list.get(i)))}.</li>
     *   <li>{@code roster1} / {@code roster2} — <b>Don't use wildcards as return types</b>.
     *       {@code Collection<? extends Animal>} forces wildcard plumbing on every caller; the
     *       invariant {@code Collection<Animal>} return is preferable.</li>
     *   <li>{@code heaviest} — <b>Recursive PECS bound</b>:
     *       {@code <T extends Comparable<? super T>>}. {@code Cat} is {@code Comparable<Animal>}
     *       via inheritance, which is-a {@code Comparable<? super Cat>} — but is <i>not</i> a
     *       {@code Comparable<Cat>}. The {@code <? super T>} inside the bound is what makes the
     *       method accept any {@code T extends Animal}.</li>
     * </ol>
     *
     * <h3>Type lattice</h3>
     * <pre>
     *   Animal (implements Comparable&lt;Animal&gt;, abstract averageWeight())
     *   ├── Mammal
     *   │   ├── Cat       (    4.5 kg)
     *   │   └── Whale     (130000.0 kg)
     *   └── Reptile
     *       ├── Tortoise  (    2.0 kg)
     *       └── Snake     (    0.2 kg)
     * </pre>
     * <p>
     * {@code Animal implements Comparable<Animal>} (not {@code Comparable<T>} per subclass) is
     * <i>intentional</i>: it is what makes the recursive {@code <? super T>} bound necessary in
     * {@code heaviest}.
     * <p>
     * Each test method is named {@code <method>__()} and shows positive cases that compile
     * alongside commented-out call sites that would NOT compile, making the wildcard rules
     * compiler-enforced lessons rather than prose.
     */
    @DisplayName("Zoo")
    @Nested
    class Zoo_Test {

        private static class Nature {

            Nature() {
                super();
            }

            final List<Object> wildlife = new ArrayList<>();
        }

        // Animal implements Comparable<Animal> (not Comparable<T> per subclass). This is the
        // INTENTIONAL shape that makes the recursive bound <T extends Comparable<? super T>>
        // necessary in <heaviest>: Cat is Comparable<Animal> via inheritance, which is-a
        // Comparable<? super Cat> — but NOT a Comparable<Cat>.
        @NoArgsConstructor(access = AccessLevel.PACKAGE)
        private abstract static class Animal implements Comparable<Animal> {

//            static final Comparator<Animal> COMPARING_AVERAGE_WEIGHT =
//                    Comparator.comparingDouble(Animal::getAverageWeight);

            abstract double getAverageWeight(); // kg, approximate — for ordering

            @Override
            public final int compareTo(final Animal o) {
//                return COMPARING_AVERAGE_WEIGHT.compare(this, o);
                return Double.compare(getAverageWeight(), o.getAverageWeight());
            }
        }

        // -----------------------------------------------------------------------------------------
        @NoArgsConstructor(access = AccessLevel.PACKAGE)
        private abstract static class Mammal extends Animal {

        }

        @NoArgsConstructor(access = AccessLevel.PACKAGE)
        private abstract static class Reptile extends Animal {

        }

        // -----------------------------------------------------------------------------------------
        private static class Cat extends Mammal {

            @Override
            double getAverageWeight() {
                return 4.5d;
            }
        }

        private static class Whale extends Mammal {

            @Override
            double getAverageWeight() {
                return 130_000d;
            }
        }

        // -----------------------------------------------------------------------------------------
        private static class Tortoise extends Reptile {

            @Override
            double getAverageWeight() {
                return 2.0d;
            }
        }

        private static class Snake extends Reptile {

            @Override
            double getAverageWeight() {
                return 0.2d;
            }
        }

        // ------------------------------------------------------------------------------------------
        class Zoo {

            Zoo() {
                animals = new ArrayList<>();
                animals.add(new Cat());      // mammal
                animals.add(new Whale());    // mammal
                animals.add(new Tortoise()); // reptile
                animals.add(new Snake());    // reptile
            }

            // Invariant collection: the caller must pass exactly Collection<Mammal>.
            // Collection<Animal> or Collection<Object> won't compile at the call site.
            //
            // Inside this method, the parameter <collection> can be passed to
            // <roundUpMammal2(Collection<? super Mammal>)> — Collection<Mammal> is-a
            // Collection<? super Mammal>, so #1 can call #2.
            void roundUpMammal1(final Collection<Mammal> collection) {
                animals.stream()
                        .filter(Mammal.class::isInstance)
                        .map(Mammal.class::cast)
                        .forEach(collection::add);
            }

            // PECS "Consumer Super": the caller can pass Collection<Mammal>,
            // Collection<Animal>, or Collection<Object>.
            //
            // Inside this method, the parameter <collection> CANNOT be passed to
            // <roundUpMammal1(Collection<Mammal>)> — the wildcard <? super Mammal> may
            // bind to a supertype (e.g., Animal), and Collection<Animal> is not a
            // Collection<Mammal>. So #2 cannot call #1.
            void roundUpMammal2(final Collection<? super Mammal> collection) {
                animals.stream()
                        .filter(Mammal.class::isInstance)
                        .map(Mammal.class::cast)
                        .forEach(collection::add);
            }

            // Generic version of roundUpMammal1: parameterized by any Animal subtype <T>.
            // The collection is invariant — the caller must pass exactly Collection<T>.
            <T extends Animal> void roundUp1(final Class<T> clazz,
                                             final Collection<T> collection) {
                animals.stream()
                        .filter(clazz::isInstance)
                        .map(clazz::cast)
                        .forEach(collection::add);
            }

            // Generic version of roundUpMammal2: parameterized by any Animal subtype <T>.
            // PECS "Consumer Super" — the caller can pass any Collection whose element type is
            // <T> or a supertype of <T>.
            <T extends Animal> void roundUp2(final Class<T> clazz,
                                             final Collection<? super T> collection) {
                animals.stream()
                        .filter(clazz::isInstance)
                        .map(clazz::cast)
                        .forEach(collection::add);
            }

            // -------------------------------------------------------------------------------------
            <T extends Animal> void inspect1(final Class<T> clazz, final Consumer<T> consumer) {
                animals.stream()
                        .filter(clazz::isInstance)
                        .map(clazz::cast)
                        .forEach(consumer);
            }

            <T extends Animal> void inspect2(final Class<T> clazz,
                                             final Consumer<? super T> consumer) {
                animals.stream()
                        .filter(clazz::isInstance)
                        .map(clazz::cast)
                        .forEach(consumer);
            }

            // ------------------------------------------------------------------- capture (wild → zoo)

            // Invariant: the caller MUST pass exactly Collection<Animal>.
            // Collection<Cat> or Collection<Mammal> would NOT compile here even though their
            // elements are perfectly valid Animals.
            void capture1(final Collection<Animal> source) {
                animals.addAll(source);
            }

            // PECS "Producer Extends": the caller can pass Collection<Animal>,
            // Collection<Mammal>, Collection<Reptile>, Collection<Cat>, Collection<Whale>,
            // Collection<Tortoise>, or Collection<Snake>.
            void capture2(final Collection<? extends Animal> source) {
                animals.addAll(source);
            }

            // -------------------------------------------------------------- breed (none → zoo)

            // Invariant: the caller MUST pass exactly Supplier<T>.
            // Drains the supplier until it returns null.
            <T extends Animal> void breed1(final Supplier<T> source) {
                Stream.generate(source)
                        .takeWhile(Objects::nonNull)
                        .forEach(animals::add);
            }

            // PECS "Producer Extends": the caller can pass Supplier<T> or any
            // Supplier<SubtypeOfT> — wider acceptance for the same T binding.
            // Drains the supplier until it returns null.
            <T extends Animal> void breed2(final Supplier<? extends T> source) {
                Stream.generate(source)
                        .takeWhile(Objects::nonNull)
                        .forEach(animals::add);
            }

            // ----------------------------------------------------------- release (zoo → wild)

            // 12 Monkeys, invariant: the caller MUST pass exactly Collection<Animal>.
            // Nature's <List<Object> wildlife> would NOT compile here even though Object :> Animal.
            void release1(final Collection<Animal> destination) {
                destination.addAll(animals);
                animals.clear();
            }

            // 12 Monkeys, PECS "Consumer Super": the caller can pass any Collection whose element
            // type is Animal or a supertype — including Nature's <List<Object> wildlife>.
            void release2(final Collection<? super Animal> destination) {
                destination.addAll(animals);
                animals.clear();
            }

            // ---------------------------------------------------- map (transform: BOTH PECS sides)

            // Both sides invariant: caller MUST pass exactly Function<T, R>.
            <T extends Animal, R> List<R> tag1(final Class<T> clazz,
                                               final Function<T, R> mapper) {
                return animals.stream()
                        .filter(clazz::isInstance)
                        .map(clazz::cast)
                        .map(mapper)
                        .toList();
            }

            // Full PECS — input "Consumer Super", output "Producer Extends":
            // caller can pass Function<SuperOfT, SubtypeOfR>.
            <T extends Animal, R> List<R> tag2(final Class<T> clazz,
                                               final Function<? super T, ? extends R> mapper) {
                return animals.stream()
                        .filter(clazz::isInstance)
                        .map(clazz::cast)
                        .<R>map(mapper)
                        .toList();
            }

            // ----------------------------------------------------- Gap 1: invariance (read AND write)
            //
            // <list> is BOTH read (get/set return) and written (set's value). Neither
            // <? extends T> nor <? super T> works:
            //   <? extends T>  ❌ list.set(j, ...) won't accept a T (capture mismatch)
            //   <? super T>    ❌ list.get(i) returns a wildcard, not a T
            // Invariance is the ONLY correct API here. PECS is not always "use a wildcard."
            static <T extends Animal> void swap(final List<T> list, final int i,
                                                final int j) {
                list.set(i, list.set(j, list.get(i)));
            }

            // -------------------------------------------- Gap 2: don't use wildcards as return types
            //
            // GOOD — invariant return: caller gets a Collection<Animal> they can use anywhere
            // a Collection<Animal> is expected.
            Collection<Animal> roster1() {
                return List.copyOf(animals);
            }

            // BAD (Item 31 anti-pattern) — wildcard return: callers are forced into wildcard
            // plumbing. They can't pass the result to a method expecting Collection<Animal>,
            // and they can't add to it.
            Collection<? extends Animal> roster2() {
                return List.copyOf(animals);
            }

            // ------------------------------------------------ Gap 3: recursive bound `<? super T>`
            //
            // The recursive bound <T extends Comparable<? super T>> is what lets `heaviest` accept
            // any T whose comparison is defined at T or any of T's supertypes. Cat is
            // Comparable<Animal> via inheritance, which is-a Comparable<? super Cat>.
            static <T extends Comparable<? super T>> T heaviest(
                    final Collection<? extends T> coll) {
                final var it = coll.iterator();
                T result = it.next();
                while (it.hasNext()) {
                    final T t = it.next();
                    if (t.compareTo(result) > 0) {
                        result = t;
                    }
                }
                return result;
            }

            private final Collection<Animal> animals;
        }

        // roundUpMammal1 is invariant: the caller MUST pass exactly Collection<Mammal>.

        /**
         * Verifies that {@code roundUpMammal1} accepts only an invariant
         * {@code Collection<Mammal>}.
         */
        @DisplayName("should accept only an invariant <Collection<Mammal>> in <roundUpMammal1>")
        @Test
        void roundUpMammal1__() {
            {
                final var zoo = new Zoo();
                final Collection<Mammal> list = new ArrayList<>();
                zoo.roundUpMammal1(list);
                assertEquals(2, list.size()); // Cat + Whale
            }

            // these would NOT compile — the parameter is invariant:
            {
                final var zoo = new Zoo();
                final Collection<Animal> list = new ArrayList<>();
//                zoo.roundUpMammal1(list); // ❌ won't compile
            }
            {
                final var zoo = new Zoo();
                final Collection<Object> list = new ArrayList<>();
//                zoo.roundUpMammal1(list);  // ❌ won't compile
            }
        }

        // roundUpMammal2 follows PECS "Consumer Super":
        // the caller can pass any Collection whose element type is Mammal or a supertype.

        /**
         * Verifies that {@code roundUpMammal2} accepts any {@code Collection<? super Mammal>} via
         * PECS.
         */
        @DisplayName("should accept any <Collection<? super Mammal>> via PECS in <roundUpMammal2>")
        @Test
        void roundUpMammal2__() {
            {
                final var zoo = new Zoo();
                final Collection<Mammal> list = new ArrayList<>();
                zoo.roundUpMammal2(list);
                assertEquals(2, list.size());
            }
            {
                final var zoo = new Zoo();
                final Collection<Animal> list = new ArrayList<>();
                zoo.roundUpMammal2(list); // ✅ compiles (Animal :> Mammal)
                assertEquals(2, list.size());
            }
            {
                final var zoo = new Zoo();
                final Collection<Object> list = new ArrayList<>();
                zoo.roundUpMammal2(list); // ✅ compiles (Object :> Mammal)
                assertEquals(2, list.size());
            }
        }

        // roundUp1<T> is invariant: the caller MUST pass exactly Collection<T>.

        /**
         * Verifies that {@code roundUp1} accepts only an invariant {@code Collection<T>}.
         */
        @DisplayName("should accept only an invariant <Collection<T>> in <roundUp1>")
        @Test
        void roundUp1__() {
            {
                final var zoo = new Zoo();
                final Collection<Mammal> list = new ArrayList<>();
                zoo.roundUp1(Mammal.class, list);
                assertEquals(2, list.size()); // Cat + Whale
            }
            {
                final var zoo = new Zoo();
                final Collection<Cat> list = new ArrayList<>();
                zoo.roundUp1(Cat.class, list);
                assertEquals(1, list.size()); // Cat
            }
            {
                final var zoo = new Zoo();
                final Collection<Reptile> list = new ArrayList<>();
                zoo.roundUp1(Reptile.class, list);
                assertEquals(2, list.size()); // Tortoise + Snake
            }
            {
                // these would NOT compile — Collection<T> is invariant:
                final var zoo = new Zoo();
                final Collection<Animal> list = new ArrayList<>();
//                zoo.roundUp1(Mammal.class, list); // ❌ T inferred as Mammal; Animal is not Mammal
            }
        }

        // roundUp2<T> follows PECS "Consumer Super":
        // the caller can pass any Collection whose element type is <T> or a supertype of <T>.

        /**
         * Verifies that {@code roundUp2} accepts any {@code Collection<? super T>} via PECS.
         */
        @DisplayName("should accept any <Collection<? super T>> via PECS in <roundUp2>")
        @Test
        void roundUp2__() {
            // T = Mammal
            {
                final var zoo = new Zoo();
                final Collection<Mammal> list = new ArrayList<>();
                zoo.roundUp2(Mammal.class, list);
                assertEquals(2, list.size());
            }
            {
                final var zoo = new Zoo();
                final Collection<Animal> list = new ArrayList<>();
                zoo.roundUp2(Mammal.class, list); // ✅ Animal :> Mammal
                assertEquals(2, list.size());
            }
            {
                final var zoo = new Zoo();
                final Collection<Object> list = new ArrayList<>();
                zoo.roundUp2(Mammal.class, list); // ✅ Object :> Mammal
                assertEquals(2, list.size());
            }
            // T = Cat — the supertype set widens further: Mammal, Animal, Object all OK
            {
                final var zoo = new Zoo();
                final Collection<Mammal> list = new ArrayList<>();
                zoo.roundUp2(Cat.class, list);       // ✅ Mammal :> Cat
                assertEquals(1, list.size());
            }
            {
                final var zoo = new Zoo();
                final Collection<Animal> list = new ArrayList<>();
                zoo.roundUp2(Cat.class, list);       // ✅ Animal :> Cat
                assertEquals(1, list.size());
            }
        }

        // inspect1<T> is invariant: the caller MUST pass exactly Consumer<T>.

        /**
         * Verifies that {@code inspect1} accepts only an invariant {@code Consumer<T>}.
         */
        @DisplayName("should accept only an invariant <Consumer<T>> in <inspect1>")
        @Test
        void inspect1__() {
            {
                final var zoo = new Zoo();
                final var counter = new AtomicInteger();
                final Consumer<Mammal> consumer = s -> counter.incrementAndGet();
                zoo.inspect1(Mammal.class, consumer);
                assertEquals(2, counter.get()); // Cat + Whale
            }
            {
                final var zoo = new Zoo();
                final var counter = new AtomicInteger();
                final Consumer<Animal> consumer = a -> counter.incrementAndGet();
//                zoo.inspect1(Mammal.class, consumer); // ❌ T inferred as Mammal; Animal ≠ Mammal
            }
            {
                final var zoo = new Zoo();
                final var counter = new AtomicInteger();
                final Consumer<Object> consumer = o -> counter.incrementAndGet();
//                zoo.inspect1(Mammal.class, consumer); // ❌ same reason
            }
        }

        // inspect2<T> follows PECS "Consumer Super":
        // the caller can pass any Consumer whose element type is <T> or a supertype of <T>.

        /**
         * Verifies that {@code inspect2} accepts any {@code Consumer<? super T>} via PECS.
         */
        @DisplayName("should accept any <Consumer<? super T>> via PECS in <inspect2>")
        @Test
        void inspect2__() {
            // T = Mammal
            {
                final var zoo = new Zoo();
                final var counter = new AtomicInteger();
                final Consumer<Mammal> consumer = s -> counter.incrementAndGet();
                zoo.inspect2(Mammal.class, consumer);
                assertEquals(2, counter.get());
            }
            {
                final var zoo = new Zoo();
                final var counter = new AtomicInteger();
                final Consumer<Animal> consumer = a -> counter.incrementAndGet();
                zoo.inspect2(Mammal.class, consumer); // ✅ Animal :> Mammal
                assertEquals(2, counter.get());
            }
            {
                final var zoo = new Zoo();
                final var counter = new AtomicInteger();
                final Consumer<Object> consumer = o -> counter.incrementAndGet();
                zoo.inspect2(Mammal.class, consumer); // ✅ Object :> Mammal
                assertEquals(2, counter.get());
            }
            // T = Cat — narrower T means a wider set of acceptable Consumer supertypes
            {
                final var zoo = new Zoo();
                final var counter = new AtomicInteger();
                final Consumer<Mammal> consumer = s -> counter.incrementAndGet();
                zoo.inspect2(Cat.class, consumer);       // ✅ Mammal :> Cat
                assertEquals(1, counter.get());
            }
            {
                final var zoo = new Zoo();
                final var counter = new AtomicInteger();
                final Consumer<Animal> consumer = a -> counter.incrementAndGet();
                zoo.inspect2(Cat.class, consumer);       // ✅ Animal :> Cat
                assertEquals(1, counter.get());
            }
        }

        // ========================================================== capture (wild → zoo) tests

        // capture1 is invariant: the caller MUST pass exactly Collection<Animal>.

        /**
         * Verifies that {@code capture1} accepts only an invariant {@code Collection<Animal>}.
         */
        @DisplayName("should accept only an invariant <Collection<Animal>> in <capture1>")
        @Test
        void capture1__() {
            {
                final var zoo = new Zoo();
                final Collection<Animal> source = new ArrayList<>();
                source.add(new Cat());
                source.add(new Snake());
                zoo.capture1(source);
            }

            // these would NOT compile — Collection<Animal> is invariant:
            {
                final var zoo = new Zoo();
                final Collection<Cat> source = new ArrayList<>();
//                zoo.capture1(source); // ❌ Collection<Cat> is not Collection<Animal>
            }
            {
                final var zoo = new Zoo();
                final Collection<Mammal> source = new ArrayList<>();
//                zoo.capture1(source); // ❌ won't compile
            }
            {
                final var zoo = new Zoo();
                final Collection<Reptile> source = new ArrayList<>();
//                zoo.capture1(source); // ❌ won't compile
            }
        }

        // capture2 follows PECS "Producer Extends":
        // the caller can pass Collection<Animal> or any Collection<SubtypeOfAnimal>.

        /**
         * Verifies that {@code capture2} accepts any {@code Collection<? extends Animal>} via
         * PECS.
         */
        @DisplayName("should accept any <Collection<? extends Animal>> via PECS in <capture2>")
        @Test
        void capture2__() {
            {
                final var zoo = new Zoo();
                final Collection<Animal> source = new ArrayList<>();
                source.add(new Cat());
                source.add(new Snake());
                zoo.capture2(source);
            }
            {
                final var zoo = new Zoo();
                final Collection<Mammal> source = new ArrayList<>();
                source.add(new Cat());
                source.add(new Whale());
                zoo.capture2(source); // ✅ Mammal <: Animal
            }
            {
                final var zoo = new Zoo();
                final Collection<Reptile> source = new ArrayList<>();
                source.add(new Tortoise());
                source.add(new Snake());
                zoo.capture2(source); // ✅ Reptile <: Animal
            }
            {
                final var zoo = new Zoo();
                final Collection<Cat> source = new ArrayList<>();
                source.add(new Cat());
                source.add(new Cat());
                zoo.capture2(source); // ✅ Cat <: Animal
            }
        }

        // ============================================================== breed (none → zoo) tests

        // breed1<T> is invariant: the caller MUST pass exactly Supplier<T> for the inferred T.

        /**
         * Verifies that {@code breed1} accepts only an invariant {@code Supplier<T>}.
         */
        @DisplayName("should accept only an invariant <Supplier<T>> in <breed1>")
        @Test
        void breed1__() {
            {
                // T inferred as Cat — supplier yields 3 Cats then null
                final var zoo = new Zoo();
                final var remaining = new AtomicInteger(3);
                final Supplier<Cat> source = () ->
                        remaining.get() > 0 && remaining.decrementAndGet() >= 0 ? new Cat() : null;
                zoo.breed1(source);
                assertEquals(0, remaining.get()); // drained to 0
            }
            {
                // T explicitly Mammal — supplier must be Supplier<Mammal> exactly
                final var zoo = new Zoo();
                final var remaining = new AtomicInteger(3);
                final Supplier<Mammal> source = () ->
                        remaining.get() > 0 && remaining.decrementAndGet() >= 0 ? new Cat() : null;
                zoo.breed1(source);
                assertEquals(0, remaining.get());
            }

            // this would NOT compile — Supplier<T> is invariant, can't pass narrower-typed supplier:
            {
                final var zoo = new Zoo();
                final Supplier<Cat> source = Cat::new;
//                zoo.<Mammal>breed1(source); // ❌ Supplier<Cat> is not Supplier<Mammal>
            }
        }

        // breed2<T> follows PECS "Producer Extends":
        // the caller can pass Supplier<T> or any Supplier<SubtypeOfT>.

        /**
         * Verifies that {@code breed2} accepts any {@code Supplier<? extends T>} via PECS.
         */
        @DisplayName("should accept any <Supplier<? extends T>> via PECS in <breed2>")
        @Test
        void breed2__() {
            {
                // T = Cat (inferred)
                final var zoo = new Zoo();
                final var remaining = new AtomicInteger(3);
                final Supplier<Cat> source = () ->
                        remaining.get() > 0 && remaining.decrementAndGet() >= 0 ? new Cat() : null;
                zoo.breed2(source);
                assertEquals(0, remaining.get());
            }
            {
                // T = Mammal explicitly — Supplier<Cat> is accepted because Cat <: Mammal
                final var zoo = new Zoo();
                final var remaining = new AtomicInteger(3);
                final Supplier<Cat> source = () ->
                        remaining.get() > 0 && remaining.decrementAndGet() >= 0 ? new Cat() : null;
                zoo.<Mammal>breed2(source); // ✅ Supplier<Cat> :< Supplier<? extends Mammal>
                assertEquals(0, remaining.get());
            }
            {
                // T = Animal explicitly — Supplier<Cat> is still accepted
                final var zoo = new Zoo();
                final var remaining = new AtomicInteger(3);
                final Supplier<Cat> source = () ->
                        remaining.get() > 0 && remaining.decrementAndGet() >= 0 ? new Cat() : null;
                zoo.<Animal>breed2(source); // ✅ Supplier<Cat> :< Supplier<? extends Animal>
                assertEquals(0, remaining.get());
            }
        }

        // =========================================================== release (zoo → wild) tests

        // release1 is invariant: the caller MUST pass exactly Collection<Animal>.

        /**
         * Verifies that {@code release1} accepts only an invariant {@code Collection<Animal>}.
         */
        @DisplayName("should accept only an invariant <Collection<Animal>> in <release1>")
        @Test
        void release1__() {
            {
                final var zoo = new Zoo();
                final Collection<Animal> destination = new ArrayList<>();
                zoo.release1(destination);
                assertEquals(4, destination.size()); // Cat + Whale + Tortoise + Snake
            }

            // 12 Monkeys: liberating zoo into Nature's <List<Object> wildlife> would NOT compile:
            {
                final var zoo = new Zoo();
                final var nature = new Nature();
//                zoo.release1(nature.wildlife); // ❌ List<Object> is not Collection<Animal>
            }
        }

        // release2 follows PECS "Consumer Super":
        // the caller can pass Collection<Animal>, Collection<Object>, or anything in between.

        /**
         * Verifies that {@code release2} accepts any {@code Collection<? super Animal>} via PECS.
         */
        @DisplayName("should accept any <Collection<? super Animal>> via PECS in <release2>")
        @Test
        void release2__() {
            {
                final var zoo = new Zoo();
                final Collection<Animal> destination = new ArrayList<>();
                zoo.release2(destination);
                assertEquals(4, destination.size());
            }
            {
                // 12 Monkeys: liberate the zoo into Nature.
                final var zoo = new Zoo();
                final var nature = new Nature();
                zoo.release2(nature.wildlife); // ✅ List<Object> is Collection<? super Animal>
                assertEquals(4, nature.wildlife.size());
            }
        }

        // ========================================================== map (Function: BOTH PECS sides)

        // tag1: both sides invariant — caller MUST pass exactly Function<T, R>.

        /**
         * Verifies that {@code tag1} accepts only an invariant {@code Function<T, R>}.
         */
        @DisplayName("should accept only an invariant <Function<T, R>> in <tag1>")
        @Test
        void tag1__() {
            // T = Cat, R = Animal
            {
                final var zoo = new Zoo();
                final Function<Cat, Animal> mapper = c -> c;
                final List<Animal> result = zoo.tag1(Cat.class, mapper);
                assertEquals(1, result.size()); // one Cat in zoo
            }

            // these would NOT compile — Function<T, R> is invariant on BOTH sides:
            {
                // input mismatch: Function<Animal, Animal> is not Function<Cat, Animal>
                final var zoo = new Zoo();
                final Function<Animal, Animal> mapper = a -> a;
//                zoo.tag1(Cat.class, mapper); // ❌ won't compile
            }
            {
                // output mismatch: Function<Cat, Cat> is not Function<Cat, Animal>
                final var zoo = new Zoo();
                final Function<Cat, Cat> mapper = c -> c;
//                final List<Animal> result = zoo.tag1(Cat.class, mapper); // ❌ won't compile
            }
        }

        // tag2: full PECS — input "? super T", output "? extends R".
        // Caller can pass Function<SuperOfT, SubtypeOfR>.

        /**
         * Verifies that {@code tag2} accepts any {@code Function<? super T, ? extends R>} via full
         * PECS.
         */
        @DisplayName("should accept any <Function<? super T, ? extends R>> via full PECS in <tag2>")
        @Test
        void tag2__() {
            // T = Cat, R = Animal
            {
                final var zoo = new Zoo();
                final Function<Cat, Animal> mapper = c -> c;
                final List<Animal> result = zoo.tag2(Cat.class, mapper);
                assertEquals(1, result.size());
            }
            {
                // input "super" accepts Function<Animal, Animal> (Animal :> Cat)
                final var zoo = new Zoo();
                final Function<Animal, Animal> mapper = a -> a;
                final List<Animal> result = zoo.tag2(Cat.class, mapper);
                assertEquals(1, result.size());
            }
            {
                // output "extends" accepts Function<Cat, Cat> (Cat <: Animal)
                final var zoo = new Zoo();
                final Function<Cat, Cat> mapper = c -> c;
                final List<Animal> result = zoo.tag2(Cat.class, mapper);
                assertEquals(1, result.size());
            }
            {
                // BOTH sides exercised — Function<Animal, Cat>:
                // input super, output extends. THE PECS payoff: one mapper, multiple binding sites.
                final var zoo = new Zoo();
                final Function<Animal, Cat> mapper = a -> new Cat();
                final List<Animal> result = zoo.tag2(Cat.class, mapper);
                assertEquals(1, result.size());
            }
        }

        // ====================================== Gap 1: invariance when read AND write (swap)

        // Caller must pass <List<T>> for the SAME T as the Cats / Mammals / Animals they're
        // swapping. Neither <? extends T> nor <? super T> would compile inside <swap>.

        /**
         * Verifies that {@code swap} requires an invariant {@code List<T>} when both reading and
         * writing.
         */
        @DisplayName(
                "should require an invariant <List<T>> in <swap> when both reading and writing")
        @Test
        void swap__() {
            // T = Cat
            {
                final List<Cat> cats = new ArrayList<>(List.of(new Cat(), new Cat()));
                final var first = cats.getFirst();
                Zoo.swap(cats, 0, 1);
                assertSame(first, cats.get(1));
            }
            // T = Mammal
            {
                final List<Mammal> mammals = new ArrayList<>(List.of(new Cat(), new Whale()));
                final var first = mammals.getFirst();
                Zoo.swap(mammals, 0, 1);
                assertSame(first, mammals.get(1));
            }
            // T = Animal
            {
                final List<Animal> animals = new ArrayList<>(List.of(new Cat(), new Snake()));
                final var first = animals.getFirst();
                Zoo.swap(animals, 0, 1);
                assertSame(first, animals.get(1));
            }
            // these would NOT have worked inside <swap> if we'd tried PECS — but unlike the
            // caller-side experiments we did for collect/inspect/etc., the failure here
            // is on the IMPLEMENTATION side. Try replacing <List<T>> with <List<? extends T>>
            // or <List<? super T>> in <swap> — the body won't compile.
        }

        // ============================ Gap 2: don't use wildcards as return types (roster)

        // roster1 returns invariant Collection<Animal> — easy to use.
        // roster2 returns Collection<? extends Animal> — caller is stuck with wildcards.

        /**
         * Verifies that an invariant return type is preferable over a wildcard return type.
         */
        @DisplayName("should prefer an invariant return type over a wildcard return in <roster>")
        @Test
        void roster__() {
            final var zoo = new Zoo();

            // roster1: free flow into any Collection<Animal> sink
            {
                final Collection<Animal> snap = zoo.roster1();
                final List<Animal> sink = new ArrayList<>();
                sink.addAll(snap);                      // ✅ compiles
                assertEquals(4, sink.size());
            }

            // roster2: caller has Collection<? extends Animal>; can read but can't pass to
            // any API that expects an invariant Collection<Animal>:
            {
                final Collection<? extends Animal> snap = zoo.roster2();
                // this would NOT compile — wildcard return forces the caller into wildcard plumbing:
                //     final List<Animal> sink = new ArrayList<>();
                //     sink.addAll(snap); // works (addAll accepts Collection<? extends E>)
                //     final Collection<Animal> reusable = snap; // ❌ wildcard not assignable to invariant
                assertEquals(4, snap.size()); // still fine to read
            }
        }

        // ============================== Gap 3: recursive bound <T extends Comparable<? super T>>

        // <heaviest> demands T's elements know how to compare themselves to a T (or supertype of T).
        // Cats are NOT Comparable<Cat>; they ARE Comparable<Animal> (via Animal's
        // implements Comparable<Animal>). The <? super T> in the bound is what lets <Cat> bind:
        // <Cat extends Comparable<? super Cat>> ✅ because Comparable<Animal> satisfies it.

        /**
         * Verifies that {@code heaviest} binds any {@code T} via the recursive
         * {@code <T extends Comparable<? super T>>} bound.
         */
        @DisplayName("""
                should bind any <T>
                via the recursive <T extends Comparable<? super T>> bound in <heaviest>""")
        @Test
        void heaviest__() {
            // T = Cat
            {
                final List<Cat> cats = List.of(new Cat(), new Cat());
                final Cat heaviest = Zoo.heaviest(cats);
                assertNotNull(heaviest);
            }
            // T = Mammal — Cat (4.5kg) vs Whale (130_000kg) ⇒ Whale
            {
                final List<Mammal> mammals = List.of(new Cat(), new Whale());
                final Mammal heaviest = Zoo.heaviest(mammals);
                assertInstanceOf(Whale.class, heaviest);
            }
            // T = Reptile — Tortoise (2.0kg) vs Snake (0.2kg) ⇒ Tortoise
            {
                final List<Reptile> reptiles = List.of(new Tortoise(), new Snake());
                final Reptile heaviest = Zoo.heaviest(reptiles);
                assertInstanceOf(Tortoise.class, heaviest);
            }
            // T = Animal — Whale wins overall
            {
                final List<Animal> all = List.of(new Cat(), new Tortoise(), new Whale(),
                                                 new Snake());
                final Animal heaviest = Zoo.heaviest(all);
                assertInstanceOf(Whale.class, heaviest);
            }
            // Producer Extends side: passing Collection<Cat> when T = Mammal is also fine,
            // because of <Collection<? extends T>>:
            {
                final List<Cat> cats = List.of(new Cat(), new Cat());
                final Mammal heaviest = Zoo.<Mammal>heaviest(cats); // ✅ Cat <: Mammal
                assertInstanceOf(Cat.class, heaviest);
            }
        }
    }
}
