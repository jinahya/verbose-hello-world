# ReactiveHelloWorld 가이드

## 목차

1. [리액티브 프로그래밍이란?](#리액티브-프로그래밍이란)
2. [Reactive Streams 인터페이스란?](#reactive-streams-인터페이스란)
3. [ReactiveHelloWorldFactory란?](#reactivehelloworldfactory란)
4. [ReactiveHelloWorldFactory 구현체들](#reactivehelloworldfactory-구현체들)

---

## 리액티브 프로그래밍이란?

**리액티브 프로그래밍(Reactive Programming)**은 데이터 스트림과 변화의 전파에 초점을 맞춘 프로그래밍 패러다임입니다.

### 핵심 개념

리액티브 프로그래밍은 다음과 같은 특징을 가집니다:

- **비동기 처리**: 데이터를 비동기적으로 처리하여 블로킹을 최소화합니다.
- **이벤트 기반**: 데이터가 도착하면 이벤트로 처리합니다.
- **백프레셔(Backpressure)**: 소비자가 생산자의 속도를 제어할 수 있어 메모리 오버플로우를 방지합니다.
- **조합 가능성**: 여러 스트림을 조합하여 복잡한 데이터 파이프라인을 구성할 수 있습니다.

### 전통적인 방식과의 비교

| 특징         | 전통적 방식    | 리액티브 방식      |
|------------|-----------|--------------|
| **데이터 처리** | 동기적, 순차적  | 비동기적, 스트림 기반 |
| **메모리 관리** | 버퍼링 필요    | 백프레셔로 자동 제어  |
| **에러 처리**  | try-catch | onError 콜백   |
| **조합**     | 중첩된 콜백    | 연산자 체이닝      |
| **다중 아이템** | 반복문       | 스트림 처리       |

### 왜 리액티브 프로그래밍인가?

1. **확장성**: 대량의 데이터를 효율적으로 처리
2. **반응성**: 빠른 응답 시간과 높은 처리량
3. **탄력성**: 시스템 장애에 대한 복원력
4. **메시지 기반**: 느슨한 결합과 명확한 경계

---

## Reactive Streams 인터페이스란?

**Reactive Streams**는 비동기 스트림 처리와 백프레셔를 위한 표준 명세입니다. Java에서는 `org.reactivestreams` 패키지로 제공됩니다.

### 핵심 인터페이스

#### 1. `Publisher<T>`

**역할**: 데이터를 발행(publish)하는 생산자

**메서드**:

```java
void subscribe(Subscriber<? super T> s)
```

**특징**:

- 0개 이상의 데이터를 비동기적으로 발행
- 여러 구독자를 가질 수 있음
- 구독자가 데이터를 요청할 때까지 발행하지 않음 (백프레셔)

#### 2. `Subscriber<T>`

**역할**: 데이터를 구독(subscribe)하는 소비자

**메서드**:

```java
void onSubscribe(Subscription s)    // 구독 시작 시 호출

void onNext(T t)                    // 데이터 수신 시 호출

void onError(Throwable t)          // 에러 발생 시 호출

void onComplete()                   // 스트림 완료 시 호출
```

**호출 순서**:

1. `onSubscribe()` - 반드시 첫 번째로 호출
2. `onNext()` - 0회 이상 호출 가능
3. `onComplete()` 또는 `onError()` - 마지막에 한 번만 호출

#### 3. `Subscription`

**역할**: Publisher와 Subscriber 간의 연결을 관리

**메서드**:

```java
void request(long n)    // n개의 데이터를 요청 (백프레셔 제어)

void cancel()           // 구독 취소
```

**특징**:

- `request(n)`을 통해 소비자가 생산 속도를 제어
- `n`은 양수여야 하며, `Long.MAX_VALUE`는 무제한 요청
- `cancel()`은 멱등성(idempotent)을 가져야 함

#### 4. `Processor<T, R>`

**역할**: Publisher이면서 동시에 Subscriber인 변환기

**특징**:

- 데이터 변환 파이프라인 구성에 사용
- `Publisher<R>`와 `Subscriber<T>`를 모두 구현

### Reactive Streams 규칙

Reactive Streams 명세는 엄격한 규칙을 정의합니다:

1. **Rule 1.1**: `onSubscribe`는 정확히 한 번만 호출되어야 합니다.
2. **Rule 1.2**: `onSubscribe`의 인자는 null이 아니어야 합니다.
3. **Rule 1.3**: `onSubscribe` 이전에 다른 콜백이 호출되어서는 안 됩니다.
4. **Rule 1.6**: 종료(`onComplete` 또는 `onError`) 후 `request()`나 `cancel()` 호출은 무시됩니다.
5. **Rule 3.9**: 0 이하의 `request(n)` 값은 무시됩니다.
6. **Rule 3.10**: `cancel()`은 멱등성(idempotent)을 가져야 합니다.

### 백프레셔(Backpressure) 메커니즘

백프레셔는 소비자가 생산자의 속도를 제어하는 메커니즘입니다:

```
Publisher → [데이터 생성] → Subscriber
              ↑                    ↓
              └── request(n) ───────┘
```

- Subscriber가 `request(n)`을 호출하면 Publisher가 최대 n개의 데이터를 발행
- Subscriber가 요청하지 않으면 Publisher는 대기
- 이를 통해 메모리 오버플로우를 방지

---

## ReactiveHelloWorldFactory란?

`ReactiveHelloWorldFactory`는 리액티브 스트림을 사용하여 "hello, world" 데이터를 다양한 형태로 발행하는 인터페이스입니다.

### 위치

```
01-verbose-hello-world-api/src/main/java/com/github/jinahya/hello/api/ReactiveHelloWorldFactory.java
```

### 인터페이스 구조

```java
public interface ReactiveHelloWorldFactory {

    Publisher<Byte> newOctetPublisher();

    Publisher<byte[]> newArrayPublisher();

    Publisher<String> newStringPublisher();
}
```

### 주요 메서드

#### 1. `newOctetPublisher()`

```java
default Publisher<Byte> newOctetPublisher()
```

- **목적**: "hello, world" 문자열의 각 바이트를 개별적으로 발행
- **반환 타입**: `Publisher<Byte>`
- **데이터**: 12개의 바이트 (h, e, l, l, o, ,, 공백, w, o, r, l, d)
- **기본 구현**: `ReactiveHelloWorldFactoryDefaults.DefaultOctetPublisher`

#### 2. `newArrayPublisher()`

```java
default Publisher<byte[]> newArrayPublisher()
```

- **목적**: "hello, world" 문자열을 바이트 배열로 발행
- **반환 타입**: `Publisher<byte[]>`
- **데이터**: `byte[12]` 배열
- **기본 구현**:
    - `newOctetPublisher()`를 구독하여 바이트를 수집
    - 12바이트를 모두 수집한 후 배열로 발행
    - 여러 배열을 요청하면 각각 새로운 구독 생성

#### 3. `newStringPublisher()`

```java
default Publisher<String> newStringPublisher()
```

- **목적**: "hello, world" 문자열을 String으로 발행
- **반환 타입**: `Publisher<String>`
- **데이터**: `"hello, world"` 문자열
- **기본 구현**:
    - `newArrayPublisher()`를 구독하여 바이트 배열을 수신
    - 바이트 배열을 US_ASCII 인코딩으로 문자열로 변환

### 리액티브 체이닝

각 메서드는 이전 메서드를 활용하여 파이프라인을 구성합니다:

```
newOctetPublisher() 
    ↓
newArrayPublisher() (바이트를 수집하여 배열로 변환)
    ↓
newStringPublisher() (배열을 문자열로 변환)
```

### 기본 구현: ReactiveHelloWorldFactoryDefaults

**위치**: `src/main/java/com/github/jinahya/hello/api/ReactiveHelloWorldFactoryDefaults.java`

교육 목적으로 단순하게 작성된 구현:

- **DefaultOctetPublisher**: 각 바이트를 순차적으로 발행
- **ArraySubscription**: 바이트를 수집하여 배열로 변환하는 구독 관리
- **StringSubscription**: 바이트 배열을 문자열로 변환하는 구독 관리

**특징**:

- 외부 라이브러리 의존성 없음
- Reactive Streams 명세 직접 구현
- `AtomicLong`을 사용한 수요(demand) 추적
- volatile 플래그를 사용한 취소 처리

---

## ReactiveHelloWorldFactory 구현체들

이 프로젝트는 여러 리액티브 스트림 라이브러리를 사용한 구현체를 제공합니다:

### 1. ReactiveHelloWorldFactoryDefaults (기본 구현)

**위치**: `src/main/java/com/github/jinahya/hello/api/ReactiveHelloWorldFactoryDefaults.java`

**특징**:

- ✅ 외부 라이브러리 의존성 없음
- ✅ Reactive Streams 명세를 직접 구현
- ✅ 교육 목적으로 단순하게 작성
- ✅ 백프레셔 처리 포함

**구현 방식**:

- `AbstractSubscription`을 상속하여 구독 관리
- `AtomicLong`을 사용한 수요(demand) 추적
- volatile 플래그를 사용한 취소 처리

### 2. ReactorReactiveHelloWorldFactory (Project Reactor)

**위치**: `src/test/java/com/github/jinahya/hello/api/ReactorReactiveHelloWorldFactory.java`

**테스트 클래스**: `ReactorReactiveHelloWorldFactoryTest`

**라이브러리**: Project Reactor (`reactor-core` 3.8.1)

**특징**:

- **Flux**: 0개 이상의 데이터를 발행하는 리액티브 타입
- **Mono**: 0개 또는 1개의 데이터를 발행하는 리액티브 타입
- Spring WebFlux의 기반 라이브러리
- 풍부한 연산자 제공 (map, filter, flatMap, collectList 등)

**구현 방식**:

```java
// 바이트 발행
Flux.fromStream(IntStream.range(0, array.length).

mapToObj(i ->array[i]))
        .

publishOn(scheduler)
    .

doOnNext(b ->log.

debug("publishing byte: 0x{}('{}')",...))

// 배열 발행 (Flux.create 사용)
        Flux.

create(sink ->{
        sink.

onRequest(n ->{
        // 백프레셔 처리
        Flux.

from(newOctetPublisher())
        .

collectList()
            .

map(ReactiveHelloWorldFactoryUtils::toByteArray)
            .

subscribe(array ->{
        sink.

next(array);
// 완료 처리
            });
                    });
                    })
```

**장점**:

- `Flux.create()`의 `sink.onRequest()`로 백프레셔 직접 제어 가능
- `Scheduler`를 통한 스레드 관리
- 강력한 연산자 체이닝
- Spring 생태계와 완벽한 통합

**사용 예시**:

```java
ReactorReactiveHelloWorldFactory factory =
        new ReactorReactiveHelloWorldFactory(service, Schedulers.immediate());

Publisher<Byte> bytes = factory.newOctetPublisher();
```

### 3. RxJava3ReactiveHelloWorldFactory (RxJava 3)

**위치**: `src/test/java/com/github/jinahya/hello/api/RxJava3ReactiveHelloWorldFactory.java`

**테스트 클래스**: `RxJava3ReactiveHelloWorldFactoryTest`

**라이브러리**: RxJava 3 (`rxjava` 3.1.12)

**특징**:

- **Flowable**: Reactive Streams를 준수하는 0개 이상의 데이터 스트림
- **Observable**: Reactive Streams를 준수하지 않는 스트림 (사용하지 않음)
- **Single**: 1개의 데이터만 발행
- **Maybe**: 0개 또는 1개의 데이터 발행

**구현 방식**:

```java
// 바이트 발행
Flowable.fromStream(IntStream.range(0, array.length).

mapToObj(i ->array[i]))
        .

observeOn(scheduler)
    .

doOnNext(...)

// 배열 발행 (커스텀 Publisher 구현)
// Flowable.create()는 setRequestHandler()를 지원하지 않으므로
// 커스텀 Publisher를 구현하여 백프레셔 처리
Flowable.

fromPublisher(new Publisher<byte[]>() {
    @Override
    public void subscribe (Subscriber < ? super byte[]>subscriber){
        subscriber.onSubscribe(new Subscription() {
            @Override
            public void request(long n) {
                // 백프레셔 처리
                Flowable.fromPublisher(newOctetPublisher())
                        .toList()
                        .map(ReactiveHelloWorldFactoryUtils::toByteArray)
                        .subscribe(...)
            }
        });
    }
})
```

**장점**:

- Reactive Streams 명세 완전 준수
- 다양한 연산자 제공
- 널리 사용되는 라이브러리
- Android 개발에서도 사용

**주의사항**:

- `Flowable.create()`는 `BackpressureStrategy`를 사용하지만 `setRequestHandler()`를 지원하지 않음
- 따라서 커스텀 `Publisher`를 구현하여 백프레셔를 직접 처리해야 함

**사용 예시**:

```java
RxJava3ReactiveHelloWorldFactory factory =
        new RxJava3ReactiveHelloWorldFactory(service, Schedulers.trampoline());

Publisher<Byte> bytes = factory.newOctetPublisher();
```

### 4. MutinyReactiveHelloWorldFactory (SmallRye Mutiny)

**위치**: `src/test/java/com/github/jinahya/hello/api/MutinyReactiveHelloWorldFactory.java`

**테스트 클래스**: `MutinyReactiveHelloWorldFactoryTest`

**라이브러리**: SmallRye Mutiny (`mutiny` 3.1.0)

**특징**:

- **Multi**: 0개 이상의 데이터를 발행 (Reactive Streams `Publisher`와 유사)
- **Uni**: 0개 또는 1개의 데이터를 발행
- Quarkus 프레임워크의 기본 리액티브 라이브러리
- Java 9+ `java.util.concurrent.Flow` API 기반

**구현 방식**:

```java
// 바이트 발행
Multi.createFrom().

items(
        IntStream.range(0, array.length).

mapToObj(i ->array[i]).

toArray(Byte[]::new)
)
        .

invoke(...)
.

emitOn(Infrastructure.getDefaultExecutor())

// Flow.Publisher를 org.reactivestreams.Publisher로 변환
// Multi는 Flow.Publisher를 구현하므로 어댑터 필요
        new Publisher<Byte>(){

public void subscribe(Subscriber<? super Byte> subscriber) {
    multi.subscribe(new Flow.Subscriber<Byte>() {
        // 어댑터 구현
    });
}
}
```

**장점**:

- Quarkus와 완벽한 통합
- 직관적인 API (`createFrom().items()`, `invoke()`, `emitOn()`)
- `emitOn()`으로 비동기 실행 제어
- Java 표준 `Flow` API 기반

**주의사항**:

- Mutiny 3는 `java.util.concurrent.Flow.Publisher`를 사용
- `org.reactivestreams.Publisher`로 변환하기 위해 어댑터 필요
- `runSubscriptionOn()` 대신 `emitOn()` 사용 (구독은 동기적으로 처리)

**사용 예시**:

```java
MutinyReactiveHelloWorldFactory factory =
        new MutinyReactiveHelloWorldFactory(service);

Publisher<Byte> bytes = factory.newOctetPublisher();
```

### 5. VertxReactiveHelloWorldFactory (Eclipse Vert.x)

**위치**: `src/test/java/com/github/jinahya/hello/api/VertxReactiveHelloWorldFactory.java`

**테스트 클래스**: `VertxReactiveHelloWorldFactoryTest`

**라이브러리**: Eclipse Vert.x (`vertx-reactive-streams` 5.0.6)

**특징**:
- **ReactiveWriteStream**: WriteStream과 Publisher를 모두 구현하는 인터페이스
- **ReadStream/WriteStream**: Vert.x의 네이티브 스트림 API
- 이벤트 루프 기반 비동기 처리
- Netty 기반의 고성능 네트워크 처리

**구현 방식**:
```java
// 바이트 발행 (커스텀 Publisher 구현)
// ReactiveWriteStream은 모든 데이터를 즉시 발행하므로
// 백프레셔를 제어하기 위해 커스텀 Publisher 사용
new Publisher<Byte>() {
    @Override
    public void subscribe(Subscriber<? super Byte> subscriber) {
        // Long.MAX_VALUE 처리 (int로 캐스팅 시 오버플로우 방지)
        final var requestCount = n == Long.MAX_VALUE 
            ? array.length - currentIndex 
            : (int) Math.min(n, array.length - currentIndex);
        // 동기적으로 바이트 발행
    }
}

// 배열/문자열 발행은 다른 구현체와 동일한 패턴 사용
```

**장점**:
- 이벤트 루프 기반의 효율적인 비동기 처리
- Netty 기반의 높은 성능
- HTTP, TCP 등 네트워크 프로토콜과의 통합 용이
- 독립적인 리액티브 프레임워크

**주의사항**:
- `Vertx` 인스턴스 필요 (리소스 관리 필요)
- `Long.MAX_VALUE`를 `int`로 캐스팅 시 오버플로우 발생 가능 (특별 처리 필요)
- 테스트 후 `Vertx.close()` 필요

**사용 예시**:
```java
Vertx vertx = Vertx.vertx();
VertxReactiveHelloWorldFactory factory = 
    new VertxReactiveHelloWorldFactory(service, vertx);
Publisher<Byte> bytes = factory.newOctetPublisher();
// 사용 후
vertx.close();
```

### 6. AkkaReactiveHelloWorldFactory (Akka Streams)

**위치**: `src/test/java/com/github/jinahya/hello/api/AkkaReactiveHelloWorldFactory.java`

**테스트 클래스**: `AkkaReactiveHelloWorldFactoryTest`

**라이브러리**: Akka Streams (`akka-stream_3` 2.8.8)

**특징**:

- **Source**: 데이터 소스 (Publisher와 유사)
- **Sink**: 데이터 목적지 (Subscriber와 유사)
- **Flow**: 변환 연산자
- 액터 기반 리액티브 스트림 처리
- 강력한 백프레셔 처리

**구현 방식**:

```java
// 바이트 발행
Source.from(IntStream.range(0, array.length).

mapToObj(i ->array[i]).

toList())
        .

map(...)
    .

runWith(Sink.asPublisher(AsPublisher.WITHOUT_FANOUT), 
             Materializer.

matFromSystem(actorSystem))

// 배열 발행 (커스텀 Publisher 구현)
// Source를 Publisher로 변환하거나 커스텀 Publisher 사용
        new Publisher<byte[]>(){
        // Subscription 구현
        }
```

**장점**:

- 액터 모델 기반의 강력한 동시성 처리
- 자동 백프레셔 처리
- 복잡한 스트림 그래프 구성 가능
- 높은 처리량과 낮은 지연시간

**주의사항**:

- `ActorSystem`이 필요 (리소스 관리 필요)
- `Materializer`를 통한 스트림 실행
- `Sink.asPublisher()`로 Publisher 변환
- 테스트 후 `ActorSystem` 종료 필요

**사용 예시**:

```java
ActorSystem actorSystem = ActorSystem.create("test-system");

AkkaReactiveHelloWorldFactory factory =
        new AkkaReactiveHelloWorldFactory(service, actorSystem);

Publisher<Byte> bytes = factory.newOctetPublisher();
// 사용 후
actorSystem.

terminate();
```

### 구현체 비교 요약

| 구현체          | 라이브러리           | 주요 타입            | 백프레셔 처리         | 특징                      |
|--------------|-----------------|------------------|-----------------|-------------------------|
| **Defaults** | 없음 (직접 구현)      | `Publisher`      | 커스텀 구현          | 교육용, 단순함                |
| **Reactor**  | Project Reactor | `Flux`, `Mono`   | `Flux.create()` | Spring 통합, 강력한 연산자      |
| **RxJava 3** | RxJava 3        | `Flowable`       | 커스텀 `Publisher` | 널리 사용, 다양한 연산자          |
| **Mutiny**   | SmallRye Mutiny | `Multi`, `Uni`   | 커스텀 `Publisher` | Quarkus 통합, Flow API 기반 |
| **Vert.x**   | Eclipse Vert.x  | `ReactiveWriteStream` | 커스텀 `Publisher` | 이벤트 루프 기반, Netty 통합 |
| **Akka**     | Akka Streams    | `Source`, `Sink` | 자동 처리           | 액터 기반, 높은 성능            |

### 공통 패턴

모든 구현체는 다음 패턴을 따릅니다:

1. **바이트 발행**: 배열에서 스트림으로 변환하여 각 바이트 발행
2. **배열 발행**: 바이트 퍼블리셔를 구독하여 수집 후 배열로 변환
3. **문자열 발행**: 배열 퍼블리셔를 구독하여 문자열로 변환

각 구현체는 라이브러리의 고유한 API를 사용하지만, 최종적으로는 `org.reactivestreams.Publisher`를 반환하여 표준 인터페이스를 준수합니다.

---

## 아직 구현되지 않은 Reactive Streams 구현체들

다음은 아직 구현되지 않았고, 다른 구현체에 의존하지 않는 독립적인 Reactive Streams 구현체들입니다:

### 1. Eclipse Vert.x (`vertx-reactive-streams`)

**상태**: ✅ 구현 완료

**라이브러리**: `io.vertx:vertx-reactive-streams` 5.0.6

**구현체**: `VertxReactiveHelloWorldFactory`

**특징**:
- **ReadStream**: 데이터를 읽는 스트림
- **WriteStream**: 데이터를 쓰는 스트림
- **ReactiveWriteStream**: WriteStream과 Publisher를 모두 구현
- 이벤트 루프 기반 비동기 처리
- Netty 기반의 고성능 네트워크 처리

### 2. RxJava 2 (`rxjava2`)

**상태**: 구현되지 않음 (RxJava 3은 이미 구현됨)

**라이브러리**: `io.reactivex.rxjava2:rxjava` 2.x

**특징**:
- RxJava 3의 이전 버전
- Reactive Streams 지원 (`Flowable`)
- 레거시 프로젝트에서 여전히 사용

**구현 필요성**: 
- 교육적 목적 (버전 비교)
- 레거시 시스템 이해

**참고**: RxJava 3이 이미 구현되어 있어 우선순위 낮음

### 3. Java 9+ Flow API

**상태**: 별도 구현 (`HelloWorldFlow`로 존재)

**특징**:
- Java 표준 라이브러리 (`java.util.concurrent.Flow`)
- 외부 의존성 없음
- Reactive Streams와 의미적으로 동일

**참고**: 
- `HelloWorldFlow`로 이미 구현되어 있음
- `ReactiveHelloWorldFactory`와는 다른 API 사용
- 어댑터를 통해 연결 가능

### 제외된 구현체들 (Transitive)

다음 구현체들은 이미 구현된 라이브러리 위에 구축되어 있어 제외됩니다:

- **Spring WebFlux**: Project Reactor 기반 (Reactor는 이미 구현됨)
- **RSocket**: 내부적으로 Reactor 사용 가능
- **Reactor Netty**: Reactor 기반
- **Reactor Kafka**: Reactor 기반

### 구현 완료

1. **Eclipse Vert.x** ✅
   - 독립적인 구현
   - 의존성 이미 존재
   - 이벤트 루프 모델 학습 가치
   - 모든 테스트 통과

2. **RxJava 2** (낮음)
   - RxJava 3과 유사
   - 교육적 가치만 있음

---

## 요약

이 프로젝트는 리액티브 프로그래밍을 학습하기 위한 종합적인 예제를 제공합니다:

- ✅ **표준 명세**: Reactive Streams 명세 준수
- ✅ **다양한 구현**: 6개의 주요 리액티브 라이브러리 예제
  - ReactiveHelloWorldFactoryDefaults (기본 구현)
  - Project Reactor
  - RxJava 3
  - SmallRye Mutiny
  - Eclipse Vert.x
  - Akka Streams
- ✅ **실전 예제**: "hello, world"를 통한 간단하고 이해하기 쉬운 예제
- ✅ **교육 자료**: 상세한 주석과 문서

각 구현체는 동일한 인터페이스를 구현하지만, 각 라이브러리의 고유한 특성과 API를 보여줍니다. 이를 통해 리액티브 프로그래밍의 핵심 개념과 다양한 라이브러리 사용법을 학습할 수 있습니다.

### 향후 추가 가능한 구현체

- **RxJava 2**: 레거시 버전 (교육적 목적)
