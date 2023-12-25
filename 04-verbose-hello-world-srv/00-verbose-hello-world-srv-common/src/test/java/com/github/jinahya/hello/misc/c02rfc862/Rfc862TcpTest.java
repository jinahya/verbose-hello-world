package com.github.jinahya.hello.misc.c02rfc862;

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

import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Constants;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@Slf4j
class Rfc862TcpTest {

    private static final List<Class<?>> SERVER_CLASSES = List.of(
            Rfc862Tcp0Server.class,
            Rfc862Tcp1Server.class,
            Rfc862Tcp2Server.class,
            Rfc862Tcp4Server.class,
            Rfc862Tcp5Server.class
    );

    private static final List<Class<?>> CLIENT_CLASSES = List.of(
            Rfc862Tcp0Client.class,
            Rfc862Tcp1Client.class,
            Rfc862Tcp2Client.class,
            Rfc862Tcp4Client.class,
            Rfc862Tcp5Client.class
    );

    private static Stream<Arguments> getClassesArgumentsList() {
        return SERVER_CLASSES.stream()
                .flatMap(sc -> CLIENT_CLASSES.stream()
                        .map(cc -> Arguments.of(Named.of(sc.getSimpleName(), sc),
                                                Named.of(cc.getSimpleName(), cc))));
    }

    @MethodSource({"getClassesArgumentsList"})
    @ParameterizedTest
    void __(Class<?> serverClass, Class<?> clientClass)
            throws Exception {
        log.debug("server: {}", serverClass.getSimpleName());
        log.debug("client: {}", clientClass.getSimpleName());
        serverClass.getClassLoader().setDefaultAssertionStatus(true);
        clientClass.getClassLoader().setDefaultAssertionStatus(true);
        var executor = Executors.newFixedThreadPool(2);
        var server = executor.submit(() -> {
            try {
                serverClass.getMethod("main", String[].class)
                        .invoke(null, new Object[] {new String[0]});
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
        Awaitility.await().pollDelay(Duration.ofMillis(100L))
                .untilAsserted(() -> Assertions.assertTrue(true));
        var client = executor.submit(() -> {
            try {
                clientClass.getMethod("main", String[].class)
                        .invoke(null, new Object[] {new String[0]});
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
        client.get(_Rfc86_Constants.CLIENT_PROGRAM_TIMEOUT,
                   _Rfc86_Constants.CLIENT_PROGRAM_TIMEOUT_UNIT);
        server.get(_Rfc86_Constants.SERVER_PROGRAM_TIMEOUT,
                   _Rfc86_Constants.SERVER_PROGRAM_TIMEOUT_UNIT);
        executor.shutdown();
    }
}
