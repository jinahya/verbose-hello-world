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

import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

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
     *   <li>{@code roundUpSynapsida1} / {@code roundUpSynapsida2} —
     *       {@code Collection<? super T>}: <b>Consumer Super</b>, concrete {@code T = Synapsida}.</li>
     *   <li>{@code roundUp1} / {@code roundUp2} —
     *       {@code Collection<? super T>}: <b>Consumer Super</b>, generic {@code T extends Amniota}.</li>
     *   <li>{@code inspect1} / {@code inspect2} —
     *       {@code Consumer<? super T>}: Consumer Super on a <b>functional interface</b>.</li>
     *   <li>{@code capture1} / {@code capture2} (wild → zoo) —
     *       {@code Collection<? extends Amniota>}: <b>Producer Extends</b>, collection.</li>
     *   <li>{@code breed1} / {@code breed2} (none → zoo) —
     *       {@code Supplier<? extends T>}: Producer Extends on a <b>functional interface</b>.</li>
     *   <li>{@code release1} / {@code release2} (zoo → wild) —
     *       {@code Collection<? super Amniota>}: Consumer Super, with the "12 Monkeys" payoff
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
     *       {@code Collection<? extends Amniota>} forces wildcard plumbing on every caller; the
     *       invariant {@code Collection<Amniota>} return is preferable.</li>
     *   <li>{@code heaviest} — <b>Recursive PECS bound</b>:
     *       {@code <T extends Comparable<? super T>>}. {@code Cat} is {@code Comparable<Amniota>}
     *       via inheritance, which is-a {@code Comparable<? super Cat>} — but is <i>not</i> a
     *       {@code Comparable<Cat>}. The {@code <? super T>} inside the bound is what makes the
     *       method accept any {@code T extends Amniota}.</li>
     * </ol>
     *
     * <h3>Type lattice</h3>
     * <pre>
     *   Amniota (implements Comparable&lt;Amniota&gt;, abstract averageWeight())
     *   ├── Synapsida
     *   │   ├── Cat       (4.5  kg)
     *   │   └── Whale     (5000 kg)
     *   └── Sauropsida
     *       ├── Tortoise  (2    kg)
     *       └── Eagle     (5    kg)
     * </pre>
     * <p>
     * {@code Amniota implements Comparable<Amniota>} (not {@code Comparable<T>} per species) is
     * <i>intentional</i>: it is what makes the recursive {@code <? super T>} bound necessary in
     * {@code heaviest}.
     * <p>
     * Each test method is named {@code <method>__()} and shows positive cases that compile
     * alongside commented-out call sites that would NOT compile, making the wildcard rules
     * compiler-enforced lessons rather than prose.
     */
    @Nested
    class Zoo_Test {

        private static class Nature {

            Nature() {
                super();
            }

            final List<Object> wildlife = new ArrayList<>();
        }

        // Cladistic taxonomy: Synapsida and Sauropsida are the two amniote clades; Mammalia
        // (cats, whales, ...) is the only living group within Synapsida, and Reptilia / Aves
        // (snakes, eagles, ...) live within Sauropsida.
        //
        // Amniota implements Comparable<Amniota> (not Comparable<T> per species). This is the
        // INTENTIONAL shape that makes the recursive bound <T extends Comparable<? super T>>
        // necessary in <heaviest>: Cat is Comparable<Amniota> via inheritance, which is-a
        // Comparable<? super Cat> — but NOT a Comparable<Cat>.
        private abstract static class Amniota implements Comparable<Amniota> {

            abstract double averageWeight(); // kg, approximate — for ordering

            @Override
            public final int compareTo(final Amniota o) {
                return Double.compare(averageWeight(), o.averageWeight());
            }
        }

        private abstract static class Synapsida extends Amniota {

        }

        private abstract static class Sauropsida extends Amniota {

        }

        // -------------------------------------------------------------- synapsida (≈ mammals)
        private static class Cat extends Synapsida {

            @Override
            double averageWeight() {
                return 4.5;
            }
        }

        private static class Whale extends Synapsida {

            @Override
            double averageWeight() {
                return 5_000;
            }
        }

        // -------------------------------------------------------------- sauropsida (reptiles + birds)
        private static class Tortoise extends Sauropsida {

            @Override
            double averageWeight() {
                return 2;
            }
        }

        private static class Eagle extends Sauropsida {

            @Override
            double averageWeight() {
                return 5;
            }
        }

        class Zoo {

            Zoo() {
                amniotes = new ArrayList<>();
                amniotes.add(new Cat());   // synapsida
                amniotes.add(new Whale()); // synapsida
                amniotes.add(new Tortoise()); // sauropsida
                amniotes.add(new Eagle()); // sauropsida
            }

            // Invariant collection: the caller must pass exactly Collection<Synapsida>.
            // Collection<Amniota> or Collection<Object> won't compile at the call site.
            //
            // Inside this method, the parameter <collection> can be passed to
            // <roundUpSynapsida2(Collection<? super Synapsida>)> — Collection<Synapsida> is-a
            // Collection<? super Synapsida>, so #1 can call #2.
            void roundUpSynapsida1(final Collection<Synapsida> collection) {
                amniotes.stream()
                        .filter(Synapsida.class::isInstance)
                        .map(Synapsida.class::cast)
                        .forEach(collection::add);
            }

            // PECS "Consumer Super": the caller can pass Collection<Synapsida>,
            // Collection<Amniota>, or Collection<Object>.
            //
            // Inside this method, the parameter <collection> CANNOT be passed to
            // <roundUpSynapsida1(Collection<Synapsida>)> — the wildcard <? super Synapsida> may
            // bind to a supertype (e.g., Amniota), and Collection<Amniota> is not a
            // Collection<Synapsida>. So #2 cannot call #1.
            void roundUpSynapsida2(final Collection<? super Synapsida> collection) {
                amniotes.stream()
                        .filter(Synapsida.class::isInstance)
                        .map(Synapsida.class::cast)
                        .forEach(collection::add);
            }

            // Generic version of roundUpSynapsida1: parameterized by any Amniota subtype <T>.
            // The collection is invariant — the caller must pass exactly Collection<T>.
            <T extends Amniota> void roundUp1(final Class<T> clazz,
                                              final Collection<T> collection) {
                amniotes.stream()
                        .filter(clazz::isInstance)
                        .map(clazz::cast)
                        .forEach(collection::add);
            }

            // Generic version of roundUpSynapsida2: parameterized by any Amniota subtype <T>.
            // PECS "Consumer Super" — the caller can pass any Collection whose element type is
            // <T> or a supertype of <T>.
            <T extends Amniota> void roundUp2(final Class<T> clazz,
                                              final Collection<? super T> collection) {
                amniotes.stream()
                        .filter(clazz::isInstance)
                        .map(clazz::cast)
                        .forEach(collection::add);
            }

            // Invariant Consumer<T>: the caller must pass exactly Consumer<T>.
            // A Consumer<Amniota> or Consumer<Object> won't compile at the call site even though
            // either could perfectly well accept a T.
            <T extends Amniota> void inspect1(final Class<T> clazz,
                                              final Consumer<T> consumer) {
                amniotes.stream()
                        .filter(clazz::isInstance)
                        .map(clazz::cast)
                        .forEach(consumer);
            }

            // PECS "Consumer Super" — the caller can pass any Consumer whose element type is
            // <T> or a supertype of <T> (e.g., Consumer<T>, Consumer<Amniota>, Consumer<Object>).
            <T extends Amniota> void inspect2(final Class<T> clazz,
                                              final Consumer<? super T> consumer) {
                amniotes.stream()
                        .filter(clazz::isInstance)
                        .map(clazz::cast)
                        .forEach(consumer);
            }

            // ------------------------------------------------------------------- capture (wild → zoo)

            // Invariant: the caller MUST pass exactly Collection<Amniota>.
            // Collection<Cat> or Collection<Synapsida> would NOT compile here even though their
            // elements are perfectly valid Amniotas.
            void capture1(final Collection<Amniota> source) {
                amniotes.addAll(source);
            }

            // PECS "Producer Extends": the caller can pass Collection<Amniota>,
            // Collection<Synapsida>, Collection<Sauropsida>, Collection<Cat>, Collection<Whale>,
            // Collection<Tortoise>, or Collection<Eagle>.
            void capture2(final Collection<? extends Amniota> source) {
                amniotes.addAll(source);
            }

            // -------------------------------------------------------------- breed (none → zoo)

            // Invariant: the caller MUST pass exactly Supplier<T>.
            // Drains the supplier until it returns null.
            <T extends Amniota> void breed1(final Supplier<T> source) {
                Stream.generate(source)
                        .takeWhile(Objects::nonNull)
                        .forEach(amniotes::add);
            }

            // PECS "Producer Extends": the caller can pass Supplier<T> or any
            // Supplier<SubtypeOfT> — wider acceptance for the same T binding.
            // Drains the supplier until it returns null.
            <T extends Amniota> void breed2(final Supplier<? extends T> source) {
                Stream.generate(source)
                        .takeWhile(Objects::nonNull)
                        .forEach(amniotes::add);
            }

            // ----------------------------------------------------------- release (zoo → wild)

            // 12 Monkeys, invariant: the caller MUST pass exactly Collection<Amniota>.
            // Nature's <List<Object> wildlife> would NOT compile here even though Object :> Amniota.
            void release1(final Collection<Amniota> destination) {
                destination.addAll(amniotes);
                amniotes.clear();
            }

            // 12 Monkeys, PECS "Consumer Super": the caller can pass any Collection whose element
            // type is Amniota or a supertype — including Nature's <List<Object> wildlife>.
            void release2(final Collection<? super Amniota> destination) {
                destination.addAll(amniotes);
                amniotes.clear();
            }

            // ---------------------------------------------------- map (transform: BOTH PECS sides)

            // Both sides invariant: caller MUST pass exactly Function<T, R>.
            <T extends Amniota, R> List<R> tag1(final Class<T> clazz,
                                                final Function<T, R> mapper) {
                return amniotes.stream()
                        .filter(clazz::isInstance)
                        .map(clazz::cast)
                        .map(mapper)
                        .toList();
            }

            // Full PECS — input "Consumer Super", output "Producer Extends":
            // caller can pass Function<SuperOfT, SubtypeOfR>.
            <T extends Amniota, R> List<R> tag2(final Class<T> clazz,
                                                final Function<? super T, ? extends R> mapper) {
                return amniotes.stream()
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
            static <T extends Amniota> void swap(final List<T> list, final int i,
                                                 final int j) {
                list.set(i, list.set(j, list.get(i)));
            }

            // -------------------------------------------- Gap 2: don't use wildcards as return types
            //
            // GOOD — invariant return: caller gets a Collection<Amniota> they can use anywhere
            // a Collection<Amniota> is expected.
            Collection<Amniota> roster1() {
                return List.copyOf(amniotes);
            }

            // BAD (Item 31 anti-pattern) — wildcard return: callers are forced into wildcard
            // plumbing. They can't pass the result to a method expecting Collection<Amniota>,
            // and they can't add to it.
            Collection<? extends Amniota> roster2() {
                return List.copyOf(amniotes);
            }

            // ------------------------------------------------ Gap 3: recursive bound `<? super T>`
            //
            // The recursive bound <T extends Comparable<? super T>> is what lets `heaviest` accept
            // any T whose comparison is defined at T or any of T's supertypes. Cat is
            // Comparable<Amniota> via inheritance, which is-a Comparable<? super Cat>.
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

            private final Collection<Amniota> amniotes;
        }

        // roundUpSynapsida1 is invariant: the caller MUST pass exactly Collection<Synapsida>.
        @Test
        void roundUpSynapsida1__() {
            {
                final var zoo = new Zoo();
                final Collection<Synapsida> list = new ArrayList<>();
                zoo.roundUpSynapsida1(list);
                Assertions.assertEquals(2, list.size()); // Cat + Whale
            }

            // these would NOT compile — the parameter is invariant:
            {
                final var zoo = new Zoo();
                final Collection<Amniota> list = new ArrayList<>();
//                zoo.roundUpSynapsida1(list); // ❌ won't compile
            }
            {
                final var zoo = new Zoo();
                final Collection<Object> list = new ArrayList<>();
//                zoo.roundUpSynapsida1(list);  // ❌ won't compile
            }
        }

        // roundUpSynapsida2 follows PECS "Consumer Super":
        // the caller can pass any Collection whose element type is Synapsida or a supertype.
        @Test
        void roundUpSynapsida2__() {
            {
                final var zoo = new Zoo();
                final Collection<Synapsida> list = new ArrayList<>();
                zoo.roundUpSynapsida2(list);
                Assertions.assertEquals(2, list.size());
            }
            {
                final var zoo = new Zoo();
                final Collection<Amniota> list = new ArrayList<>();
                zoo.roundUpSynapsida2(list); // ✅ compiles (Amniota :> Synapsida)
                Assertions.assertEquals(2, list.size());
            }
            {
                final var zoo = new Zoo();
                final Collection<Object> list = new ArrayList<>();
                zoo.roundUpSynapsida2(list); // ✅ compiles (Object :> Synapsida)
                Assertions.assertEquals(2, list.size());
            }
        }

        // roundUp1<T> is invariant: the caller MUST pass exactly Collection<T>.
        @Test
        void roundUp1__() {
            {
                final var zoo = new Zoo();
                final Collection<Synapsida> list = new ArrayList<>();
                zoo.roundUp1(Synapsida.class, list);
                Assertions.assertEquals(2, list.size()); // Cat + Whale
            }
            {
                final var zoo = new Zoo();
                final Collection<Cat> list = new ArrayList<>();
                zoo.roundUp1(Cat.class, list);
                Assertions.assertEquals(1, list.size()); // Cat
            }
            {
                final var zoo = new Zoo();
                final Collection<Sauropsida> list = new ArrayList<>();
                zoo.roundUp1(Sauropsida.class, list);
                Assertions.assertEquals(2, list.size()); // Tortoise + Eagle
            }
            {
                // these would NOT compile — Collection<T> is invariant:
                final var zoo = new Zoo();
                final Collection<Amniota> list = new ArrayList<>();
//                zoo.roundUp1(Synapsida.class, list); // ❌ T inferred as Synapsida; Amniota is not Synapsida
            }
        }

        // roundUp2<T> follows PECS "Consumer Super":
        // the caller can pass any Collection whose element type is <T> or a supertype of <T>.
        @Test
        void roundUp2__() {
            // T = Synapsida
            {
                final var zoo = new Zoo();
                final Collection<Synapsida> list = new ArrayList<>();
                zoo.roundUp2(Synapsida.class, list);
                Assertions.assertEquals(2, list.size());
            }
            {
                final var zoo = new Zoo();
                final Collection<Amniota> list = new ArrayList<>();
                zoo.roundUp2(Synapsida.class, list); // ✅ Amniota :> Synapsida
                Assertions.assertEquals(2, list.size());
            }
            {
                final var zoo = new Zoo();
                final Collection<Object> list = new ArrayList<>();
                zoo.roundUp2(Synapsida.class, list); // ✅ Object :> Synapsida
                Assertions.assertEquals(2, list.size());
            }
            // T = Cat — the supertype set widens further: Synapsida, Amniota, Object all OK
            {
                final var zoo = new Zoo();
                final Collection<Synapsida> list = new ArrayList<>();
                zoo.roundUp2(Cat.class, list);       // ✅ Synapsida :> Cat
                Assertions.assertEquals(1, list.size());
            }
            {
                final var zoo = new Zoo();
                final Collection<Amniota> list = new ArrayList<>();
                zoo.roundUp2(Cat.class, list);       // ✅ Amniota :> Cat
                Assertions.assertEquals(1, list.size());
            }
        }

        // inspect1<T> is invariant: the caller MUST pass exactly Consumer<T>.
        @Test
        void inspect1__() {
            {
                final var zoo = new Zoo();
                final var counter = new AtomicInteger();
                final Consumer<Synapsida> consumer = s -> counter.incrementAndGet();
                zoo.inspect1(Synapsida.class, consumer);
                Assertions.assertEquals(2, counter.get()); // Cat + Whale
            }

            // these would NOT compile — Consumer<T> is invariant:
            {
                final var zoo = new Zoo();
                final var counter = new AtomicInteger();
                final Consumer<Amniota> consumer = a -> counter.incrementAndGet();
//                zoo.inspect1(Synapsida.class, consumer); // ❌ T inferred as Synapsida; Amniota ≠ Synapsida
            }
            {
                final var zoo = new Zoo();
                final var counter = new AtomicInteger();
                final Consumer<Object> consumer = o -> counter.incrementAndGet();
//                zoo.inspect1(Synapsida.class, consumer); // ❌ same reason
            }
        }

        // inspect2<T> follows PECS "Consumer Super":
        // the caller can pass any Consumer whose element type is <T> or a supertype of <T>.
        @Test
        void inspect2__() {
            // T = Synapsida
            {
                final var zoo = new Zoo();
                final var counter = new AtomicInteger();
                final Consumer<Synapsida> consumer = s -> counter.incrementAndGet();
                zoo.inspect2(Synapsida.class, consumer);
                Assertions.assertEquals(2, counter.get());
            }
            {
                final var zoo = new Zoo();
                final var counter = new AtomicInteger();
                final Consumer<Amniota> consumer = a -> counter.incrementAndGet();
                zoo.inspect2(Synapsida.class, consumer); // ✅ Amniota :> Synapsida
                Assertions.assertEquals(2, counter.get());
            }
            {
                final var zoo = new Zoo();
                final var counter = new AtomicInteger();
                final Consumer<Object> consumer = o -> counter.incrementAndGet();
                zoo.inspect2(Synapsida.class, consumer); // ✅ Object :> Synapsida
                Assertions.assertEquals(2, counter.get());
            }
            // T = Cat — narrower T means a wider set of acceptable Consumer supertypes
            {
                final var zoo = new Zoo();
                final var counter = new AtomicInteger();
                final Consumer<Synapsida> consumer = s -> counter.incrementAndGet();
                zoo.inspect2(Cat.class, consumer);       // ✅ Synapsida :> Cat
                Assertions.assertEquals(1, counter.get());
            }
            {
                final var zoo = new Zoo();
                final var counter = new AtomicInteger();
                final Consumer<Amniota> consumer = a -> counter.incrementAndGet();
                zoo.inspect2(Cat.class, consumer);       // ✅ Amniota :> Cat
                Assertions.assertEquals(1, counter.get());
            }
        }

        // ========================================================== capture (wild → zoo) tests

        // capture1 is invariant: the caller MUST pass exactly Collection<Amniota>.
        @Test
        void capture1__() {
            {
                final var zoo = new Zoo();
                final Collection<Amniota> source = new ArrayList<>();
                source.add(new Cat());
                source.add(new Eagle());
                zoo.capture1(source);
            }

            // these would NOT compile — Collection<Amniota> is invariant:
            {
                final var zoo = new Zoo();
                final Collection<Cat> source = new ArrayList<>();
//                zoo.capture1(source); // ❌ Collection<Cat> is not Collection<Amniota>
            }
            {
                final var zoo = new Zoo();
                final Collection<Synapsida> source = new ArrayList<>();
//                zoo.capture1(source); // ❌ won't compile
            }
            {
                final var zoo = new Zoo();
                final Collection<Sauropsida> source = new ArrayList<>();
//                zoo.capture1(source); // ❌ won't compile
            }
        }

        // capture2 follows PECS "Producer Extends":
        // the caller can pass Collection<Amniota> or any Collection<SubtypeOfAmniota>.
        @Test
        void capture2__() {
            {
                final var zoo = new Zoo();
                final Collection<Amniota> source = new ArrayList<>();
                source.add(new Cat());
                source.add(new Eagle());
                zoo.capture2(source);
            }
            {
                final var zoo = new Zoo();
                final Collection<Synapsida> source = new ArrayList<>();
                source.add(new Cat());
                source.add(new Whale());
                zoo.capture2(source); // ✅ Synapsida <: Amniota
            }
            {
                final var zoo = new Zoo();
                final Collection<Sauropsida> source = new ArrayList<>();
                source.add(new Tortoise());
                source.add(new Eagle());
                zoo.capture2(source); // ✅ Sauropsida <: Amniota
            }
            {
                final var zoo = new Zoo();
                final Collection<Cat> source = new ArrayList<>();
                source.add(new Cat());
                source.add(new Cat());
                zoo.capture2(source); // ✅ Cat <: Amniota
            }
        }

        // ============================================================== breed (none → zoo) tests

        // breed1<T> is invariant: the caller MUST pass exactly Supplier<T> for the inferred T.
        @Test
        void breed1__() {
            {
                // T inferred as Cat — supplier yields 3 Cats then null
                final var zoo = new Zoo();
                final var remaining = new AtomicInteger(3);
                final Supplier<Cat> source = () ->
                        remaining.get() > 0 && remaining.decrementAndGet() >= 0 ? new Cat() : null;
                zoo.breed1(source);
                Assertions.assertEquals(0, remaining.get()); // drained to 0
            }
            {
                // T explicitly Synapsida — supplier must be Supplier<Synapsida> exactly
                final var zoo = new Zoo();
                final var remaining = new AtomicInteger(3);
                final Supplier<Synapsida> source = () ->
                        remaining.get() > 0 && remaining.decrementAndGet() >= 0 ? new Cat() : null;
                zoo.breed1(source);
                Assertions.assertEquals(0, remaining.get());
            }

            // this would NOT compile — Supplier<T> is invariant, can't pass narrower-typed supplier:
            {
                final var zoo = new Zoo();
                final Supplier<Cat> source = Cat::new;
//                zoo.<Synapsida>breed1(source); // ❌ Supplier<Cat> is not Supplier<Synapsida>
            }
        }

        // breed2<T> follows PECS "Producer Extends":
        // the caller can pass Supplier<T> or any Supplier<SubtypeOfT>.
        @Test
        void breed2__() {
            {
                // T = Cat (inferred)
                final var zoo = new Zoo();
                final var remaining = new AtomicInteger(3);
                final Supplier<Cat> source = () ->
                        remaining.get() > 0 && remaining.decrementAndGet() >= 0 ? new Cat() : null;
                zoo.breed2(source);
                Assertions.assertEquals(0, remaining.get());
            }
            {
                // T = Synapsida explicitly — Supplier<Cat> is accepted because Cat <: Synapsida
                final var zoo = new Zoo();
                final var remaining = new AtomicInteger(3);
                final Supplier<Cat> source = () ->
                        remaining.get() > 0 && remaining.decrementAndGet() >= 0 ? new Cat() : null;
                zoo.<Synapsida>breed2(source); // ✅ Supplier<Cat> :< Supplier<? extends Synapsida>
                Assertions.assertEquals(0, remaining.get());
            }
            {
                // T = Amniota explicitly — Supplier<Cat> is still accepted
                final var zoo = new Zoo();
                final var remaining = new AtomicInteger(3);
                final Supplier<Cat> source = () ->
                        remaining.get() > 0 && remaining.decrementAndGet() >= 0 ? new Cat() : null;
                zoo.<Amniota>breed2(source); // ✅ Supplier<Cat> :< Supplier<? extends Amniota>
                Assertions.assertEquals(0, remaining.get());
            }
        }

        // =========================================================== release (zoo → wild) tests

        // release1 is invariant: the caller MUST pass exactly Collection<Amniota>.
        @Test
        void release1__() {
            {
                final var zoo = new Zoo();
                final Collection<Amniota> destination = new ArrayList<>();
                zoo.release1(destination);
                Assertions.assertEquals(4, destination.size()); // Cat + Whale + Tortoise + Eagle
            }

            // 12 Monkeys: liberating zoo into Nature's <List<Object> wildlife> would NOT compile:
            {
                final var zoo = new Zoo();
                final var nature = new Nature();
//                zoo.release1(nature.wildlife); // ❌ List<Object> is not Collection<Amniota>
            }
        }

        // release2 follows PECS "Consumer Super":
        // the caller can pass Collection<Amniota>, Collection<Object>, or anything in between.
        @Test
        void release2__() {
            {
                final var zoo = new Zoo();
                final Collection<Amniota> destination = new ArrayList<>();
                zoo.release2(destination);
                Assertions.assertEquals(4, destination.size());
            }
            {
                // 12 Monkeys: liberate the zoo into Nature.
                final var zoo = new Zoo();
                final var nature = new Nature();
                zoo.release2(nature.wildlife); // ✅ List<Object> is Collection<? super Amniota>
                Assertions.assertEquals(4, nature.wildlife.size());
            }
        }

        // ========================================================== map (Function: BOTH PECS sides)

        // tag1: both sides invariant — caller MUST pass exactly Function<T, R>.
        @Test
        void tag1__() {
            // T = Cat, R = Amniota
            {
                final var zoo = new Zoo();
                final Function<Cat, Amniota> mapper = c -> c;
                final List<Amniota> result = zoo.tag1(Cat.class, mapper);
                Assertions.assertEquals(1, result.size()); // one Cat in zoo
            }

            // these would NOT compile — Function<T, R> is invariant on BOTH sides:
            {
                // input mismatch: Function<Amniota, Amniota> is not Function<Cat, Amniota>
                final var zoo = new Zoo();
                final Function<Amniota, Amniota> mapper = a -> a;
//                zoo.tag1(Cat.class, mapper); // ❌ won't compile
            }
            {
                // output mismatch: Function<Cat, Cat> is not Function<Cat, Amniota>
                final var zoo = new Zoo();
                final Function<Cat, Cat> mapper = c -> c;
//                final List<Amniota> result = zoo.tag1(Cat.class, mapper); // ❌ won't compile
            }
        }

        // tag2: full PECS — input "? super T", output "? extends R".
        // Caller can pass Function<SuperOfT, SubtypeOfR>.
        @Test
        void tag2__() {
            // T = Cat, R = Amniota
            {
                final var zoo = new Zoo();
                final Function<Cat, Amniota> mapper = c -> c;
                final List<Amniota> result = zoo.tag2(Cat.class, mapper);
                Assertions.assertEquals(1, result.size());
            }
            {
                // input "super" accepts Function<Amniota, Amniota> (Amniota :> Cat)
                final var zoo = new Zoo();
                final Function<Amniota, Amniota> mapper = a -> a;
                final List<Amniota> result = zoo.tag2(Cat.class, mapper);
                Assertions.assertEquals(1, result.size());
            }
            {
                // output "extends" accepts Function<Cat, Cat> (Cat <: Amniota)
                final var zoo = new Zoo();
                final Function<Cat, Cat> mapper = c -> c;
                final List<Amniota> result = zoo.tag2(Cat.class, mapper);
                Assertions.assertEquals(1, result.size());
            }
            {
                // BOTH sides exercised — Function<Amniota, Cat>:
                // input super, output extends. THE PECS payoff: one mapper, multiple binding sites.
                final var zoo = new Zoo();
                final Function<Amniota, Cat> mapper = a -> new Cat();
                final List<Amniota> result = zoo.tag2(Cat.class, mapper);
                Assertions.assertEquals(1, result.size());
            }
        }

        // ====================================== Gap 1: invariance when read AND write (swap)

        // Caller must pass <List<T>> for the SAME T as the Cats / Synapsidas / Amniotas they're
        // swapping. Neither <? extends T> nor <? super T> would compile inside <swap>.
        @Test
        void swap__() {
            // T = Cat
            {
                final List<Cat> cats = new ArrayList<>(List.of(new Cat(), new Cat()));
                final var first = cats.getFirst();
                Zoo.swap(cats, 0, 1);
                Assertions.assertSame(first, cats.get(1));
            }
            // T = Synapsida
            {
                final List<Synapsida> synapsidas = new ArrayList<>(List.of(new Cat(), new Whale()));
                final var first = synapsidas.getFirst();
                Zoo.swap(synapsidas, 0, 1);
                Assertions.assertSame(first, synapsidas.get(1));
            }
            // T = Amniota
            {
                final List<Amniota> amniotas = new ArrayList<>(List.of(new Cat(), new Eagle()));
                final var first = amniotas.getFirst();
                Zoo.swap(amniotas, 0, 1);
                Assertions.assertSame(first, amniotas.get(1));
            }
            // these would NOT have worked inside <swap> if we'd tried PECS — but unlike the
            // caller-side experiments we did for collect/inspect/etc., the failure here
            // is on the IMPLEMENTATION side. Try replacing <List<T>> with <List<? extends T>>
            // or <List<? super T>> in <swap> — the body won't compile.
        }

        // ============================ Gap 2: don't use wildcards as return types (roster)

        // roster1 returns invariant Collection<Amniota> — easy to use.
        // roster2 returns Collection<? extends Amniota> — caller is stuck with wildcards.
        @Test
        void roster__() {
            final var zoo = new Zoo();

            // roster1: free flow into any Collection<Amniota> sink
            {
                final Collection<Amniota> snap = zoo.roster1();
                final List<Amniota> sink = new ArrayList<>();
                sink.addAll(snap);                      // ✅ compiles
                Assertions.assertEquals(4, sink.size());
            }

            // roster2: caller has Collection<? extends Amniota>; can read but can't pass to
            // any API that expects an invariant Collection<Amniota>:
            {
                final Collection<? extends Amniota> snap = zoo.roster2();
                // this would NOT compile — wildcard return forces the caller into wildcard plumbing:
                //     final List<Amniota> sink = new ArrayList<>();
                //     sink.addAll(snap); // works (addAll accepts Collection<? extends E>)
                //     final Collection<Amniota> reusable = snap; // ❌ wildcard not assignable to invariant
                Assertions.assertEquals(4, snap.size()); // still fine to read
            }
        }

        // ============================== Gap 3: recursive bound <T extends Comparable<? super T>>

        // <heaviest> demands T's elements know how to compare themselves to a T (or supertype of T).
        // Cats are NOT Comparable<Cat>; they ARE Comparable<Amniota> (via Amniota's
        // implements Comparable<Amniota>). The <? super T> in the bound is what lets <Cat> bind:
        // <Cat extends Comparable<? super Cat>> ✅ because Comparable<Amniota> satisfies it.
        @Test
        void heaviest__() {
            // T = Cat
            {
                final List<Cat> cats = List.of(new Cat(), new Cat());
                final Cat heaviest = Zoo.heaviest(cats);
                Assertions.assertNotNull(heaviest);
            }
            // T = Synapsida — Cat (4.5kg) vs Whale (5_000kg) ⇒ Whale
            {
                final List<Synapsida> synapsidas = List.of(new Cat(), new Whale());
                final Synapsida heaviest = Zoo.heaviest(synapsidas);
                Assertions.assertInstanceOf(Whale.class, heaviest);
            }
            // T = Sauropsida — Tortoise (2kg) vs Eagle (5kg) ⇒ Eagle
            {
                final List<Sauropsida> sauropsidas = List.of(new Tortoise(), new Eagle());
                final Sauropsida heaviest = Zoo.heaviest(sauropsidas);
                Assertions.assertInstanceOf(Eagle.class, heaviest);
            }
            // T = Amniota — Whale wins overall
            {
                final List<Amniota> all = List.of(new Cat(), new Tortoise(), new Whale(),
                                                  new Eagle());
                final Amniota heaviest = Zoo.heaviest(all);
                Assertions.assertInstanceOf(Whale.class, heaviest);
            }
            // Producer Extends side: passing Collection<Cat> when T = Synapsida is also fine,
            // because of <Collection<? extends T>>:
            {
                final List<Cat> cats = List.of(new Cat(), new Cat());
                final Synapsida heaviest = Zoo.<Synapsida>heaviest(cats); // ✅ Cat <: Synapsida
                Assertions.assertInstanceOf(Cat.class, heaviest);
            }
        }
    }
}
