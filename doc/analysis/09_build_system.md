# 09. Build System

## Overview

The project uses Apache Maven as its build system with a multi-module structure and extensive plugin configuration.

## Maven Requirements

| Requirement | Version |
|-------------|---------|
| Maven | 3.6.3+ |
| Java | 21+ |

## Project Structure

```
verbose-hello-world/                    # Root POM (pom)
├── 01-verbose-hello-world-api/         # API module (jar)
├── 02-verbose-hello-world-lib/         # Library module (jar)
└── 03-verbose-hello-world-app/         # App parent (pom)
    ├── 01-verbose-hello-world-app1/    # App1 (jar)
    ├── 02-verbose-hello-world-app2/    # App2 (jar)
    ├── 03-verbose-hello-world-app3/    # App3 (jar)
    └── 04-verbose-hello-world-app4/    # App4 (jar)
```

## Parent POM

```xml
<parent>
    <groupId>com.github.jinahya</groupId>
    <artifactId>jinahya-parent</artifactId>
    <version>0.9.2</version>
</parent>
```

The parent POM provides:
- Dependency version management
- Plugin version management
- Common build configurations

## Key Properties

```xml
<properties>
    <!-- Java Version -->
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
    <maven.compiler.release>21</maven.compiler.release>

    <!-- Encoding -->
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>

    <!-- Dependency Versions -->
    <version.awaitility>4.3.0</version.awaitility>
    <version.mockito>5.21.0</version.mockito>
    <version.logback-classic>1.5.24</version.logback-classic>
    <!-- ... more versions ... -->
</properties>
```

## Maven Plugins

### Compiler Plugin

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.14.1</version>
    <configuration>
        <release>21</release>
        <compilerArgs>
            <arg>-Xlint:all</arg>
        </compilerArgs>
        <annotationProcessorPaths>
            <!-- Lombok -->
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${version.lombok}</version>
            </path>
            <!-- Error Prone -->
            <path>
                <groupId>com.google.errorprone</groupId>
                <artifactId>error_prone_core</artifactId>
                <version>${version.error_prone}</version>
            </path>
            <!-- NullAway -->
            <path>
                <groupId>com.uber.nullaway</groupId>
                <artifactId>nullaway</artifactId>
                <version>${version.nullaway}</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

### Surefire Plugin (Unit Tests)

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.5.4</version>
    <configuration>
        <includes>
            <include>**/*Test.java</include>
            <include>**/*Tests.java</include>
        </includes>
    </configuration>
</plugin>
```

### Shade Plugin (Fat JAR)

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.6.1</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
            <configuration>
                <transformers>
                    <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                        <mainClass>${mainClass}</mainClass>
                    </transformer>
                </transformers>
                <shadedArtifactAttached>true</shadedArtifactAttached>
                <shadedClassifierName>shade</shadedClassifierName>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Assembly Plugin

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-assembly-plugin</artifactId>
    <version>3.8.0</version>
    <configuration>
        <archive>
            <manifest>
                <mainClass>${mainClass}</mainClass>
            </manifest>
        </archive>
        <descriptorRefs>
            <descriptorRef>jar-with-dependencies</descriptorRef>
        </descriptorRefs>
    </configuration>
    <executions>
        <execution>
            <id>make-assembly</id>
            <phase>package</phase>
            <goals>
                <goal>single</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Spring Boot Plugin

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>repackage</id>
            <goals>
                <goal>repackage</goal>
            </goals>
            <configuration>
                <classifier>boot</classifier>
                <mainClass>${mainClass}</mainClass>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### JaCoCo Plugin (Code Coverage)

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

### SpotBugs Plugin

```xml
<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
    <version>4.9.8.2</version>
</plugin>
```

### Build Helper Plugin

Used for adding additional source directories:

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

## Maven Profiles

### DI Framework Profiles

```xml
<profiles>
    <!-- CDI with OpenWebBeans -->
    <profile>
        <id>cdi-se-openwebbeans</id>
        <dependencies>...</dependencies>
        <build>...</build>
    </profile>

    <!-- CDI with Weld -->
    <profile>
        <id>cdi-se-weld</id>
        <dependencies>...</dependencies>
        <build>...</build>
    </profile>

    <!-- Google Dagger -->
    <profile>
        <id>di-dagger</id>
        <dependencies>...</dependencies>
        <build>...</build>
    </profile>

    <!-- Google Guice -->
    <profile>
        <id>di-guice</id>
        <dependencies>...</dependencies>
        <build>...</build>
    </profile>

    <!-- GlassFish HK2 -->
    <profile>
        <id>di-hk2</id>
        <dependencies>...</dependencies>
        <build>...</build>
    </profile>

    <!-- Spring Framework -->
    <profile>
        <id>di-spring</id>
        <dependencies>...</dependencies>
        <build>...</build>
    </profile>
</profiles>
```

### Executable Generation Profile

```xml
<profile>
    <id>generate-executables</id>
    <activation>
        <property>
            <name>generateExecutables</name>
            <value>true</value>
        </property>
    </activation>
    <build>
        <plugins>
            <!-- Shade, Assembly, Spring Boot plugins -->
        </plugins>
    </build>
</profile>
```

## Maven Wrapper

The project includes Maven Wrapper for consistent builds:

```bash
# Unix/macOS
./mvnw clean install

# Windows
mvnw.cmd clean install
```

Wrapper files:
- `mvnw` - Unix shell script
- `mvnw.cmd` - Windows batch script
- `.mvn/wrapper/maven-wrapper.jar` - Wrapper JAR
- `.mvn/wrapper/maven-wrapper.properties` - Configuration

## Common Build Commands

```bash
# Clean and build
./mvnw clean install

# Build without tests
./mvnw clean install -DskipTests

# Run tests only
./mvnw test

# Run specific profile tests
./mvnw test -Pcdi-se-weld

# Generate code coverage report
./mvnw test jacoco:report

# Check for dependency updates
./mvnw versions:display-dependency-updates

# Check for plugin updates
./mvnw versions:display-plugin-updates

# Generate site documentation
./mvnw site

# Run SpotBugs analysis
./mvnw spotbugs:check

# Create executable JARs
./mvnw package -DgenerateExecutables=true
```

## Generated Artifacts

For each application module:

| Artifact | Description |
|----------|-------------|
| `*-SNAPSHOT.jar` | Standard JAR |
| `*-SNAPSHOT-shade.jar` | Fat JAR (all dependencies) |
| `*-SNAPSHOT-jar-with-dependencies.jar` | Assembly JAR |
| `*-SNAPSHOT-boot.jar` | Spring Boot executable |

---
[Back to Index](00_index.md) | [Previous: Testing Strategy](08_testing.md) | [Next: Code Quality](10_code_quality.md)
