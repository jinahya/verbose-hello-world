# Reactive I/O Report

## Overview

The reactive I/O support in this project provides **two complementary approaches** to reactive programming, both implementing the Reactive Streams specification but using different APIs:

1. **ReactiveHelloWorld** - Uses external `org.reactivestreams.Publisher` (Reactive Streams library)
2. **HelloWorldFlow** - Uses Java 9+ built-in `java.util.concurrent.Flow.Publisher`

Both approaches teach reactive programming concepts but serve different educational purposes.

## Core Components

### 1. ReactiveHelloWorld Interface

**Interface:** `com.github.jinahya.hello.api.ReactiveHelloWorldFactory`

**Purpose:** Defines reactive publishers using **external Reactive Streams library**

**API Base:** `org.reactivestreams.Publisher` (external library dependency)

**Current State:** Minimal interface (work in progress) - provides three publisher methods

**Methods:**
- `Publisher<Byte> getHelloWorldCharsPublisher()` - Publishes each byte individually
- `Publisher<byte[]> getHelloWorldBytesPublisher()` - Publishes complete byte array
- `Publisher<String> getHelloWorldStringPublisher()` - Publishes string representation

**Design Philosophy:** 
- Uses external Reactive Streams library (`org.reactivestreams`)
- Compatible with libraries that use Reactive Streams directly (Project Reactor, RxJava, etc.)
- Requires external dependency on `reactive-streams` library
- Follows the same pattern as `AsynchronousHelloWorld` (interface-based approach)

**Note:** This interface is currently minimal and does not include:
- Factory methods (unlike `AsynchronousHelloWorld.from()`)
- Default implementations
- Direct connection to `HelloWorldFlow` implementations

### 2. HelloWorldFlow Utility Class

**Class:** `com.github.jinahya.hello.api.HelloWorldFlow`

**Purpose:** Provides concrete implementations of Reactive Streams components using **Java 9+ built-in Flow API**

**API Base:** `java.util.concurrent.Flow.Publisher` (Java 9+ built-in, no external dependency)

**Components:**
- Publishers (4 types)
- Subscribers (4 types)
- Processors (for transformations)

**Design Philosophy:**
- Uses Java's built-in `Flow` API (no external dependencies)
- Demonstrates pure Java implementation of Reactive Streams
- Shows how Reactive Streams concepts map to Java's standard library
- Complete, working implementation with full backpressure support

**Relationship with ReactiveHelloWorld:**
- **Different approaches, same specification** - Both implement Reactive Streams spec
- `ReactiveHelloWorld` = Interface using external library (`org.reactivestreams`)
- `HelloWorldFlow` = Concrete implementations using Java built-in (`java.util.concurrent.Flow`)
- They are **complementary** - teaching both external library and Java built-in approaches
- A `DefaultReactiveHelloWorld` implementation could bridge them by adapting `Flow.Publisher` to `org.reactivestreams.Publisher`

## Publisher Implementations

### HelloWorldPublisher.OfByte
- **Type:** `Flow.Publisher<Byte>`
- **Behavior:** Publishes each byte of hello-world-bytes individually
- **Features:**
  - Backpressure-aware (waits for `request(n)`)
  - Asynchronous publishing via executor
  - Flow-controlled subscription

### HelloWorldPublisher.OfArray
- **Type:** `Flow.Publisher<byte[]>`
- **Behavior:** Publishes complete byte arrays
- **Features:**
  - Composes `OfByte` publisher internally
  - Accumulates bytes into array
  - Publishes array on completion

### HelloWorldPublisher.OfBuffer
- **Type:** `Flow.Publisher<ByteBuffer>`
- **Behavior:** Publishes `ByteBuffer` instances
- **Features:**
  - Uses `OfArray` as source
  - Transforms arrays to buffers via processor
  - Demonstrates processor pattern

### HelloWorldPublisher.OfString
- **Type:** `Flow.Publisher<String>`
- **Behavior:** Publishes string representation
- **Features:**
  - Uses `OfBuffer` as source
  - Decodes buffer to string (US_ASCII)
  - Demonstrates transformation chain

## Subscriber Implementations

### HelloWorldSubscriber.OfByte
- **Type:** `Flow.Subscriber<Byte>`
- **Purpose:** Consumes individual bytes
- **Output:** Logs each byte as hex and character

### HelloWorldSubscriber.OfArray
- **Type:** `Flow.Subscriber<byte[]>`
- **Purpose:** Consumes byte arrays
- **Output:** Logs array representation

### HelloWorldSubscriber.OfBuffer
- **Type:** `Flow.Subscriber<ByteBuffer>`
- **Purpose:** Consumes ByteBuffer instances
- **Output:** Logs buffer representation

