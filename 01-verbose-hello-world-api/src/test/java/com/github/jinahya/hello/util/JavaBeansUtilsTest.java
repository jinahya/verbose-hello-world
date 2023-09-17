package com.github.jinahya.hello.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.beans.IntrospectionException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.stream.Stream;

@Slf4j
class JavaBeansUtilsTest {

    @Test
    void __() throws SocketException, ReflectiveOperationException, IntrospectionException {
//        JavaBeansUtils2.<Void>acceptEachProperty(
//                null,
//                NetworkInterface.getNetworkInterfaces().nextElement(),
//                p -> n -> v -> {
//                    if (v instanceof Stream<?>) {
//                        ((Stream<?>) v).forEach(e -> {
//                            log.debug("\te: {}", e);
//                        });
//                    }
//                    log.debug("{}: {}", n, v);
//                });
    }
}
