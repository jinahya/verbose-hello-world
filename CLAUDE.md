# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## This is a standalone Java code project

This repository is a standalone Java code project. Treat it as one: edit `.java` sources, run `./mvnw test`, write javadoc in English, follow the conventions in this file. No asciidoc-book conventions, no Korean-prose rules, no "submodule is read-only" guard — those belong to other projects that may embed this one, not to this repository.

This CLAUDE.md is the project's own — the only ruleset that applies when working in this tree.

## Do not touch any `.git`-related stuff

Git is entirely the author's domain. Do not read, write, stage, commit, stash, branch, reset, restore, push, pull, fetch, merge, rebase, tag, or otherwise touch git state. Do not run mutating git commands (`git add` / `commit` / `stash` / `reset` / `checkout` / `branch` / `rm` / `mv` / `switch` / `restore` / `push` / `pull` / `fetch` / `merge` / `rebase` / `cherry-pick` / `tag` / `submodule update` / `config` / `clean`) and do not run read-only git commands (`git status` / `diff` / `log` / `rev-parse` / `show` / `ls-files` / `blame` / `rev-list` / `for-each-ref` / `remote` / `submodule status`). Do not touch `.git/` filesystem entries — no removing `.git/index.lock`, no editing `.git/HEAD`, nothing under `.git/`.

The working tree's dirty state is invisible. Modified files, untracked files, half-applied edits from prior sessions, stale lock files — none of it blocks work, none of it gets surfaced as a question. Proceed with the actual task as if the working tree were perfectly clean.

Single exception: the user types an explicit git command request in the current turn ("commit this with message X", "what does git status show"). Then run exactly that command and nothing more.

## Do not touch anything outside this CLAUDE.md's scope

The scope of this CLAUDE.md is this repository's working tree (the directory this file lives in, recursively). **Do not read, write, copy, move, delete, or otherwise touch any file outside that scope** — not parent directories, not sibling projects that may embed this one, not the host project's `.claude/` or `CLAUDE.md` or skills, not anything reachable via `../` or absolute paths above this tree.

This holds even when the cwd is somewhere above this directory and this tree appears as a sub-path. If a request would require touching files outside this scope, stop and surface the request to the user rather than reaching out. "While I'm here" cross-tree edits, "let me sync this from the parent" reflexes, and "I'll just copy that skill in" shortcuts are all forbidden by this rule.

## Project Overview

A verbose "hello, world" project whose sole purpose is to explore and learn about (almost) all Java I/O related APIs, using the simplest possible payload: the 12-byte string `"hello, world"`. The API module's `HelloWorld` interface provides default method implementations covering every major I/O pathway in the JDK (`java.io`, `java.nio`, `java.net`, async channels, reactive streams, etc.). The lib and app modules additionally demonstrate DI/CDI integration patterns (Dagger, Guice, HK2, Spring, OpenWebBeans, Weld).

## Commented-out code is intentional (book source)

This is the source code for a teaching book. `HelloWorld.java` and many test files deliberately ship with parts of method bodies commented out — each chapter progressively un-comments lines as the reader learns the corresponding API.

**Commented-out lines in `HelloWorld.java` method bodies are NOT bugs.** Do NOT:

- propose uncommenting them as "fixes"
- surface them in audits as "no-op" / "cascading bug" / "missing impl" / "double-feed"
- write contract tests that assume the commented body executes

Examples that have repeatedly tripped audits:
- `put(ByteBuffer)` L745–746 (`set(array, index);` and `buffer.position(buffer.position() + BYTES);` commented)
- `put(ByteBuffer)` L750 (`buffer.put(array);` commented)
- `write(OutputStream)` L291–292 (`set(array);` / `stream.write(array);` commented)
- `send(DatagramChannel)` / `send(DatagramChannel, SocketAddress)` non-blocking branches

If a commented-out fragment is itself broken (typo, wrong identifier, syntactic), the **fragment** may still be worth flagging — but the *fact that it is commented out* is never the bug.

When auditing or analyzing `HelloWorld.java`, treat **only the active (executed) code** as the specification. If you need to analyze "what happens when the reader uncomments these", say so explicitly and treat that as a separate, scoped task.

## Build Commands

