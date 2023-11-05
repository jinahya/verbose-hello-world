package com.github.jinahya.hello.util;

import java.io.UncheckedIOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class JavaNetUtils {

    public static <R> R applyLoopbackAddressesIPv4(
            final Function<? super InetAddress, ? extends R> function) {
        var address = InetAddress.getLoopbackAddress();
        if (address instanceof Inet6Address) {
            try {
                address = InetAddress.getByName("127.0.0.1");
            } catch (final UnknownHostException uhe) {
                throw new UncheckedIOException(uhe);
            }
        }
        return function.apply(address);
    }

    public static <R> R applyLoopbackAddressesIPv6(
            final Function<? super InetAddress, ? extends R> function) {
        var address = InetAddress.getLoopbackAddress();
        if (address instanceof Inet4Address) {
            try {
                address = InetAddress.getByName("::1");
            } catch (final UnknownHostException uhe) {
                throw new UncheckedIOException(uhe);
            }
        }
        return function.apply(address);
    }

    public static <R> R applyLoopbackAddresses(
            final BiFunction<? super InetAddress, ? super InetAddress, ? extends R> function) {
        return applyLoopbackAddressesIPv4(
                ipv4 -> applyLoopbackAddressesIPv6(ipv6 -> function.apply(ipv4, ipv6))
        );
    }

    private JavaNetUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
