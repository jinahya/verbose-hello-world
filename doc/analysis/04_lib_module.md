# 04. Library Module Analysis

## Module Information

| Attribute | Value |
|-----------|-------|
| **Artifact ID** | 02-verbose-hello-world-lib |
| **Packaging** | jar |
| **Main Classes** | 6 |
| **Test Classes** | 20+ |
| **Test Profiles** | 8 (various DI frameworks) |

## Implementation Classes

### HelloWorldImpl

The primary implementation of the `HelloWorld` interface.

```java
public class HelloWorldImpl implements HelloWorld, HelloWorldServiceProvider {

    @Override
    public byte[] set(final byte[] array) {
        // Sets bytes using various indexing methods
        array[0x00] = 0x68;  // 'h' - hex index
        array[0b0001] = 0x65; // 'e' - binary index
        array[02] = 0x6C;     // 'l' - octal index
        // ... continues for all 12 bytes
        return array;
    }

    @Override
    public HelloWorld getInstance() {
        return this;  // SPI implementation
    }
}
```

**Notable Features**:
- Uses multiple number formats for array indices (educational)
- Implements both `HelloWorld` and `HelloWorldServiceProvider`
- Self-providing SPI pattern

### HelloWorldDemo

Demo implementation using `System.arraycopy`:

```java
public class HelloWorldDemo implements HelloWorld {
    @Override
    public byte[] set(final byte[] array) {
        System.arraycopy(HelloWorld.BYTES, 0, array, 0, HelloWorld.BYTES.length);
        return array;
    }
}
```

### HelloWorldWrap

Implementation using `ByteBuffer.wrap`:

```java
public class HelloWorldWrap extends HelloWorldImpl {
    @Override
    public byte[] set(final byte[] array) {
        ByteBuffer.wrap(array).put(HelloWorld.BYTES);
        return array;
    }
}
```

## Service Provider Configuration

### META-INF/services

```
# META-INF/services/com.github.jinahya.hello.api.spi.HelloWorldServiceProvider
com.github.jinahya.hello.lib.HelloWorldImpl
```

This enables `ServiceLoader` discovery:

```java
ServiceLoader.load(HelloWorldServiceProvider.class)
    .findFirst()
    .map(HelloWorldServiceProvider::getInstance)
    .orElseThrow();
```

## Utility Classes

### JavaLangObjectUtils

Provides utility methods for `java.lang.Object`:

```java
public final class JavaLangObjectUtils {
    public static <T> T requireNonNullElseGet(T obj, Supplier<? extends T> supplier);
    public static <T> T requireNonNullElse(T obj, T defaultObj);
}
```

## Dependency Injection Support

The library module is designed to work with multiple DI frameworks. Each framework has dedicated test sources:

### Test Source Directories

```
src/test/java                           # Core tests
src/test/java-cdi-se-openwebbeans      # OpenWebBeans tests
src/test/java-cdi-se-openwebbeans-junit5
src/test/java-cdi-se-weld              # Weld tests
src/test/java-cdi-se-weld-junit5
src/test/java-di-dagger                # Dagger tests
src/test/java-di-guice                 # Guice tests
src/test/java-di-hk2                   # HK2 tests
src/test/java-di-spring                # Spring tests
```

### DI Framework Profiles

| Profile | Framework | Description |
|---------|-----------|-------------|
| `cdi-se-openwebbeans` | Apache OpenWebBeans | CDI implementation |
| `cdi-se-weld` | Weld | JBoss CDI implementation |
| `di-dagger` | Google Dagger | Compile-time DI |
| `di-guice` | Google Guice | Runtime DI |
| `di-hk2` | GlassFish HK2 | JSR-330 DI |
| `di-spring` | Spring Framework | Spring DI |

### Profile Configuration Example

```xml
<profile>
    <id>cdi-se-weld</id>
    <dependencies>
        <dependency>
            <groupId>org.jboss.weld.se</groupId>
            <artifactId>weld-se-core</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>build-helper-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <id>add-test-source-cdi-se-weld</id>
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
        </plugins>
    </build>
</profile>
```

## Test Examples

### Guice Test

```java
public class HelloWorldGuiceTest {
    @Test
    void testWithGuice() {
        Injector injector = Guice.createInjector(new HelloWorldModule());
        HelloWorld helloWorld = injector.getInstance(HelloWorld.class);
        byte[] result = helloWorld.set(new byte[12]);
        assertArrayEquals(HelloWorld.BYTES, result);
    }
}
```

### CDI/Weld Test

```java
@EnableWeld
public class HelloWorldWeldTest {
    @Inject
    HelloWorld helloWorld;

    @Test
    void testWithWeld() {
        byte[] result = helloWorld.set(new byte[12]);
        assertArrayEquals(HelloWorld.BYTES, result);
    }
}
```

### Spring Test

```java
@SpringBootTest
public class HelloWorldSpringTest {
    @Autowired
    HelloWorld helloWorld;

    @Test
    void testWithSpring() {
        byte[] result = helloWorld.set(new byte[12]);
        assertArrayEquals(HelloWorld.BYTES, result);
    }
}
```

## Dependencies

### Compile Dependencies

| Dependency | Scope | Purpose |
|------------|-------|---------|
| verbose-hello-world-api | compile | API interfaces |
| jakarta.inject-api | provided | DI annotations |

### Test Dependencies

| Dependency | Scope | Purpose |
|------------|-------|---------|
| JUnit 5 | test | Testing framework |
| Mockito | test | Mocking |
| Awaitility | test | Async testing |
| Various DI frameworks | test | DI testing |

## Key Design Patterns

### 1. Strategy Pattern
Multiple implementations of `HelloWorld` interface provide different strategies.

### 2. Provider Pattern
`HelloWorldServiceProvider` follows the provider pattern for service discovery.

### 3. Decorator Pattern
`HelloWorldWrap` extends `HelloWorldImpl`, potentially adding/modifying behavior.

---
[Back to Index](00_index.md) | [Previous: API Module](03_api_module.md) | [Next: Application Modules](05_app_modules.md)