```bash
# Full build (all modules)
./mvnw clean install

# Build without tests
./mvnw clean install -DskipTests

# Run tests for a specific module
./mvnw test -pl 01-verbose-hello-world-api
./mvnw test -pl 02-verbose-hello-world-lib

# Run tests with a specific DI/CDI profile (lib module only)
./mvnw test -Pcdi-se-openwebbeans -pl 02-verbose-hello-world-lib
./mvnw test -Pcdi-se-openwebbeans-junit5 -pl 02-verbose-hello-world-lib
./mvnw test -Pcdi-se-weld -pl 02-verbose-hello-world-lib
./mvnw test -Pcdi-se-weld-junit5 -pl 02-verbose-hello-world-lib
./mvnw test -Pdi-dagger -pl 02-verbose-hello-world-lib
./mvnw test -Pdi-guice -pl 02-verbose-hello-world-lib
./mvnw test -Phk2 -pl 02-verbose-hello-world-lib
./mvnw test -Pspring -pl 02-verbose-hello-world-lib

# Run a single test class
./mvnw test -pl 01-verbose-hello-world-api -Dtest=HelloWorld_01_Set_ArrayWithIndex_Test

# SonarCloud analysis
./mvnw install org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=jinahya_verbose-hello-world
```

## Build Requirements

- **Java 25** with preview features enabled (`--enable-preview` configured in compiler, surefire, and `.mvn/jvm.config`)
- **Maven 3.6.3+** (wrapper included: `./mvnw`)
- `.mvn/jvm.config` exports internal compiler modules for Error Prone annotation processing
- `.mvn/maven.config` enables ByteBuddy experimental mode (`-Dnet.bytebuddy.experimental=true`) required by Mockito

## Module Architecture

```
verbose-hello-world (root POM aggregator)
├── 01-verbose-hello-world-api    # HelloWorld interface + utilities
├── 02-verbose-hello-world-lib    # Implementations (HelloWorldImpl, HelloWorldDemo, HelloWorldWrap)
└── 03-verbose-hello-world-app    # Application modules (POM aggregator)
    ├── 01-...-app1               # Direct instantiation of HelloWorldImpl
    ├── 02-...-app2               # SPI via ServiceLoader
    ├── 03-...-app3               # DI (Guice)
    └── 04-...-app4               # CDI (Jakarta CDI)
```

### API Module (`01-verbose-hello-world-api`)

The core `HelloWorld` interface is a `@FunctionalInterface` whose single abstract method is `set(byte[], int)` — writing the 12-byte `"hello, world"` string. All other methods are `default` implementations covering diverse I/O patterns:

- `java.lang` — byte arrays, `Appendable`
- `java.lang.foreign` — `MemorySegment`
- `java.io` — streams, files, writers
- `java.net` — sockets, datagram sockets
- `java.nio` — `ByteBuffer`, channels (sync + async), `CompletionHandler`
- `java.security` / `javax.crypto` — `MessageDigest`, `Signature`, `Cipher`, `Mac`
- Reactive Streams — via `HelloWorldFlow` and `ReactiveHelloWorldFactory`

Also contains utility classes under subpackages for reflection, NIO, concurrency, security, and logging.

### Lib Module (`02-verbose-hello-world-lib`)

Provides concrete implementations. Test sources are split into **separate directories per DI framework**, activated by Maven profiles. Each profile uses `build-helper-maven-plugin` to add its test source tree:

- `src/test/java-cdi-se-openwebbeans/`
- `src/test/java-cdi-se-weld/`
- `src/test/java-di-dagger/`
- `src/test/java-di-guice/`
- `src/test/java-di-hk2/`
- `src/test/java-di-spring/`
- (plus `-junit5` variants for CDI)

### App Module (`03-verbose-hello-world-app`)

The `generate-executables` profile (active by default) produces multiple artifact formats: fat JAR (assembly), shaded JAR, Spring Boot JAR, and compressed archives.

## Testing

- **JUnit 5** (Jupiter) with **Mockito** for mocking and **Awaitility** for async assertions
- Coverage via **JaCoCo**
- The default `./mvnw test` runs common tests; DI/CDI-specific tests require activating the corresponding Maven profile (see Build Commands above)
- CI runs all 8 profiles sequentially against the lib module

### Test Naming Conventions

**Test class**: `HelloWorld_{Action}_{Type}_Test` — one class per method under test
- `HelloWorld_Send_DatagramSocket_Test` tests `send(DatagramSocket)`
- `HelloWorld_Write_DatagramChannel_Test` tests `write(DatagramChannel)`
- `HelloWorld_Send_DatagramChannel_Target_Test` tests `send(DatagramChannel, SocketAddress)`

