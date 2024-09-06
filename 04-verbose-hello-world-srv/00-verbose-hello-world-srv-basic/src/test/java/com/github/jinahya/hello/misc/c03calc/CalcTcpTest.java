package com.github.jinahya.hello.misc.c03calc;

/*-
 * #%L
 * verbose-hello-world-srv-common
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Slf4j
class CalcTcpTest {

    private static final List<Class<?>> SERVER_CLASSES = List.of(
            CalcTcp1Server.class,
            CalcTcp3Server.class,
            CalcTcp5Server.class
    );

    private static List<Class<?>> serverClasses() {
        return SERVER_CLASSES;
    }

    private static final List<Class<?>> CLIENT_CLASSES = List.of(
            CalcTcp1Client.class,
            CalcTcp3Client.class,
            CalcTcp5Client.class
    );

    private static List<Class<?>> clientClasses() {
        return CLIENT_CLASSES;
    }

    private static Stream<Arguments> getClassesArgumentsList() {
        return SERVER_CLASSES.stream()
                .flatMap(sc -> CLIENT_CLASSES.stream()
                        .map(cc -> Arguments.of(Named.of(sc.getSimpleName(), sc),
                                                Named.of(cc.getSimpleName(), cc))));
    }

    private static final String QUIT_PLUS_ENTER = "quit!" + System.lineSeparator();

    private static final int QUIT_PLUS_ENTER_LENGTH =
            QUIT_PLUS_ENTER.getBytes(StandardCharsets.US_ASCII).length;

    @DisplayName("quit!")
    @MethodSource({"serverClasses"})
    @ParameterizedTest
    void __quit(final Class<?> serverClass) throws Exception {
        log.debug("server: {}", serverClass.getSimpleName());
        serverClass.getClassLoader().setDefaultAssertionStatus(true);
        final var quitPlusEnterBytes = QUIT_PLUS_ENTER.getBytes(StandardCharsets.US_ASCII);
        try (var executor = Executors.newFixedThreadPool(1)) {
            final var pos = new PipedOutputStream();
            final var pis = new PipedInputStream(pos, quitPlusEnterBytes.length);
            final var systemIn = System.in;
            try {
                System.setIn(pis);
                var server = executor.submit(() -> {
                    try {
                        serverClass.getMethod("main", String[].class)
                                .invoke(null, new Object[] {new String[0]});
                    } catch (final Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                pos.write(quitPlusEnterBytes);
                pos.flush();
                server.get(16L, TimeUnit.SECONDS);
                executor.shutdown();
                if (!executor.awaitTermination(8L, TimeUnit.SECONDS)) {
                    log.error("executor not terminated");
                }
            } finally {
                System.setIn(systemIn);
            }
        }
    }

    @DisplayName("one2one")
    @MethodSource({"getClassesArgumentsList"})
    @ParameterizedTest
    void __(final Class<?> serverClass, final Class<?> clientClass) throws Exception {
        log.debug("server: {}", serverClass.getSimpleName());
        log.debug("client: {}", clientClass.getSimpleName());
        serverClass.getClassLoader().setDefaultAssertionStatus(true);
        clientClass.getClassLoader().setDefaultAssertionStatus(true);
        try (var executor = Executors.newFixedThreadPool(2)) {
            final var pos = new PipedOutputStream();
            final var pis = new PipedInputStream(pos, QUIT_PLUS_ENTER_LENGTH);
            final var systemIn = System.in;
            try {
                System.setIn(pis);
                // -------------------------------------------------------------------------- server
                final var serverFuture = executor.submit(() -> {
                    try {
                        serverClass.getMethod("main", String[].class)
                                .invoke(null, new Object[] {new String[0]});
                    } catch (final Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                // --------------------------------------------------------------------------- await
                final var duration = Duration.ofMillis(100L);
                Awaitility.await()
                        .timeout(duration.plusMillis(1L))
                        .pollDelay(duration)
                        .untilAsserted(() -> {
                        });
                // -------------------------------------------------------------------------- client
                final var clientFuture = executor.submit(() -> {
                    try {
                        clientClass.getMethod("main", String[].class)
                                .invoke(null, new Object[] {new String[0]});
                    } catch (final Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                // -------------------------------------------------- await-until-client-future-done
                Awaitility.await()
                        .timeout(Duration.ofSeconds(8L))
                        .pollDelay(Duration.ofMillis(100L))
                        .until(clientFuture::isDone);
                // --------------------------------------------------------------------------- quit!
                pos.write(_TestUtils.quitPlusEnterBytes());
                pos.flush();
                serverFuture.get();
            } finally {
                System.setIn(systemIn);
            }
        }
    }

    @DisplayName("one2many")
    @MethodSource({"serverClasses"})
    @ParameterizedTest
    void __(final Class<?> serverClass) throws Exception {
        log.debug("server: {}", serverClass.getSimpleName());
        serverClass.getClassLoader().setDefaultAssertionStatus(true);
        try (var executor = Executors.newFixedThreadPool(CLIENT_CLASSES.size() + 1)) {
            final var pos = new PipedOutputStream();
            final var pis = new PipedInputStream(pos, QUIT_PLUS_ENTER_LENGTH);
            final var systemIn = System.in;
            try {
                System.setIn(pis);
                // -------------------------------------------------------------------------- server
                final var serverFuture = executor.submit(() -> {
                    try {
                        serverClass.getMethod("main", String[].class)
                                .invoke(null, new Object[] {new String[0]});
                    } catch (final Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                final var duration = Duration.ofMillis(100L);
                Awaitility.await()
                        .timeout(duration.plusMillis(1L))
                        .pollDelay(duration)
                        .untilAsserted(() -> {
                        });
                // ------------------------------------------------------------------------- clients
                final var clientFutures = new ArrayList<Future<?>>();
                for (final Class<?> clientClass : CLIENT_CLASSES) {
                    log.debug("client: {}", clientClass.getSimpleName());
                    clientFutures.add(executor.submit(() -> {
                        try {
                            clientClass.getMethod("main", String[].class)
                                    .invoke(null, new Object[] {new String[0]});
                        } catch (final Exception e) {
                            throw new RuntimeException(e);
                        }
                    }));
                }
                Awaitility.await()
                        .timeout(Duration.ofSeconds(8L))
                        .pollDelay(Duration.ofMillis(100L))
                        .until(() -> {
                            for (final var clientFuture : clientFutures) {
                                if (!clientFuture.isDone()) {
                                    return false;
                                }
                            }
                            return true;
                        });
                // --------------------------------------------------------------------------- quit!
                pos.write(_TestUtils.quitPlusEnterBytes());
                pos.flush();
                serverFuture.get();
            } finally {
                System.setIn(systemIn);
            }
        }
    }
}
