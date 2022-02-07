package com.github.jinahya.hello;

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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

import static java.lang.String.format;
import static java.net.NetworkInterface.getNetworkInterfaces;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

/**
 * A class for testing {@link NetworkInterface}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://www.baeldung.com/java-network-interfaces">Working with
 * Network Interfaces in Java (Bealdung)</a>
 */
@Slf4j
class NetworkInterfaceTest {

    private static String hexadecimal(final byte[] bytes, final String format,
                                      final String delimiter) {
        requireNonNull(bytes, "bytes is null");
        requireNonNull(delimiter, "delimiter is null");
        return range(0, bytes.length)
                .mapToObj(i -> format(format, bytes[i]))
                .collect(joining(delimiter));
    }

    private static void inetAddress(final String indent,
                                    final InetAddress address) {
        requireNonNull(indent, "indent is null");
        requireNonNull(address, "address is null");
        log.debug("{}address: {}", indent,
                  ofNullable(address.getAddress())
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

    @Disabled("takes too long")
    @Test
    void printNetworkInterfaces() throws SocketException {
        final var nie = getNetworkInterfaces();
        while (nie.hasMoreElements()) {
            final var ni = nie.nextElement();
            log.debug("network interface: {}", ni);
            log.debug("\tdisplay name: {}", ni.getDisplayName());
            log.debug("\thardware address: {}",
                      ofNullable(ni.getHardwareAddress())
                              .map(v -> hexadecimal(v, "%02x", ":"))
                              .orElse(null));
            log.debug("\tindex: {}", ni.getIndex());
            for (final var iae = ni.getInetAddresses();
                 iae.hasMoreElements(); ) {
                final var ia = iae.nextElement();
                log.debug("\tinet address: {}", ia);
                inetAddress("\t\t", ia);
            }
            for (final var ia : ni.getInterfaceAddresses()) {
                log.debug("\tinterface address: {}", ia);
                final var address = ia.getAddress();
                log.debug("\t\taddress: {}", address);
                inetAddress("\t\t\t", address);
                final var broadcast = ia.getBroadcast();
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
            for (final var sie = ni.getSubInterfaces();
                 sie.hasMoreElements(); ) {
                final var si = sie.nextElement();
                log.debug("\tsub interface: {}", si);
            }
            log.debug("\tloopback: {}", ni.isLoopback());
            log.debug("\tpoint to point: {}", ni.isPointToPoint());
            log.debug("\tup: {}", ni.isUp());
            log.debug("\tvirtual: {}", ni.isVirtual());
            log.debug("\tsupports multicast: {}", ni.supportsMulticast());
        }
    }
}
