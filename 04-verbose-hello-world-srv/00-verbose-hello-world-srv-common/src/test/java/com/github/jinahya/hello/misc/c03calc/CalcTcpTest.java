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

import com.github.jinahya.hello.HelloWorldServerConstants;
import com.github.jinahya.hello.misc._Rfc86_Constants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class CalcTcpTest {

    private static final List<Class<?>> SERVER_CLASSES = List.of(
            CalcTcp1Server.class,
            CalcTcp3Server.class
    );

    private static final List<Class<?>> CLIENT_CLASSES = List.of(
            CalcTcp1Client.class,
            CalcTcp3Client.class
    );

    private static Stream<Arguments> getClassesArgumentsList() {
        return SERVER_CLASSES.stream()
                .flatMap(sc -> CLIENT_CLASSES.stream()
                        .map(cc -> Arguments.of(Named.of(sc.getSimpleName(), sc),
                                                Named.of(cc.getSimpleName(), cc))));
    }

    @MethodSource({"getClassesArgumentsList"})
    @ParameterizedTest
    void __(Class<?> serverClass, Class<?> clientClass) throws Exception {
        log.debug("server: {}", serverClass.getSimpleName());
        log.debug("client: {}", clientClass.getSimpleName());
        serverClass.getClassLoader().setDefaultAssertionStatus(true);
        clientClass.getClassLoader().setDefaultAssertionStatus(true);
        var executor = Executors.newFixedThreadPool(2);
        final var systemIn = System.in;
        final var pos = new PipedOutputStream();
        final var pis = new PipedInputStream(
                pos,
                HelloWorldServerConstants.QUIT_AND_ENTER.length()
        );
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
            await().pollDelay(Duration.ofMillis(100L)).untilAsserted(() -> assertTrue(true));
            var client = executor.submit(() -> {
                try {
                    clientClass.getMethod("main", String[].class)
                            .invoke(null, new Object[] {new String[0]});
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            });
            client.get(_Rfc86_Constants.CLIENT_TIMEOUT, _Rfc86_Constants.CLIENT_TIMEOUT_UNIT);
            pos.write(HelloWorldServerConstants.QUIT_AND_ENTER.getBytes(StandardCharsets.US_ASCII));
            pos.flush();
            server.get(_Rfc86_Constants.SERVER_TIMEOUT, _Rfc86_Constants.SERVER_TIMEOUT_UNIT);
            executor.shutdown();
            final var terminated = executor.awaitTermination(8L, TimeUnit.SECONDS);
            assert terminated : "executor hasn't been terminated";
        } finally {
            System.setIn(systemIn);
        }
    }
}
