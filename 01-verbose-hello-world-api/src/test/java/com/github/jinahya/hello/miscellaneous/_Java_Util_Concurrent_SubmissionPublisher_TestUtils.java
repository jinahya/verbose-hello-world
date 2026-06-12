package com.github.jinahya.hello.miscellaneous;

import lombok.extern.slf4j.*;
import org.mockito.*;

import java.util.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.miscellaneous._Java_Lang_Reflect_TestUtils.*;
import static com.github.jinahya.hello.miscellaneous._Java_Lang_TestUtils.*;
import static com.github.jinahya.hello.miscellaneous._Org_Mockito__TestUtils.OfFlow.*;
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
    public static MockedConstruction<SubmissionPublisher> loggingMockConstruction() {
        // mock -> its logging-spy sibling; populated by the initializer, read by the default answer
        // (which fires for every unstubbed method call on the mock — submit/close/hasSubscribers/…).
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
                    // route subscribe through loggingPublisher so the inner subscribe is logged
                    // by the shared helper. NOTE: Mockito's spiedInstance creates a *sibling* of
                    // `real` with copied state — operations on the spy use the spy's own state,
                    // not `real`'s — so every method call on the mock must hit this same spy.
                    final var logging = (SubmissionPublisher<Object>) loggingPublisher(real);
                    siblings.put(mock, logging);
                    // subscribe is the only call that needs custom handling: wrap the incoming
                    // subscriber so its subscription's request(n)/cancel() are logged. Every other
                    // method (submit/close/closeExceptionally/hasSubscribers/...) falls through to
                    // the default answer above and is forwarded to `logging` unchanged.
                    doAnswer(i -> { // @formatter:off
                        final Flow.Subscriber<Object> subscriber = i.getArgument(0);
                        logging.subscribe(new Flow.Subscriber<>() {
                            @Override public void onSubscribe(final Flow.Subscription s) {
                                subscriber.onSubscribe(new Flow.Subscription() {
                                    @Override public void request(final long n) {
                                        log.debug("{}.request({})", toSimplifedString(s), n);
                                        s.request(n);
                                    }
                                    @Override public void cancel() {
                                        log.debug("{}.cancel()", toSimplifedString(s));
                                        s.cancel();
                                    }
                                });
                            }
                            @Override public void onNext(final Object item)  {
                                subscriber.onNext(item);
                            }
                            @Override public void onError(final Throwable t) {
                                subscriber.onError(t);
                            }
                            @Override public void onComplete() {
                                subscriber.onComplete();
                            }
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
