package com.github.jinahya.hello.misc;

/*-
 * #%L
 * verbose-hello-world-srv-common
 * %%
 * Copyright (C) 2018 - 2022 Jinahya, Inc.
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

import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A class for testing {@link NetworkInterface}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://www.baeldung.com/java-network-interfaces">Working with
 * Network Interfaces in Java (Bealdung)</a>
 */
@Slf4j
class NetworkInterfaceTest {

    private static <T> void log(Object indent, Class<T> clazz, final T object) {
        Objects.requireNonNull(indent, "indent is null");
        Objects.requireNonNull(clazz, "clazz is null");
        Objects.requireNonNull(object, "object is null");
        for (var method : clazz.getMethods()) {
            if (method.getDeclaringClass() != clazz) {
                continue;
            }
            if (method.isSynthetic()) {
                continue;
            }
            var modifier = method.getModifiers();
            if (Modifier.isStatic(modifier)) {
                continue;
            }
            if (method.getParameterTypes().length > 0) {
                continue;
            }
            try {
                Object value = method.invoke(object);
                if (value instanceof byte[]) {
                    value = hexadecimal((byte[]) value);
                }
                log.debug("{}{}: {}", indent, method.getName(), value);
            } catch (ReflectiveOperationException roe) {
                throw new RuntimeException(roe);
            }
        }
    }

    private static String hexadecimal(byte[] bytes, String format, String delimiter) {
        Objects.requireNonNull(bytes, "bytes is null");
        Objects.requireNonNull(delimiter, "delimiter is null");
        return IntStream.range(0, bytes.length)
                .mapToObj(i -> String.format(format, bytes[i]))
                .collect(Collectors.joining(delimiter));
    }

    private static String hexadecimal(byte[] bytes) {
        return hexadecimal(bytes, "$02x", "");
    }

    private static void inetAddress(String indent, InetAddress address) {
        Objects.requireNonNull(indent, "indent is null");
        Objects.requireNonNull(address, "address is null");
        if (true) {
            log(indent, InetAddress.class, address);
        }
        log.debug("{}address: {}", indent,
                  Optional.ofNullable(address.getAddress())
                          .map(v -> hexadecimal(v, "%02X", ""))
                          .orElse("null"));
        log.debug("{}canonical host name: {}", indent,
                  address.getCanonicalHostName());
        log.debug("{}host address: {}", indent, address.getHostAddress());
        log.debug("{}host name: {}", indent, address.getHostName());
        log.debug("{}any local address: {}", indent,
                  address.isAnyLocalAddress());
        log.debug("{}link local address: {}", indent,
                  address.isLinkLocalAddress());
        log.debug("{}loopback address: {}", indent,
                  address.isLoopbackAddress());
        log.debug("{}multicast global: {}", indent, address.isMCGlobal());
        log.debug("{}multicast link local: {}", indent,
                  address.isMCLinkLocal());
        log.debug("{}multicast node local: {}", indent,
                  address.isMCNodeLocal());
        log.debug("{}multicast org local: {}", indent, address.isMCOrgLocal());
        log.debug("{}multicast site local: {}", indent,
                  address.isMCSiteLocal());
        log.debug("{}multicast address: {}", indent,
                  address.isMulticastAddress());
        log.debug("{}site local address: {}", indent,
                  address.isSiteLocalAddress());
    }

    public static void main(String... args) throws SocketException {
        var nie = NetworkInterface.getNetworkInterfaces();
        while (nie.hasMoreElements()) {
            var ni = nie.nextElement();
            log.debug("network interface: {}", ni);
            log.debug("\tdisplay name: {}", ni.getDisplayName());
            log.debug("\thardware address: {}",
                      Optional.ofNullable(ni.getHardwareAddress())
                              .map(v -> hexadecimal(v, "%02x", ":"))
                              .orElse(null));
            log.debug("\tindex: {}", ni.getIndex());
            for (var iae = ni.getInetAddresses(); iae.hasMoreElements(); ) {
                var ia = iae.nextElement();
                log.debug("\tinet address: {}", ia);
                inetAddress("\t\t", ia);
            }
            for (var ia : ni.getInterfaceAddresses()) {
                log.debug("\tinterface address: {}", ia);
                var address = ia.getAddress();
                log.debug("\t\taddress: {}", address);
                inetAddress("\t\t\t", address);
                var broadcast = ia.getBroadcast();
                log.debug("\t\tbroadcast: {}", broadcast);
                if (broadcast != null) {
                    inetAddress("\t\t\t", broadcast);
                }
                log.debug("\t\tnetwork prefix length: {}",
                          ia.getNetworkPrefixLength());
            }
            log.debug("\tmtu: " + ni.getMTU());
            log.debug("\tname: " + ni.getName());
            log.debug("\tparent: " + ni.getParent());
            for (var si = ni.getSubInterfaces();
                 si.hasMoreElements(); ) {
                var subInterface = si.nextElement();
                log.debug("\tsub interface: {}", subInterface);
            }
            log.debug("\tloopback: {}", ni.isLoopback());
            log.debug("\tisPointToPoint: {}", ni.isPointToPoint());
            log.debug("\tup: {}", ni.isUp());
            log.debug("\tvirtual: {}", ni.isVirtual());
            log.debug("\tsupports multicast: {}",
                      ni.supportsMulticast());
        }
    }
}
