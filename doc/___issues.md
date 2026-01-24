# Project Issues and Concerns

> This document is intentionally excluded from the main index. It catalogs issues, potential bugs, and areas requiring attention.

## Issue Summary

| Severity | Count | Description |
|----------|-------|-------------|
| CRITICAL | 2 | Null returns, incomplete implementations |
| HIGH | 3 | Disabled null-checking, resource leaks, random security code |
| MEDIUM | 8 | Reflection issues, deprecated APIs, testing gaps |
| LOW | 10+ | Code smells, documentation, minor config issues |

---

## CRITICAL Issues

### 1. Incomplete Method Implementations

**File:** `01-verbose-hello-world-api/src/main/java/com/github/jinahya/hello/api/HelloWorld.java`

#### 1.1 `set(byte[] array)` Returns Null (Lines 171-180)

```java
default byte[] set(final byte[] array) {
    // ... validation commented out ...
    return null;  // BUG: Should call implementation
}
```

**Impact:** `NullPointerException` when callers use the returned value.
**Contract Violation:** JavaDoc states it returns the array.

#### 1.2 `append(Path path)` Is Stubbed (Lines 638-654)

```java
default <T extends Path> T append(final T path) throws IOException {
    // Implementation commented out
    return path;  // Does nothing!
}
```

**Impact:** Silent failure - file is not modified.

#### 1.3 Async `write()` Not Implemented (Lines 769-802)

```java
default <T extends AsynchronousByteChannel, A> void write(
        final T channel, final @Nullable A attachment,
        final CompletionHandler<Integer, ? super A> handler) {
    // Entire body commented out
}
```

**Impact:** Async write operations silently fail.

---

### 2. Random Behavior in Security Code

**File:** `01-verbose-hello-world-api/src/main/java/com/github/jinahya/hello/api/util/JavaSecurityMessageDigestUtils.java` (Line 64)

```java
if (ThreadLocalRandom.current().nextBoolean()) { // TODO: remove?
    // Different digest update behavior based on random
}
```

**Impact:** Non-deterministic message digest calculation.
**Security Risk:** Cryptographic operations should never be random.

---

## HIGH Severity Issues

### 3. Disabled Null-Safety Checking

**File:** `pom.xml` (Lines 431-432)

```xml
<!-- https://github.com/google/error-prone/issues/5354 -->
<!--<arg>-Xplugin:ErrorProne -Xep:NullAway:ERROR -XepOpt:NullAway:OnlyNullMarked=true</arg>-->
```

