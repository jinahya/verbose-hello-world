package com.github.jinahya.hello.miscellaneous;

import lombok.extern.slf4j.*;
import org.mockito.*;

import java.util.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.miscellaneous._java_lang_reflect.__Java_Lang_Reflect_TestUtils.*;
import static com.github.jinahya.hello.miscellaneous._Org_Mockito__TestUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Slf4j
public final class _Java_Util_Concurrent_SubmissionPublisher_TestUtils {

    /**
     * Returns a {@link MockedConstruction} that intercepts every
     * {@code new SubmissionPublisher(...)} on the current thread and replaces it with a Mockito
     * mock whose {@code subscribe} / {@code submit} / {@code close} / {@code closeExceptionally}
     * calls are routed through a
     * {@linkplain
     * _Org_Mockito__TestUtils.OfFlow#loggingPublisher(java.util.concurrent.Flow.Publisher) logging
     * spy} of a real, sibling {@link SubmissionPublisher} — so the inner subscribe is logged and
     * every subscription's {@code request(n)} / {@code cancel()} are logged too.
     * <p>
     * Use in {@code @BeforeAll} / {@code @AfterAll}:
     * <pre>{@code
     * private static MockedConstruction<SubmissionPublisher> CONSTRUCTION;
     * @BeforeAll static void open()  { CONSTRUCTION = loggingMockConstruction(); }
     * @AfterAll static void close() { CONSTRUCTION.close(); }
     * }</pre>
     *
     * @return a {@link MockedConstruction} of {@link SubmissionPublisher}; close it when done.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static MockedConstruction<SubmissionPublisher> loggingSubmissionPublisherConstruction() {
        // mock -> its off-thread real sibling; populated by the initializer, read by the default
        // answer (which fires for every unstubbed call — submit/close/hasSubscribers/...).
        final Map<Object, SubmissionPublisher<Object>> siblings = new IdentityHashMap<>();
        return mockConstruction(
                SubmissionPublisher.class,
                ctx -> withSettings().defaultAnswer(i -> {
                    final var sibling = siblings.get(i.getMock());
                    return i.getMethod().invoke(sibling, i.getArguments());
                }),
                (mock, context) -> {
                    final var args = context.arguments();
                    final var constructor = findConstructor(SubmissionPublisher.class, args);
                    constructor.setAccessible(true);
                    // construct the real instance off-thread so mockConstruction (thread-local on
                    // the registering thread) does not intercept and return yet another mock.
                    final var real = (SubmissionPublisher<Object>) CompletableFuture.supplyAsync(
                            () -> {
                                try {
                                    return constructor.newInstance(args.toArray());
                                } catch (final ReflectiveOperationException roe) {
                                    throw new AssertionError(roe);
                                }
                            }).join();
                    siblings.put(mock, real);
                    final String mockId = toSimplifedString(mock);
                    // subscribe(...) is the only call that needs custom handling: log with the
                    // mock's own identity (so reads consistent with the test's `publisher`), then
                    // register a sub-wrapping forwarder with the real publisher so the subscriber
                    // sees a logging Subscription. Every other method (submit/close/...) falls
                    // through to the default answer and is forwarded to `real` unchanged.
                    doAnswer(i -> { // @formatter:off
                        final Flow.Subscriber<Object> subscriber = i.getArgument(0);
                        log.debug("{}.subscribe({})", mockId, toSimplifedString(subscriber));
                        real.subscribe(new Flow.Subscriber<>() {
                            @Override public void onSubscribe(final Flow.Subscription s) {
                                subscriber.onSubscribe(new Flow.Subscription() {
                                    @Override public void request(final long n) {
                                        log.debug("{}.request({})", toSimplifedString(this), n);
                                        s.request(n);
                                    }
                                    @Override public void cancel() {
                                        log.debug("{}.cancel()", toSimplifedString(this));
                                        s.cancel();
                                    }
                                });
                            }
                            @Override public void onNext(final Object item)  { subscriber.onNext(item); }
                            @Override public void onError(final Throwable t) { subscriber.onError(t); }
                            @Override public void onComplete() { subscriber.onComplete(); }
                        });
                        return null;
                    }).when(mock).subscribe(any()); // @formatter:on
                });
    }

    // ---------------------------------------------------------------------------------------------
    private _Java_Util_Concurrent_SubmissionPublisher_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
