package com.github.jinahya.hello.miscellaneous;

import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.*;
import java.time.temporal.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import static com.github.jinahya.hello.miscellaneous._Java_Util_Concurrent_SubmissionPublisher_TestUtils.*;
import static com.github.jinahya.hello.miscellaneous._Org_Mockito__TestUtils.OfFlow.*;
import static org.awaitility.Awaitility.*;

@DisplayName("java.util.concurrent.Flow")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
final class _Java_Util_Concurrent_Flow__Test {

    /**
     * A {@link SubmissionPublisher} of an arbitrary {@link Enum} type {@code E}, with a
     * {@link #submitAll() submitAll()} convenience that submits every enum constant in
     * {@linkplain Class#getEnumConstants() declaration order}.
     */
    private static class EnumPublisher<E extends Enum<E>> extends SubmissionPublisher<E> {

        EnumPublisher(final Class<E> enumType) {
            super();
            values = List.of(enumType.getEnumConstants());
        }

        final void submitAll() {
            values.forEach(this::submit);
        }

        final List<E> values;
    }

    /**
     * A multicast {@link Flow.Publisher} of an arbitrary {@link Temporal} type {@code T}: wraps a
     * {@link SubmissionPublisher} and schedules a
     * {@linkplain ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)
     * fixed-rate tick} that reads the supplied {@link Clock} through {@code extractor} and submits
     * the result every {@code tickDuration}, until {@link #close() closed}.
     */
    private static final class ClockPublisher<T extends Temporal>
            implements Flow.Publisher<T>, AutoCloseable {

        ClockPublisher(final Clock baseClock, final Function<Clock, T> itemMapper,
                       final Duration tickDuration) {
            super();
            this.baseClock = Objects.requireNonNull(baseClock, "baseClock is null");
            this.itemMapper = Objects.requireNonNull(itemMapper, "itemMapper is null");
            publisher = new SubmissionPublisher<>();
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(
                    () -> publisher.submit(this.itemMapper.apply(this.baseClock)),
                    0L,
                    Objects.requireNonNull(tickDuration, "tickDuration is null").toNanos(),
                    TimeUnit.NANOSECONDS);
        }

        @Override
        public void subscribe(final Flow.Subscriber<? super T> subscriber) {
            publisher.subscribe(subscriber);
        }

        @Override
        public void close() {
            scheduler.shutdownNow();
            publisher.close();
        }

        private final Clock baseClock;

        private final Function<Clock, T> itemMapper;

        private final SubmissionPublisher<T> publisher;

        private final ScheduledExecutorService scheduler;
    }

    // ---------------------------------------------------------------------------------------------
    private static MockedConstruction<SubmissionPublisher> SUBMISSION_PUBLISHER_CONSTRUCTION;

    @BeforeAll
    static void __stubSubmissionPublisherMockConstruct() {
        SUBMISSION_PUBLISHER_CONSTRUCTION = loggingSubmissionPublisherConstruction();
    }

    @AfterAll
    static void __closeSubmissionPublisherMockConstruct() {
        SUBMISSION_PUBLISHER_CONSTRUCTION.close();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that, when two subscribers attach to an {@link EnumPublisher} of {@link DayOfWeek}
     * and each {@linkplain Flow.Subscription#request(long) requests} a different, randomly-chosen
     * positive count, each subscriber receives exactly the first
     * {@code min(request, DayOfWeek.values().length)} {@link DayOfWeek} values in declaration order
     * from {@link DayOfWeek#MONDAY MONDAY}.
     */
    @DisplayName(
            "back-pressure — each subscriber receives <min(request, 7)> <DayOfWeek> values from <MONDAY>")
    @Test
    void __DayOfWeek() {
        try (final var publisher = new EnumPublisher<>(DayOfWeek.class)) {
            for (int i = 0; i < 3; i++) {
                publisher.subscribe(
                        loggingSubscriberRequestsOnSubscribe(
                                ThreadLocalRandom.current().nextInt(publisher.values.size()) + 1
                        )
                );
            }
            publisher.submitAll();
        }
    }

    /**
     * Verifies the multicast behavior of a {@link ClockPublisher} of {@link LocalTime} that
     * self-ticks every 100&nbsp;ms via
     * {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)
     * scheduleAtFixedRate(...)}: the early subscriber attaches first and observes every tick, while
     * a late subscriber attaches two seconds later and observes only the values published after it
     * subscribed. The publisher runs for approximately five seconds before being
     * {@link ClockPublisher#close() closed}.
     */
    @DisplayName(
            "multicast — scheduled-tick producer streams <LocalTime> values for ~5s; late subscriber gets only later ones")
    @Test
    void __LocalTime() {
        try (final var publisher = new ClockPublisher<>(Clock.systemDefaultZone(), LocalTime::now,
                                                        Duration.ofMillis(100L))) {
            publisher.subscribe(loggingSubscriberRequestsOnSubscribe(Long.MAX_VALUE));
            await().pollDelay(Duration.ofMillis(400L)).until(() -> true);
            publisher.subscribe(loggingSubscriberRequestsOnSubscribe(Long.MAX_VALUE));
            await().pollDelay(Duration.ofMillis(800L)).until(() -> true);
        }
    }
}
