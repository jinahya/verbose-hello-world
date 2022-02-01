package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
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

    private static String hexadecimal(final byte[] bytes, final String format,
                                      final String delimiter) {
        Objects.requireNonNull(bytes, "bytes is null");
        Objects.requireNonNull(delimiter, "delimiter is null");
        return IntStream.range(0, bytes.length)
                .mapToObj(i -> String.format(format, bytes[i]))
                .collect(Collectors.joining(delimiter));
    }

    private static void inetAddress(final String indent,
                                    final InetAddress address) {
        Objects.requireNonNull(indent, "indent is null");
        Objects.requireNonNull(address, "address is null");
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

    @Disabled("takes too long")
    @Test
    void printNetworkInterfaces() throws SocketException {
        final Enumeration<NetworkInterface> nie
                = NetworkInterface.getNetworkInterfaces();
        while (nie.hasMoreElements()) {
            final NetworkInterface ni = nie.nextElement();
            log.debug("network interface: {}", ni);
            log.debug("\tdisplay name: {}", ni.getDisplayName());
            log.debug("\thardware address: {}",
                      Optional.ofNullable(ni.getHardwareAddress())
                              .map(v -> hexadecimal(v, "%02x", ":"))
                              .orElse(null));
            log.debug("\tindex: {}", ni.getIndex());
            for (final Enumeration<InetAddress> iae = ni.getInetAddresses();
                 iae.hasMoreElements(); ) {
                final InetAddress ia = iae.nextElement();
                log.debug("\tinet address: {}", ia);
                inetAddress("\t\t", ia);
            }
            for (final InterfaceAddress ia : ni.getInterfaceAddresses()) {
                log.debug("\tinterface address: {}", ia);
                final InetAddress address = ia.getAddress();
                log.debug("\t\taddress: {}", address);
                inetAddress("\t\t\t", address);
                final InetAddress broadcast = ia.getBroadcast();
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
            for (final Enumeration<NetworkInterface> sie
                 = ni.getSubInterfaces();
                 sie.hasMoreElements(); ) {
                final NetworkInterface si = sie.nextElement();
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