### HelloWorldSubscriber.OfString
- **Type:** `Flow.Subscriber<String>`
- **Purpose:** Consumes string values
- **Output:** Logs string (typically "hello, world")

## Key Features

### 1. Backpressure Support
- Publishers wait for `request(n)` before emitting
- Accumulates demand using `AtomicLong`
- Uses `ReentrantLock` and `Condition` for signaling
- Prevents overwhelming subscribers

### 2. Flow Control
```java
// Subscription pattern
subscriber.onSubscribe(new Flow.Subscription() {
    @Override
    public void request(long n) {
        // Accumulate demand
        accumulated.accumulateAndGet(n, (c, u) -> (c + u) & Long.MAX_VALUE);
        condition.signal(); // Wake publisher
    }
    
    @Override
    public void cancel() {
        future.cancel(true); // Stop publishing
    }
});
```

### 3. Processor Pattern
- Transforms between types (array → buffer → string)
- Implements both `Publisher` and `Subscriber`
- Enables transformation pipelines

### 4. Asynchronous Execution
- All publishers use `ExecutorService`
- Items published in separate threads
- Non-blocking subscription setup

## Integration with Reactive Libraries

The project tests integration with multiple reactive libraries:

### 1. Project Reactor
- **Library:** `reactor-core` 3.8.1
- **Integration:** Via `FlowAdapters`
- **Usage:** Convert `Flow.Publisher` to `Mono`/`Flux`

### 2. RxJava 3
- **Library:** `rxjava` 3.1.12
- **Integration:** Via reactive-streams adapters
- **Usage:** Convert to `Observable`/`Single`

### 3. SmallRye Mutiny
- **Library:** `mutiny` (Quarkus reactive library)
- **Integration:** Via adapters
- **Usage:** Convert to `Uni`/`Multi`

### 4. Eclipse Vert.x
- **Library:** `vertx-reactive-streams`
- **Integration:** Via reactive-streams bridge
- **Usage:** Convert to Vert.x reactive types

## Teaching Points

### 1. Reactive Streams Specification
- **Publisher:** Produces items
- **Subscriber:** Consumes items
- **Subscription:** Controls flow (request/cancel)
- **Processor:** Both publisher and subscriber

### 2. Backpressure
- Subscriber controls rate via `request(n)`
- Prevents memory issues with fast producers
- Demonstrates demand-driven flow

### 3. Transformation Pipelines
```
Byte → Array → Buffer → String
```
- Shows how to chain processors
- Demonstrates data transformation
- Type-safe conversions

### 4. Asynchronous Processing
- Non-blocking item production
- Thread-safe demand accumulation
- Proper cancellation handling

### 5. Error Handling
- `onError()` in subscribers
- Error propagation through processors
- Graceful failure handling

## Code Examples

### Basic Usage
```java
HelloWorld service = new HelloWorldImpl();
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

// Create publisher
HelloWorldPublisher.OfString publisher = 
    new HelloWorldPublisher.OfString(service, executor);

// Subscribe
publisher.subscribe(new HelloWorldSubscriber.OfString());
```

### With Flow Control
```java
publisher.subscribe(new Flow.Subscriber<String>() {
    private Flow.Subscription subscription;
    
    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1); // Request one item
    }
    
    @Override
    public void onNext(String item) {
        System.out.println(item);
        subscription.request(1); // Request next item
    }
    
    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }
    
    @Override
    public void onComplete() {
        System.out.println("Done!");
    }
});
```

## Comparison with Other Approaches

| Aspect | Synchronous | Asynchronous | Reactive |
|--------|------------|--------------|----------|
| **API** | Direct return | `CompletionStage` | `Publisher`/`Subscriber` |
| **Backpressure** | N/A | N/A | ✅ Built-in |
| **Composition** | Sequential | `thenCompose()` | `Processor` chains |
| **Error Handling** | try-catch | `exceptionally()` | `onError()` |
| **Multiple Items** | Loop | Multiple futures | ✅ Stream |
| **Flow Control** | N/A | N/A | ✅ `request(n)` |

## Coverage Summary

| Component | Status | Notes |
|-----------|--------|-------|
| **Publishers** | ✅ 4 types | Byte, Array, Buffer, String |
| **Subscribers** | ✅ 4 types | Matching publishers |
| **Processors** | ✅ 2 types | Array→Buffer, Buffer→String |
| **Library Integration** | ✅ 4 libraries | Reactor, RxJava, Mutiny, Vert.x |

## Two Approaches: Design Rationale

### Why Two Different Approaches?

The project intentionally provides **two complementary approaches** to reactive programming:

