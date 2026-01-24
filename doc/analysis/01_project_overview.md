# 01. Project Overview

## Basic Information

| Attribute | Value |
|-----------|-------|
| **Artifact ID** | verbose-hello-world |
| **Group ID** | com.github.jinahya |
| **Version** | 0.0.1-SNAPSHOT |
| **Packaging** | pom (multi-module parent) |
| **Parent POM** | com.github.jinahya:jinahya-parent:0.9.2 |
| **Inception Year** | 2018 |

## Project Description

The verbose-hello-world project is an educational Java project that demonstrates multiple ways to implement a simple "hello, world" program. Rather than a single implementation, it showcases:

1. **Various I/O mechanisms** - Streams, NIO channels, async operations, network sockets
2. **Multiple DI frameworks** - Guice, CDI (Weld/OpenWebBeans), Dagger, HK2, Spring
3. **Service discovery** - Java SPI (ServiceLoader) pattern
4. **Best practices** - Multi-module Maven, code quality tools, comprehensive testing

## Repository Structure

```
verbose-hello-world/
├── .github/                    # GitHub Actions workflows
├── .idea/                      # IntelliJ IDEA configuration
├── .mvn/                       # Maven wrapper configuration
├── .vscode/                    # VS Code configuration
├── 01-verbose-hello-world-api/ # API module
├── 02-verbose-hello-world-lib/ # Implementation module
├── 03-verbose-hello-world-app/ # Application modules (4 variants)
├── doc/                        # Documentation
├── pom.xml                     # Root POM
├── README.md                   # Project readme
├── mvnw, mvnw.cmd              # Maven wrapper scripts
└── _*.sh                       # Utility shell scripts
```

## Key Characteristics

### Educational Focus
- Deliberately verbose code with extensive comments
- Multiple approaches to the same problem
- Comprehensive JavaDoc documentation
- Examples of various Java APIs (I/O, NIO, Reactive, etc.)

### Modern Java
- Targets Java SE 21
- Uses modern Java features
- Module system support (module-info.java)
- JSpecify annotations for null-safety

### Enterprise Patterns
- Dependency Injection with multiple frameworks
- Service Provider Interface (SPI)
- Clean separation of API, implementation, and application layers

### Quality Assurance
- Static analysis with SpotBugs and Error Prone
- Null-safety checking with NullAway
- Code coverage with JaCoCo
- SonarCloud integration

## The "hello, world" Message

The core functionality revolves around a 12-byte message:

```
h  e  l  l  o  ,     w  o  r  l  d
```

In hexadecimal:
```
68 65 6C 6C 6F 2C 20 77 6F 72 6C 64
```

The `HelloWorld` interface defines methods to write these 12 bytes to various targets.

## Module Dependencies

```
                    ┌──────────────┐
                    │   API (01)   │
                    │  Interfaces  │
                    └──────┬───────┘
                           │
                           ▼
                    ┌──────────────┐
                    │   LIB (02)   │
                    │Implementation│
                    └──────┬───────┘
                           │
        ┌──────────┬───────┴───────┬──────────┐
        ▼          ▼               ▼          ▼
   ┌─────────┐ ┌─────────┐   ┌─────────┐ ┌─────────┐
   │  App1   │ │  App2   │   │  App3   │ │  App4   │
   │ Direct  │ │   SPI   │   │  Guice  │ │   CDI   │
   └─────────┘ └─────────┘   └─────────┘ └─────────┘
```

## Build Requirements

- **JDK**: 21 or higher
- **Maven**: 3.6.3 or higher (wrapper included)
- **OS**: Cross-platform (Linux, macOS, Windows)

## Quick Start

```bash
# Clone the repository
git clone https://github.com/jinahya/verbose-hello-world.git
cd verbose-hello-world

# Build the project
./mvnw clean install

# Run an application variant
java -jar 03-verbose-hello-world-app/01-verbose-hello-world-app1/target/*-shade.jar
```

---
[Back to Index](00_index.md) | [Next: Architecture](02_architecture.md)
