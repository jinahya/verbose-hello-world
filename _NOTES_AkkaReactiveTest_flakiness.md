# Notes: AkkaReactiveHelloWorldFactoryTest.multiple_subscriptions_with_multiple_requests flakiness

Date: 2026-05-05
Status: **Speculation only — could not reproduce locally** (50+ sequential runs all passed; CPU-stress run not completed).

## What's flaky (per user)

```
[ERROR] com.github.jinahya.hello.api.reactive.AkkaReactiveHelloWorldFactoryTest.multiple_subscriptions_with_multiple_requests
```

Test name occurs in three `@Nested` classes: `OctetPublisherTest`, `ArrayPublisherTest`, `StringPublisherTest`. The user did not specify which one. Need the surefire stack trace next time it fails to disambiguate.

## Files involved

- `01-verbose-hello-world-api/src/test/java/com/github/jinahya/hello/api/reactive/ReactiveHelloWorldFactoryTest.java`
  - `SubscriberForTesting` (line ~45): `final List<E> items = new ArrayList<>();` (NOT thread-safe)
  - `SubscriberForTesting.request()` (line ~107): throws `IllegalStateException` if `subscription == null`
  - Three nested test methods named `multiple_subscriptions_with_multiple_requests`
- `01-verbose-hello-world-api/src/test/java/com/github/jinahya/hello/api/reactive/AkkaReactiveHelloWorldFactory.java`
  - `newOctetPublisher()` uses Akka `Sink.asPublisher(WITHOUT_FANOUT)` — single subscriber, async `onSubscribe`
  - `newArrayPublisher()` and `newStringPublisher()` use `AbstractReactiveHelloWorldPublisher` (sync `onSubscribe`) but each request handler subscribes to a fresh Akka octet publisher
- `01-verbose-hello-world-api/src/test/java/com/github/jinahya/hello/api/reactive/AbstractReactiveHelloWorldPublisher.java`
  - `subscribe()` calls `subscriber.onSubscribe(...)` synchronously
  - `createStandardSubscription.request(n)` calls request handler `n` times synchronously on caller thread

## Top suspects (ranked)

### Suspect #1 — Concurrent `items.add` on non-thread-safe `ArrayList` (Array/String tests)

For `subscriber1` in the array test: initial `request(2)` + two `request(1)` = 4 outstanding array requests. Each request handler invocation (run on the **calling thread** inside `createStandardSubscription.request`) subscribes to a **fresh** Akka octet publisher. Each octet stream runs on its own Akka dispatcher thread.

When each octet stream completes, its inner `Subscriber<Byte>.onComplete()` calls `subscriber.onNext(array)` from the Akka dispatcher thread. Up to 4 Akka threads can hit `subscriber1.items.add(...)` concurrently.

`ArrayList.add` is not atomic. Concurrent calls can cause:
- **Null entries** → `Assertions.assertNotNull(array)` fails
- **Lost items** (size mismatch) → in this test the bound is `>= 1` so usually survives
- `ArrayIndexOutOfBoundsException` from internal resize race
- `ConcurrentModificationException` during the for-each iteration in the assertion block (if a late onNext fires after `awaitCompletionOrError` returned)

Same shape applies to `StringPublisherTest`.

**Does NOT apply to OctetPublisherTest**: Akka `WITHOUT_FANOUT` only attaches one subscriber and delivers serially.

### Suspect #2 — Async `onSubscribe` race in OctetPublisherTest

`Sink.asPublisher` delivers `onSubscribe` on an Akka dispatcher thread. The test does:

```java
publisher.subscribe(subscriber1);   // schedules async onSubscribe
publisher.subscribe(subscriber2);
subscriber1.request(1L);            // SubscriberForTesting.request throws if subscription == null
```

If the dispatcher hasn't called `onSubscribe(subscription)` on `subscriber1` by the time the test thread reaches `subscriber1.request(1L)`, `SubscriberForTesting.request` throws `IllegalStateException("No subscription available")` and fails the test.

Local debug logs show `[main]` thread doing the `onSubscribe` for the first subscriber after a ~140 ms gap (Akka materialization), so the race window is real on slower CI.

### Suspect #3 — `Thread.sleep(100L)` too short in OctetPublisherTest

Octet test sleeps 100 ms then asserts `totalItems > 0`. Under CPU contention this could be insufficient for any byte to arrive at `subscriber1`.

## How to disambiguate (next failure)

Inspect the surefire stack trace:
- `AssertionFailedError` on `assertNotNull(array)` or per-byte equality → **Suspect #1** (ArrayList race, Array/String test)
- `IllegalStateException: No subscription available. Call request() after onSubscribe()` → **Suspect #2** (Octet test, async onSubscribe race)
- `AssertionFailedError` on `totalItems > 0` → **Suspect #3** (Octet test, sleep too short) or also #2
- `ConcurrentModificationException` during the for-each → **Suspect #1**

Check `target/surefire-reports/TEST-com.github.jinahya.hello.api.reactive.AkkaReactiveHelloWorldFactoryTest.xml` for `<failure>`/`<error>` entries with the nested classname (`ArrayPublisher` / `StringPublisher` / `OctetPublisher`).

## Suggested fixes (not applied yet — waiting on confirmation)

1. Make `SubscriberForTesting.items` thread-safe:
   - `final List<E> items = Collections.synchronizedList(new ArrayList<>());`
   - And iterate inside `synchronized (items) { ... }` blocks in the test assertions
   - Or use `CopyOnWriteArrayList`
2. Make `SubscriberForTesting.request` await subscription instead of throwing:
   ```java
   void request(final long n) {
       Awaitility.await().atMost(Duration.ofSeconds(5)).until(() -> subscription != null);
       subscription.request(n);
   }
   ```
3. Bump the `Thread.sleep(100L)` in the OctetPublisher test to something larger (or replace with Awaitility on a stable condition).

## Reproduction notes

- 50+ sequential `./mvnw test -pl 01-verbose-hello-world-api -Dtest="AkkaReactiveHelloWorldFactoryTest"` runs on this machine: 0 failures.
- Need to try: parallel runs, CPU stress (`yes > /dev/null` × N), CI environment, or running the full test class set together.
- The frequent debug-log entries `IllegalStateException` (from second WITHOUT_FANOUT subscriber being rejected) and `AbruptTerminationException` (from `actorSystem.terminate()` while streams were still in flight) are **expected** — don't confuse them with test failures.