NullAway is disabled, allowing null-related bugs (like Issue #1) to pass through build.

**Recommendation:** Re-enable once upstream issue is resolved, or use alternative null-checking.

---

### 4. Resource Leak: Unclosed ExecutorService

**File:** `01-verbose-hello-world-api/src/main/java/com/github/jinahya/hello/api/AsynchronousHelloWorld.java` (Line 15)

```java
static AsynchronousHelloWorld from(final HelloWorld underlying) {
    return from(underlying, Executors.newVirtualThreadPerTaskExecutor());
}
```

**Problem:** Creates an `ExecutorService` with no lifecycle management.
**Impact:** Thread/memory leak - executor never shut down.
**Recommendation:** Return `AutoCloseable` or document caller responsibility.

---

### 5. Reflection Failure During Class Loading

**File:** `01-verbose-hello-world-api/src/main/java/com/github/jinahya/hello/api/util/LogbackUtils.java` (Lines 57-67)

```java
static {
    try {
        // Reflection to access Logback internals
    } catch (final ReflectiveOperationException roe) {
        throw new ExceptionInInitializerError(roe);
    }
}
```

**Impact:** If Logback not on classpath, class loading fails.
**Recommendation:** Graceful degradation with optional dependency handling.

---

## MEDIUM Severity Issues

### 6. Deprecated Methods Still in Use

**File:** `HelloWorld.java`

| Line | Method | Status |
|------|--------|--------|
| 597 | `send(SocketChannel)` | `@Deprecated(forRemoval = true)` |
| 714 | `send(AsynchronousSocketChannel)` | `@Deprecated(forRemoval = true)` |
| 823 | `send(AsynchronousSocketChannel, A, CompletionHandler)` | `@Deprecated(forRemoval = true)` |

**Recommendation:** Remove deprecated methods or document migration path.

---

### 7. Pre-release Dependency Versions

**File:** `pom.xml`

| Dependency | Version | Risk |
|------------|---------|------|
| Weld | `6.0.0.Alpha1` | Alpha - unstable |
| Jakarta EE | `11.0.0-M4` | Milestone - breaking changes possible |

**Recommendation:** Use stable releases for production code.

---

### 8. Loose Exception Handling

**File:** `JavaUtilConcurrentCallableUtils.java` (Line 50)

```java
try {
    return callable.call();
} catch (final Exception e) {
    throw new RuntimeException(e);
}
```

**Problem:** Catches generic `Exception`, wraps in `RuntimeException`.
**Recommendation:** Distinguish checked vs unchecked exceptions.

---

### 9. Testing Gaps

- Stubbed methods have tests that don't validate actual behavior
- Tests pass despite missing implementations
- Direct `System.out` usage instead of output capture
- `Future.get()` without timeout in async tests (potential deadlock)

---

### 10. Misleading JavaDoc

**File:** `HelloWorld.java` (Lines 165-166)

```java
/**
 * @implSpec Default implementation invokes {@code set(array, index)} method
 */
default byte[] set(final byte[] array) {
    return null;  // Actually returns null!
}
```

---

## LOW Severity Issues

### 11. Maven Property Typo

**File:** `pom.xml` (Line 55)

```xml
<maven.compiler.testRelase>${maven.compiler.testTarget}</maven.compiler.testRelase>
```

Should be `testRelease` (typo: "Relase").

---

### 12. Non-ASCII Class Names

**Files:**
- `屋上架屋.java` (roof above roof - code duplication marker)
- `畵蛇添足.java` (adding feet to snake - unnecessary code marker)
- `屋下架屋.java`

**Impact:** Limited IDE support, cross-platform issues, maintenance burden.
**Suppressed:** `@SuppressWarnings({"UnicodeInCode"})`

---

### 13. Mixed Number Literal Formats

**File:** `HelloWorldImpl.java` (Lines 52-63)

```java
array[index + 0x00] = 0x68;  // hex index
array[index + 0b1] = 0x65;   // binary index
array[index + 007] = 0x2C;   // octal index (with comment "// ?")
```

**Note:** Intentional for educational purposes but confusing.

---

### 14. Excessive @SuppressWarnings

50+ occurrences of `@SuppressWarnings` including:
- `"java:S101"` - Class naming conventions
- `"unchecked"` - Generic type safety
- `"java:S1481"` - Unused local variables
- `"UnicodeInCode"` - Non-ASCII identifiers

---

### 15. TODO/FIXME Comments

| File | Line | Comment |
|------|------|---------|
| `DefaultAsynchronousHelloWorld.java` | 44 | `// TODO: remove; just calls super implementation` |
| `JavaSecurityMessageDigestUtils.java` | 64 | `// TODO: remove?` |
| `HelloWorldFlow_14_Mutiny_MultiCreate_Test.java` | 233 | `// TODO: test!` |

---

### 16. Proxy Security Concern

**File:** `JavaLangReflectUtils.java` (Lines 14-30)

Generic proxy invocation handler without method validation.
**Risk:** Could invoke arbitrary methods on untrusted objects.

---

### 17. Performance: Reflection in Hot Path

**File:** `JavaLangReflectUtils.java`

Proxy handler performs reflection + Optional stream operations on every method call.

---

### 18. Unbounded Virtual Thread Executor

Multiple test files create `Executors.newVirtualThreadPerTaskExecutor()` without task limits.

---

## Recommendations Summary

| Priority | Action |
|----------|--------|
| 1 | Complete stubbed method implementations in `HelloWorld.java` |
| 2 | Remove random behavior from `JavaSecurityMessageDigestUtils` |
| 3 | Re-enable NullAway analysis when upstream issue fixed |
| 4 | Add ExecutorService lifecycle management |
| 5 | Remove deprecated methods marked for removal |
| 6 | Upgrade pre-release dependencies to stable versions |
| 7 | Address TODO comments or document deferral reasons |
| 8 | Consider ASCII-only class names for tooling compatibility |

---

## Issue Tracking

These issues should be tracked in GitHub Issues for proper resolution:
- https://github.com/jinahya/verbose-hello-world/issues

---
*Generated: 2026-01-24*
