# 02. Architecture

## Module Structure

The project follows a clean three-layer architecture with clear separation of concerns.

### Layer Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                        APPLICATION LAYER                            │
│  ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐           │
│  │   App1    │ │   App2    │ │   App3    │ │   App4    │           │
│  │  Direct   │ │    SPI    │ │   Guice   │ │    CDI    │           │
│  └─────┬─────┘ └─────┬─────┘ └─────┬─────┘ └─────┬─────┘           │
└────────┼─────────────┼─────────────┼─────────────┼──────────────────┘
         │             │             │             │
         ▼             ▼             ▼             ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      IMPLEMENTATION LAYER                           │
│                  ┌─────────────────────────┐                        │
│                  │  02-verbose-hello-lib   │                        │
│                  │     HelloWorldImpl      │                        │
│                  └────────────┬────────────┘                        │
└───────────────────────────────┼─────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────────┐
│                           API LAYER                                 │
│                  ┌─────────────────────────┐                        │
│                  │  01-verbose-hello-api   │                        │
│                  │   HelloWorld interface  │                        │
│                  └─────────────────────────┘                        │
└─────────────────────────────────────────────────────────────────────┘
```

## Module Details

### 01-verbose-hello-world-api

**Purpose**: Defines contracts and utilities

| Package | Description |
|---------|-------------|
| `com.github.jinahya.hello` | Base package |
| `com.github.jinahya.hello.api` | Core interfaces |
| `com.github.jinahya.hello.api.spi` | Service provider interfaces |
| `com.github.jinahya.hello.api.util` | Utility classes |

**Exports** (module-info.java):
- `com.github.jinahya.hello`
- `com.github.jinahya.hello.api`
- `com.github.jinahya.hello.api.spi`

### 02-verbose-hello-world-lib

**Purpose**: Provides implementations of API interfaces

| Package | Description |
|---------|-------------|
| `com.github.jinahya.hello.lib` | Implementation classes |
| `com.github.jinahya.hello.lib.util` | Implementation utilities |

**Key Features**:
- Implements `HelloWorld` interface
- Provides `HelloWorldServiceProvider` implementation
- Supports multiple DI frameworks (test scope)

### 03-verbose-hello-world-app (Parent)

**Purpose**: Parent POM for application modules

**Common Configuration**:
- Main class: `com.github.jinahya.hello.app2_.HelloWorldMain`
- Executable generation enabled
- Shade, Assembly, and Spring Boot plugins configured

## Application Variants

| App | Approach | Key Technology |
|-----|----------|----------------|
| **app1** | Direct instantiation | `new HelloWorldImpl()` |
| **app2** | Service Provider Interface | `ServiceLoader<HelloWorldServiceProvider>` |
| **app3** | Dependency Injection | Google Guice + `@Inject` |
| **app4** | CDI | Weld SE + `@Inject` + `@Produces` |

## Package Structure

```
com.github.jinahya.hello
├── api/
│   ├── HelloWorld.java                 # Main interface
│   ├── HelloWorldFlow.java             # Flow/reactive support
│   ├── HelloWorldRevisited.java        # Alternative interface
│   ├── AsynchronousHelloWorld.java     # Async interface
│   ├── DefaultAsynchronousHelloWorld.java
│   ├── spi/
│   │   └── HelloWorldServiceProvider.java
│   └── util/
│       ├── HelloWorldLoggers.java
│       ├── HelloWorldValidator.java
│       ├── HelloWorldUtils.java
│       ├── Java*Utils.java             # Various utility classes
│       └── ...
├── lib/
│   ├── HelloWorldImpl.java             # Main implementation
│   ├── HelloWorldDemo.java
│   ├── HelloWorldWrap.java
│   └── util/
│       └── JavaLangObjectUtils.java
└── app/
    ├── HelloWorldMain.java             # Main class (per variant)
    ├── HelloWorldModule.java           # Guice module (app3)
    ├── HelloWorldProvider.java         # CDI provider (app4)
    └── HelloWorldQualifier.java        # CDI qualifier (app4)
```

## Dependency Flow

```
External Dependencies
        │
        ▼
┌───────────────────────────────────────────────────┐
│                     API Module                     │
│  Dependencies:                                     │
│  - SLF4J (logging)                                │
│  - JSpecify (null annotations)                    │
│  - Guava (utilities)                              │
│  - Jakarta Validation (provided)                  │
└───────────────────┬───────────────────────────────┘
                    │ compile
                    ▼
┌───────────────────────────────────────────────────┐
│                     LIB Module                     │
│  Dependencies:                                     │
│  - API module                                      │
│  - Jakarta Inject API (provided)                  │
│  - DI frameworks (test only)                      │
└───────────────────┬───────────────────────────────┘
                    │ compile/runtime
                    ▼
┌───────────────────────────────────────────────────┐
│                   APP Modules                      │
│  Dependencies (varies by app):                     │
│  - API module (compile)                           │
│  - LIB module (compile/runtime)                   │
│  - DI framework (Guice/CDI for app3/app4)        │
└───────────────────────────────────────────────────┘
```

## Design Principles

### 1. Interface Segregation
- Clean separation between API and implementation
- Applications depend on interfaces, not implementations

### 2. Dependency Inversion
- High-level modules don't depend on low-level modules
- Both depend on abstractions (HelloWorld interface)

### 3. Single Responsibility
- Each module has a clear, focused purpose
- Utilities are separated into dedicated packages

### 4. Open/Closed Principle
- API is stable (closed for modification)
- New implementations can be added (open for extension)

---
[Back to Index](00_index.md) | [Previous: Project Overview](01_project_overview.md) | [Next: API Module](03_api_module.md)