| Aspect | ReactiveHelloWorld | HelloWorldFlow |
|--------|-------------------|----------------|
| **API** | `org.reactivestreams.Publisher` | `java.util.concurrent.Flow.Publisher` |
| **Dependency** | External library | Java built-in (Java 9+) |
| **Purpose** | Interface for external Reactive Streams | Concrete implementations with Java Flow |
| **Teaching Focus** | Library-based reactive programming | Java standard library reactive programming |
| **Compatibility** | Works with Reactor, RxJava directly | Requires adapter for external libraries |

### Design Intent

1. **ReactiveHelloWorld** - Teaches reactive programming using the **external Reactive Streams library**
   - Shows how to work with `org.reactivestreams.Publisher`
   - Compatible with libraries that use Reactive Streams directly
   - Follows interface pattern (like `AsynchronousHelloWorld`)

2. **HelloWorldFlow** - Teaches reactive programming using **Java's built-in Flow API**
   - Shows how Reactive Streams concepts are implemented in Java standard library
   - No external dependencies required
   - Demonstrates complete implementation with backpressure

Both are valid approaches and teach different aspects of reactive programming in Java.

## Current Implementation Status

### ReactiveHelloWorld Interface

**Status:** ⚠️ **Work in Progress** - Minimal interface, needs implementation

**Current Limitations:**
1. **No Factory Methods** - Unlike `AsynchronousHelloWorld.from()`, no factory methods exist
2. **No Default Implementation** - All methods are abstract, no `DefaultReactiveHelloWorld` class
3. **No Integration** - Interface doesn't connect to existing `HelloWorldFlow` implementations

**Recommended Improvements:**
1. Add factory methods: `ReactiveHelloWorld.from(HelloWorld, ExecutorService)`
2. Create `DefaultReactiveHelloWorld` that adapts `Flow.Publisher` to `org.reactivestreams.Publisher`
3. Add default implementations that bridge `HelloWorldFlow` to Reactive Streams interface
4. Consider adding adapter utilities to convert between `Flow.Publisher` and `org.reactivestreams.Publisher`

### HelloWorldFlow

**Status:** ✅ **Complete** - Full implementation with all components

**Strengths:**
- Complete Reactive Streams implementation
- Proper backpressure handling
- Multiple publisher/subscriber types
- Processor pattern demonstrated
- Well-tested with multiple reactive libraries

## Missing Components

1. **DefaultReactiveHelloWorld Implementation** - No concrete implementation of `ReactiveHelloWorld` interface
2. **Sink/Collector** - No built-in collector for accumulating results
3. **Error Recovery** - No retry/backoff mechanisms
4. **Buffering** - No buffering strategies for slow subscribers
5. **Timing** - No time-based operations (delay, timeout)
6. **Reactive Operations** - No map, filter, flatMap operations in the interface

## Best Practices Demonstrated

1. ✅ Proper backpressure implementation
2. ✅ Thread-safe demand accumulation
3. ✅ Resource cleanup on cancellation
4. ✅ Type-safe transformations
5. ✅ Integration with major reactive libraries

## Use Cases

- **Teaching Reactive Streams:** Complete implementation of spec via `HelloWorldFlow`
- **Library Integration:** Examples for Reactor, RxJava, etc.
- **Backpressure:** Demonstrates demand-driven flow
- **Transformation:** Shows processor pattern
- **Async Processing:** Non-blocking item production

## Design Assessment

### Current Approach Evaluation

**Strengths:**
- ✅ **Dual Approach:** Teaches both external library and Java built-in reactive programming
- ✅ Simple, minimal interface - easy to understand
- ✅ Three granularities (Byte, byte[], String) - good for teaching
- ✅ Uses standard Reactive Streams specification (via external library)
- ✅ `HelloWorldFlow` provides complete, working implementation (via Java built-in)
- ✅ **Educational Value:** Shows students both approaches to reactive programming

**Areas for Improvement:**
- ⚠️ Missing factory pattern: Should add `from()` methods like `AsynchronousHelloWorld`
- ⚠️ No default implementation: Should create `DefaultReactiveHelloWorld`
- ⚠️ No bridge: Missing adapter between `Flow.Publisher` and `org.reactivestreams.Publisher`

### Recommended Next Steps

1. **Add Factory Methods:** Implement `from()` static methods (consistent with `AsynchronousHelloWorld`)
2. **Create Default Implementation:** Build `DefaultReactiveHelloWorld` that:
   - Uses `HelloWorldFlow` internally (Java built-in)
   - Adapts `Flow.Publisher` to `org.reactivestreams.Publisher` for the interface
   - Bridges the two approaches
3. **Add Adapter Utilities:** Create utilities to convert between `Flow.Publisher` and `org.reactivestreams.Publisher`
4. **Add Default Methods:** Provide default implementations that delegate to `HelloWorldFlow`

**Note:** The type difference (`org.reactivestreams.Publisher` vs `Flow.Publisher`) is **intentional** - it teaches both approaches. The implementation should bridge them, not eliminate the difference.
