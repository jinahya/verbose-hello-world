# AGENTS.md

This file provides Codex guidance for working in this repository.

## Scope

These instructions apply to this repository tree, recursively.

Do not read, write, copy, move, or delete files outside this repository while working on project tasks. If a request requires external files, stop and ask for direction.

## Project

This is a standalone Java Maven project for a deliberately verbose `hello, world` implementation. It explores Java I/O and related APIs using the 12-byte payload `hello, world`.

The root project is a Maven aggregator with these modules:

- `01-verbose-hello-world-api`: API and default implementations.
- `02-verbose-hello-world-lib`: concrete implementations and DI/CDI examples.
- `03-verbose-hello-world-app`: application modules.

## Build

Use the included Maven wrapper.

```bash
./mvnw test
./mvnw test -pl 01-verbose-hello-world-api
./mvnw test -pl 02-verbose-hello-world-lib
./mvnw clean install
```

The project targets Java 25 with preview features enabled through the Maven configuration.

## Working Rules

- Prefer focused edits that match the existing package, naming, and test style.
- Keep Java source and Javadoc in English.
- Do not modify generated files, build output, or local IDE metadata unless the task explicitly requires it.
- Do not make broad formatting-only changes.
- Do not mutate git state unless the user explicitly asks for that exact git operation.
- Treat commented-out instructional code as intentional unless the user asks to work on that specific teaching fragment.

## Tests

Test classes use the repository's naming conventions:

- `*_Test`: contract tests for behavior.
- `*__Test`: exploratory tests using stubs or real collaborators.

Prefer running the smallest relevant Maven test command first, then broaden only when the changed surface requires it.

## Documentation

When adding or editing tests, keep Javadocs concise and specific:

- Test class Javadocs should identify the class or method under test using `{@link ...}`.
- Test method Javadocs should begin with `Asserts` or `Verifies`.

When editing production Javadocs, preserve the existing style, including `@implSpec`, `@apiNote`, precise type names, and clear article usage such as "the given" or "the specified".
