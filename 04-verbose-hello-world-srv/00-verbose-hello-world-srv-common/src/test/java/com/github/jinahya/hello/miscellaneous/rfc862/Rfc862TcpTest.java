package com.github.jinahya.hello.miscellaneous.rfc862;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@Slf4j
class Rfc862TcpTest {

    private static final List<Class<?>> SERVER_CLASSES = List.of(
            Rfc862Tcp1Server.class,
            Rfc862Tcp2Server.class,
            Rfc862Tcp3Server.class,
            Rfc862Tcp4Server.class
    );

    private static final List<Class<?>> CLIENT_CLASSES = List.of(
            Rfc862Tcp1Client.class,
            Rfc862Tcp2Client.class,
            Rfc862Tcp3Client.class,
            Rfc862Tcp4Client.class
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
        var executor = Executors.newFixedThreadPool(2);
        log.debug("serverClass: {}", serverClass);
        var server = executor.submit(() -> {
            try {
                serverClass.getMethod("main", String[].class)
                        .invoke(null, new Object[] {new String[0]});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        log.debug("clientClass: {}", clientClass);
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