**`_Test` vs `__Test` — trailing-underscore count distinguishes two *kinds* of test class**:

- `*_Test` (single trailing underscore) — **contract tests**. These specify what the method-under-test must do: null-arg validation, return value, interactions with collaborators (typically via `mock(...)` + `verify(...)`). They are the executable specification. The implementation is driven by them, and a failure here means the impl is wrong. Example: `HelloWorld_Update_MessageDigest_Test`, `HelloWorld_Add_SequencedCollection_Function_Test`.

- `*__Test` (double trailing underscore — 畵蛇添足, "adding feet") — **extras / explorations**. These **stub the method-under-test itself** (typically `doAnswer(...).when(service()).<method>(...)`) so they can run independently of whether the real implementation exists or is correct. Their purpose is to catalogue how real collaborators (concrete collection types, real JCA providers, real files, real network I/O, etc.) behave when called by the stub's loop. Example: `HelloWorld_Update_MessageDigest__Test` exercises real `MessageDigest.getInstance(...)` providers; `HelloWorld_Add_SequencedCollection_Function__Test` exercises real `SequencedCollection` subtypes.
  - The stub inside `__Test` is the *test's own* iteration logic, **not a copy of the real impl** and **not required to be equivalent to it**. Don't critique a `__Test` stub for diverging from the real method (null-check messages, `set(byte[])` skipped, primitive `byte` vs boxed `Byte`, etc.) — divergence is expected. The contract tests live in `_Test`.
  - A `__Test` should keep running even if the real method body is wiped to `throw new AssertionError("TODO")`. That property is the whole point.

**Test method**: `(method)_{then}_{given}` — all three parts may be omitted
- `_ThrowNullPointerException_SocketIsNull` — throws NPE when socket is null
- `_ThrowIllegalArgumentException_SocketIsNotConnected` — throws IAE when not connected
- `__()` — happy path (all parts omitted, verifies normal behavior)
- `_添足_畵蛇()` — integration test with `@畵蛇添足` annotation
  - 畵蛇添足 (huà shé tiān zú) = "drawing a snake and adding feet" (Chinese idiom for doing something superfluous)
  - `_添足_畵蛇` = `_{then}_{given}` = "adding feet" given "drawing a snake"
  - Marks tests that go beyond unit testing (e.g., real network I/O instead of mocks)

### Test Documentation Conventions

- **Test class javadoc**: Every concrete test class must specify which class and method it tests, using `{@link}` references. Example: `"A class for testing {@link HelloWorld#write(OutputStream) write(stream)} method."`
- **Test method javadoc**: Every test method must be documented starting with "Asserts" or "Verifies". Example: `"Verifies that the method throws a {@link NullPointerException} when the {@code channel} argument is {@code null}."`

## Key Conventions

- Java Module System (`module-info.java`) is used across modules
- Annotation processors: Lombok, Error Prone, NullAway
- Null safety: JSpecify annotations (`@NullMarked`, `@Nullable`)
- Custom annotations `@屋上架屋` and `@屋下架屋` are used as code documentation markers
- Logging: SLF4J API with Logback runtime, plus `jul-to-slf4j` bridge

## Javadoc Conventions

- **Articles**: Use "the given" (not "given"), "the specified", "the result"
- **Oxford commas**: Applied consistently in lists of 3+ items
- **JLS references**: Use `se25` version (e.g., `jls/se25/html/jls-9.html`)
- **Type precision**: Match descriptions to exact types (e.g., "asynchronous file channel" for `AsynchronousFileChannel`, not "file channel")
- **ASCII diagrams**: Used in `<pre>` blocks to visualize:
  - Array indices and contents: `|h|e|l|l|o|,| |w|o|r|l|d|`
  - ByteBuffer state (position, limit, capacity, remaining)
  - Before/after states for I/O operations
- **@author**: `@author Jin Kwon &lt;onacit_at_gmail.com&gt;`
- **@implSpec**: Describes default implementation behavior
- **@apiNote**: Documents usage notes (e.g., "does not flush the stream")

## Tracking Documents

- **`TARGETS.md`** — Catalog of all interface methods organized by Java API package, with call-chain dependencies, status (active/deprecated), and potential additions
- **`ASSESSMENTS.md`** — Code review findings per interface and method (issues, stub implementations, missing javadoc, etc.)
