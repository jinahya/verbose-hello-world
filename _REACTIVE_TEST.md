# Reactive publisher testing plan

> **TestNG is not allowed in this project**, so the Reactive Streams TCK and TCK-Flow are off the
> table. Trade-off: we don't get the ~180-test spec-conformance gauntlet for free; we cover the
> same rules with hand-rolled JUnit 5 tests at lower density.

## Subjects (what we test)

Four production targets in `com.github.jinahya.hello.api`:

| # | Subject | Type | Factory |
|---|---|---|---|
| 1 | `ReactiveHelloWorldBytePublisher` | `org.reactivestreams.Publisher<Byte>` | `ReactiveHelloWorldPublishers.ofBytes(service)` |
| 2 | `ReactiveHelloWorldArrayPublisher` | `org.reactivestreams.Publisher<byte[]>` | `ReactiveHelloWorldPublishers.ofArrays(service)` |
| 3 | `ReactiveHelloWorldStringPublisher` | `org.reactivestreams.Publisher<String>` | `ReactiveHelloWorldPublishers.ofStrings(service)` |
| 4 | `HelloWorldFlow` | `java.util.concurrent.Flow.Publisher<T>` (Byte / byte[] / String) | `HelloWorldFlow.ofBytes/ofArrays/ofStrings(service)` |

Subject 4 is *not* a separate publisher — it's the same three publishers from (1)–(3) wrapped as `Flow.Publisher` via `FlowAdapters.toFlowPublisher(...)`.

## Methods (how we test)

All JUnit 5.

| Method | Library / artifact | Tests against |
|---|---|---|
| Self-contained baseline | (none) | RS `Publisher<T>` |
| Project Reactor | `io.projectreactor:reactor-core` (`Flux`) | RS `Publisher<T>` |
| RxJava 3 | `io.reactivex.rxjava3:rxjava` (`Flowable`) | RS `Publisher<T>` |
| SmallRye Mutiny | `io.smallrye.reactive:mutiny` (`Multi`) | JDK `Flow.Publisher<T>` (via `HelloWorldFlow`) |
| Akka Streams | `com.typesafe.akka:akka-stream_3` (`Source`) | RS `Publisher<T>` |

## The matrix

### Tier 1 — self-contained baseline (1 class)

| File | Subjects |
|---|---|
| `ReactiveHelloWorldPublishersTest` | 1, 2, 3 (the three RS publishers) |

Tests the three Reactive Streams publishers directly with a hand-rolled `Subscriber` helper. Covers: request all, request partial, request `<=0` (Rule 3.9), cancel (Rule 3.12), multiple subscribers.

### Tier 2 — library interop (4 classes × 3 nested = 12 cells)

Each library gets one top-level test class with three nested classes (one per publisher type).

| File | Library API | Source |
|---|---|---|
| `ReactiveHelloWorldPublishersReactorTest` | `Flux.from(...)` | RS publisher |
| `ReactiveHelloWorldPublishersRxJava3Test` | `Flowable.fromPublisher(...)` | RS publisher |
| `ReactiveHelloWorldPublishersMutinyTest` | `Multi.createFrom().publisher(...)` | **Flow** (via `HelloWorldFlow`) |
| `ReactiveHelloWorldPublishersAkkaTest` | `Source.fromPublisher(...)` | RS publisher |

Each nested class verifies:
- happy-path consumption (`.collectList()` / `.toList()` / `.collect().asList()` / `.runWith(Sink.seq())`),
- equivalence with the expected `"hello, world"` payload,
- shape: `Byte` × 12 / one `byte[]` per request / one `String` per request.

## File count summary

```
Tier 1   1   self-contained baseline
Tier 2   4   library interop (3 nested cells each = 12 cells)
─────────────
total    5   test classes
```

Expected test count (rough):
- Tier 1: ~11 cases
- Tier 2: ~3–5 cases × 12 cells ≈ ~40–60 cases

≈ **50–70 test cases** at full coverage. All hand-rolled with JUnit 5.

## Recommended order of attack

1. **Tier 1** — self-contained baseline first; provides quick smoke-test of the publishers themselves.
2. **Tier 2 — Reactor**, then **RxJava 3**: the simplest libraries (Reactive Streams `Publisher` flows in directly, no Flow adapter, no Materializer).
3. **Tier 2 — Mutiny**: takes `Flow.Publisher`, so source is `HelloWorldFlow.ofXxx(...)` rather than `ReactiveHelloWorldPublishers.ofXxx(...)`. Demonstrates the Flow/RS bridge implicitly.
4. **Tier 2 — Akka**: most boilerplate (`ActorSystem` + `Materializer` setup). Do last.

## Spec rule coverage (no-TCK trade-off)

Without the TCK, we lose comprehensive coverage of edge cases. Tier 1 covers the rules we exercise explicitly; Tier 2 mostly covers happy paths.

| Rule | Tier 1 | Tier 2 |
|---|---|---|
| 1.1 — `onNext` ≤ requested | ✓ | weakly |
| 1.3 — serial signals | implicit | implicit |
| 1.4 — error → `onError` | partial | partial |
| 1.5 — completion → `onComplete` | ✓ | ✓ |
| 1.7 — single terminal | partial | partial |
| 1.9 — `subscribe` returns normally | implicit | implicit |
| 3.5 — `cancel` thread-safe/timely | partial | partial |
| 3.6 — `request` after cancel = NOP | partial | — |
| 3.9 — `request(<=0)` → `onError(IAE)` | ✓ | — |
| 3.12 — `cancel` eventually stops | partial | partial |
| 3.17 — demand up to `Long.MAX_VALUE` | partial | — |

For book purposes, this is enough. Production hardening would want the TCK — that conversation belongs in a different chapter ("the spec also ships a TCK, but it requires TestNG; here's how you'd wire it up in a TestNG-friendly module").
