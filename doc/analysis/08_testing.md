# 08. Testing Strategy

## Overview

The project employs a comprehensive testing strategy with multiple testing frameworks and approaches, particularly focusing on testing across different DI frameworks.

## Testing Frameworks

| Framework | Version | Purpose |
|-----------|---------|---------|
| JUnit 5 (Jupiter) | 5.12.x | Primary testing framework |
| Mockito | 5.21.0 | Mocking and stubbing |
| Awaitility | 4.3.0 | Async testing utilities |
| AssertJ | via JUnit | Fluent assertions |

## Test Structure

### API Module Tests

```
01-verbose-hello-world-api/
└── src/test/java/
    └── com/github/jinahya/hello/
        ├── api/
        │   ├── HelloWorldTest.java
        │   ├── HelloWorldFlowTest.java
        │   └── ...
        └── util/
            ├── HelloWorldUtilsTest.java
            └── ...
```

### LIB Module Tests

The library module has multiple test source directories for different DI frameworks:

```
02-verbose-hello-world-lib/
└── src/test/
    ├── java/                              # Core tests
    ├── java-cdi-se-openwebbeans/          # OpenWebBeans tests
    ├── java-cdi-se-openwebbeans-junit5/   # OpenWebBeans + JUnit5
    ├── java-cdi-se-weld/                  # Weld tests
    ├── java-cdi-se-weld-junit5/           # Weld + JUnit5
    ├── java-di-dagger/                    # Dagger tests
    ├── java-di-guice/                     # Guice tests
    ├── java-di-hk2/                       # HK2 tests
    └── java-di-spring/                    # Spring tests
```

## Testing by Framework

### Core JUnit 5 Tests

```java
class HelloWorldImplTest {

    @Test
    void set_ShouldPopulateArray_WhenCalled() {
        // Given
        HelloWorld helloWorld = new HelloWorldImpl();
        byte[] array = new byte[12];

        // When
        byte[] result = helloWorld.set(array);

        // Then
        assertArrayEquals(HelloWorld.BYTES, result);
        assertSame(array, result);
    }
}
```

### Mockito Tests

```java
@ExtendWith(MockitoExtension.class)
class HelloWorldMockTest {

    @Mock
    private OutputStream outputStream;

    @InjectMocks
    private HelloWorldImpl helloWorld;

    @Test
    void write_ShouldWriteBytes_ToOutputStream() throws IOException {
        // When
        helloWorld.write(outputStream);

        // Then
        verify(outputStream).write(any(byte[].class));
    }
}
```

### Guice Tests

```java
class HelloWorldGuiceTest {

    private Injector injector;

    @BeforeEach
    void setUp() {
        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(HelloWorld.class).to(HelloWorldImpl.class);
            }
        });
    }

    @Test
    void injection_ShouldProvide_HelloWorldInstance() {
        HelloWorld helloWorld = injector.getInstance(HelloWorld.class);

        assertNotNull(helloWorld);
        assertInstanceOf(HelloWorldImpl.class, helloWorld);
    }
}
```

### CDI/Weld Tests

```java
@EnableWeld
class HelloWorldWeldTest {

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.of(HelloWorldImpl.class);

    @Inject
    HelloWorld helloWorld;

    @Test
    void injection_ShouldWork_WithWeld() {
        assertNotNull(helloWorld);

        byte[] result = helloWorld.set(new byte[12]);
        assertArrayEquals(HelloWorld.BYTES, result);
    }
}
```

### Spring Tests

```java
@SpringBootTest
class HelloWorldSpringTest {

    @Autowired
    HelloWorld helloWorld;

    @Test
    void injection_ShouldWork_WithSpring() {
        assertNotNull(helloWorld);

        byte[] result = helloWorld.set(new byte[12]);
        assertArrayEquals(HelloWorld.BYTES, result);
    }
}
```

### Dagger Tests

```java
class HelloWorldDaggerTest {

    @Component
    interface HelloWorldComponent {
        HelloWorld helloWorld();
    }

    @Test
    void injection_ShouldWork_WithDagger() {
        HelloWorldComponent component = DaggerHelloWorldDaggerTest_HelloWorldComponent.create();
        HelloWorld helloWorld = component.helloWorld();

        assertNotNull(helloWorld);
    }
}
```

## Async Testing with Awaitility

```java
class AsyncHelloWorldTest {

    @Test
    void asyncWrite_ShouldComplete_Eventually() {
        AsynchronousHelloWorld asyncHw = new DefaultAsynchronousHelloWorld(
            new HelloWorldImpl(),
            Executors.newSingleThreadExecutor()
        );

        CompletableFuture<byte[]> future = asyncHw.setAsync(new byte[12]);

        await()
            .atMost(Duration.ofSeconds(5))
            .until(future::isDone);

        assertArrayEquals(HelloWorld.BYTES, future.join());
    }
}
```

## Reactive Testing

### Project Reactor Tests

```java
class HelloWorldReactorTest {

    @Test
    void reactiveFlow_ShouldEmit_HelloWorld() {
        HelloWorld helloWorld = new HelloWorldImpl();

        Mono<byte[]> mono = Mono.fromSupplier(() ->
            helloWorld.set(new byte[12])
        );

        StepVerifier.create(mono)
            .expectNextMatches(bytes ->
                Arrays.equals(HelloWorld.BYTES, bytes)
            )
            .verifyComplete();
    }
}
```

### RxJava Tests

```java
class HelloWorldRxJavaTest {

    @Test
    void observable_ShouldEmit_HelloWorld() {
        HelloWorld helloWorld = new HelloWorldImpl();

        Observable<byte[]> observable = Observable.fromCallable(() ->
            helloWorld.set(new byte[12])
        );

        observable.test()
            .assertValue(bytes ->
                Arrays.equals(HelloWorld.BYTES, bytes)
            )
            .assertComplete();
    }
}
```

## Maven Profile Activation

Tests for specific frameworks are enabled via Maven profiles:

```bash
# Run with Weld CDI tests
./mvnw test -Pcdi-se-weld

# Run with Guice tests
./mvnw test -Pdi-guice

# Run with Spring tests
./mvnw test -Pdi-spring

# Run with Dagger tests
./mvnw test -Pdi-dagger
```

## Test Configuration

### Surefire Plugin

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <includes>
            <include>**/*Test.java</include>
            <include>**/*Tests.java</include>
        </includes>
    </configuration>
</plugin>
```

### Build Helper Plugin

Adds additional test source directories:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>build-helper-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>add-test-source</id>
            <phase>generate-test-sources</phase>
            <goals>
                <goal>add-test-source</goal>
            </goals>
            <configuration>
                <sources>
                    <source>src/test/java-cdi-se-weld</source>
                </sources>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## Code Coverage

### JaCoCo Configuration

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.14</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Coverage Report

```bash
./mvnw test jacoco:report
# Reports available at target/site/jacoco/index.html
```

## Testing Best Practices Used

1. **Arrange-Act-Assert** pattern in all tests
2. **Descriptive test names** using `methodName_ShouldExpectedBehavior_WhenCondition`
3. **Isolated tests** - no shared mutable state
4. **Framework-specific test directories** for clean separation
5. **Profile-based activation** for optional framework tests
6. **Async testing** with proper timeouts and Awaitility

---
[Back to Index](00_index.md) | [Previous: Design Patterns](07_design_patterns.md) | [Next: Build System](09_build_system.md)
