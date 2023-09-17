package com.github.jinahya.hello.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.beans.IntrospectionException;
import java.net.NetworkInterface;
import java.net.SocketException;

@Slf4j
class JavaBeansUtilsTest {

    @Test
    void __NetworkInterfaces()
            throws SocketException, ReflectiveOperationException, IntrospectionException {
        for (final var e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ) {
            final var networkInterface = e.nextElement();
            JavaBeansUtils.acceptEachProperty(
                    null,
                    networkInterface,
                    p -> i -> {
                        log.debug("{} / {}", p, i);
                        return (JavaBeansUtils.PropertyInfoHolder) () -> i;
                    }
            );
        }
    }
}
