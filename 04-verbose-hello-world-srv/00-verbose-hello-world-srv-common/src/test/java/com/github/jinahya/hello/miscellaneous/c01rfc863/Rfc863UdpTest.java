package com.github.jinahya.hello.miscellaneous.c01rfc863;

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
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@Slf4j
class Rfc863UdpTest {

    private static final List<Class<?>> SERVER_CLASSES = List.of(
            Rfc863Udp1Server.class,
            Rfc863Udp2Server.class
    );

    private static final List<Class<?>> CLIENT_CLASSES = List.of(
            Rfc863Udp1Client.class,
            Rfc863Udp2Client.class
    );

    private static Stream<Arguments> getClassesArgumentsList() {
        return SERVER_CLASSES.stream()
                .flatMap(sc -> CLIENT_CLASSES.stream()
                        .map(cc -> Arguments.of(Named.of(sc.getSimpleName(), sc),
                                                Named.of(cc.getSimpleName(), cc))));
    }

    //    @Disabled
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
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        Thread.sleep(100L);
        var client = executor.submit(() -> {
            try {
                clientClass.getMethod("main", String[].class)
                        .invoke(null, new Object[] {new String[0]});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        client.get();
        server.get();
    }
}
