# 10. Code Quality

## Overview

The project employs multiple code quality tools and practices to maintain high code standards.

## Static Analysis Tools

### 1. Error Prone (Google)

Error Prone is a static analysis tool that catches common Java mistakes at compile time.

**Configuration:**

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <compilerArgs>
            <arg>-XDcompilePolicy=simple</arg>
            <arg>-Xplugin:ErrorProne</arg>
        </compilerArgs>
        <annotationProcessorPaths>
            <path>
                <groupId>com.google.errorprone</groupId>
                <artifactId>error_prone_core</artifactId>
                <version>${version.error_prone}</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

**Catches:**
- Dead code
- Null pointer issues
- Incorrect API usage
- Performance problems

### 2. NullAway (Uber)

NullAway provides compile-time null safety checking.

**Configuration:**

```xml
<annotationProcessorPaths>
    <path>
        <groupId>com.uber.nullaway</groupId>
        <artifactId>nullaway</artifactId>
        <version>${version.nullaway}</version>
    </path>
</annotationProcessorPaths>
```

**Combined with JSpecify:**

```java
@NullMarked
package com.github.jinahya.hello.api;

import org.jspecify.annotations.NullMarked;
```

### 3. SpotBugs

SpotBugs (successor to FindBugs) analyzes bytecode for potential bugs.

**Configuration:**

```xml
<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
    <version>4.9.8.2</version>
    <configuration>
        <effort>Max</effort>
        <threshold>Low</threshold>
    </configuration>
</plugin>
```

**Run:**
```bash
./mvnw spotbugs:check
./mvnw spotbugs:gui  # Visual interface
```

### 4. Compiler Warnings

All compiler warnings are enabled:

```xml
<compilerArgs>
    <arg>-Xlint:all</arg>
</compilerArgs>
```

## Code Coverage

### JaCoCo

**Configuration:**

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

**Generate Report:**
```bash
./mvnw test jacoco:report
# View: target/site/jacoco/index.html
```

## Continuous Integration

### GitHub Actions

```yaml
# .github/workflows/maven.yml
name: Java CI with Maven

on:
  push:
    branches: [ master ]
  pull_request:
    branches: [ master ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4
    - name: Set up JDK 21
      uses: actions/setup-java@v4
      with:
        java-version: '21'
        distribution: 'temurin'
    - name: Build with Maven
      run: ./mvnw -B verify
```

### SonarCloud Integration

The project integrates with SonarCloud for continuous code quality monitoring:

- Code smells detection
- Bug detection
- Vulnerability scanning
- Code duplication analysis
- Maintainability metrics

## Null Safety

### JSpecify Annotations

```java
// Package-level null marking
@NullMarked
package com.github.jinahya.hello.api;

// Class usage
public class HelloWorldImpl implements HelloWorld {
    @Override
    public byte[] set(byte[] array) {  // Non-null by default
        // ...
    }

    public @Nullable String getOptionalValue() {  // Explicitly nullable
        // ...
    }
}
```

### Annotation Types

| Annotation | Meaning |
|------------|---------|
| `@NullMarked` | Package/class is null-checked |
| `@Nullable` | Value can be null |
| `@NonNull` | Value cannot be null (default in @NullMarked) |

## Documentation Standards

### JavaDoc

All public APIs are documented:

```java
/**
 * Sets the "hello, world" bytes into the given array.
 *
 * @param array the array to populate; must have at least 12 elements
 * @return the same array, for chaining
 * @throws NullPointerException if array is null
 * @throws IllegalArgumentException if array.length < 12
 */
byte[] set(byte[] array);
```

### Generate JavaDoc:
```bash
./mvnw javadoc:javadoc
# View: target/site/apidocs/index.html
```

## Code Style

### Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Classes | PascalCase | `HelloWorldImpl` |
| Methods | camelCase | `set`, `writeToStream` |
| Constants | UPPER_SNAKE_CASE | `BYTES`, `HELLO_STRING` |
| Packages | lowercase | `com.github.jinahya.hello` |

### File Organization

```java
// 1. Package declaration
package com.github.jinahya.hello.api;

// 2. Imports (organized)
import java.io.*;
import java.nio.*;
import java.util.*;

// 3. Class JavaDoc
/**
 * Represents a hello-world message encoder.
 */
// 4. Class declaration
@FunctionalInterface
public interface HelloWorld {

    // 5. Constants
    byte[] BYTES = {...};

    // 6. Abstract methods
    byte[] set(byte[] array);

    // 7. Default methods
    default byte[] get() {...}
}
```

## Quality Metrics

### Target Metrics

| Metric | Target |
|--------|--------|
| Code Coverage | > 80% |
| Duplicated Lines | < 3% |
| Technical Debt | < 1 day |
| Bugs | 0 |
| Vulnerabilities | 0 |
| Code Smells | < 10 |

## Best Practices Applied

### 1. Immutability
```java
// Constants are effectively immutable
byte[] BYTES = {0x68, 0x65, 0x6C, ...};
```

### 2. Defensive Copying
```java
public byte[] getBytes() {
    return Arrays.copyOf(BYTES, BYTES.length);  // Return copy
}
```

### 3. Fail-Fast Validation
```java
public byte[] set(byte[] array) {
    Objects.requireNonNull(array, "array must not be null");
    if (array.length < BYTES.length) {
        throw new IllegalArgumentException("array too small");
    }
    // ...
}
```

### 4. Resource Management
```java
// Try-with-resources
try (OutputStream os = Files.newOutputStream(path)) {
    write(os);
}
```

### 5. Functional Style
```java
// Lambda-friendly interfaces
HelloWorld hw = array -> {
    System.arraycopy(BYTES, 0, array, 0, BYTES.length);
    return array;
};
```

## Running Quality Checks

```bash
# Full quality check
./mvnw clean verify

# Static analysis only
./mvnw spotbugs:check

# Coverage report
./mvnw test jacoco:report

# JavaDoc generation
./mvnw javadoc:javadoc

# All reports (site)
./mvnw site
```

---
[Back to Index](00_index.md) | [Previous: Build System](09_build_system.md)
