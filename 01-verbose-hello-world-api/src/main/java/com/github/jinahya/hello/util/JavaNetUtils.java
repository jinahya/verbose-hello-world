package com.github.jinahya.hello.util;

/*-
 * #%L
 * verbose-hello-world-api
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

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class JavaNetUtils {

    public static void acceptNetworkInterfaces(final Predicate<? super NetworkInterface> predicate,
                                               final Consumer<? super NetworkInterface> consumer)
            throws SocketException {
        Objects.requireNonNull(predicate, "predicate is null");
        Objects.requireNonNull(consumer, "consumer is null");
        Collections.list(NetworkInterface.getNetworkInterfaces())
                .stream()
                .filter(predicate)
                .forEach(consumer::accept);
    }

    public static void acceptInetAddresses(
            final Predicate<? super NetworkInterface> interfacePredicate,
            final BiPredicate<? super NetworkInterface, ? super InetAddress> addressPredicate,
            final BiConsumer<? super NetworkInterface, ? super InetAddress> consumer)
            throws SocketException {
        Objects.requireNonNull(interfacePredicate, "interfacePredicate is null");
        Objects.requireNonNull(addressPredicate, "addressPredicate is null");
        Objects.requireNonNull(consumer, "consumer is null");
        acceptNetworkInterfaces(
                interfacePredicate,
                ni -> ni.inetAddresses()
                        .filter(ia -> addressPredicate.test(ni, ia))
                        .forEach(ia -> consumer.accept(ni, ia))
        );
    }

    public static Stream<NetworkInterface> networkInterfaceStream(
            final Predicate<? super NetworkInterface> predicate)
            throws SocketException {
        Objects.requireNonNull(predicate, "predicate is null");
        return Collections.list(NetworkInterface.getNetworkInterfaces())
                .stream().filter(predicate);
    }

    public static Stream<InetAddress> inetAddressStream(
            final Predicate<? super NetworkInterface> interfacePredicate,
            final BiPredicate<? super NetworkInterface, ? super InetAddress> addressPredicate)
            throws SocketException {
        Objects.requireNonNull(interfacePredicate, "interfacePredicate is null");
        Objects.requireNonNull(addressPredicate, "addressPredicate is null");
        return networkInterfaceStream(interfacePredicate)
                .flatMap(ni -> ni.inetAddresses().filter(ia -> addressPredicate.test(ni, ia)));
    }

    public static Stream<InetAddress> loopbackAddressStream(
            final Predicate<? super NetworkInterface> interfacePredicate)
            throws SocketException {
        return inetAddressStream(
                interfacePredicate,
                (ni, ia) -> ia.isLoopbackAddress()
        );
    }

    public static Stream<InetAddress> loopbackAddressStreamIPv4(
            final Predicate<? super NetworkInterface> interfacePredicate)
            throws SocketException {
        return inetAddressStream(
                interfacePredicate,
                (ni, ia) -> ia.isLoopbackAddress() && ia instanceof Inet4Address
        );
    }

    public static Stream<InetAddress> loopbackAddressStreamIPv6(
            final Predicate<? super NetworkInterface> interfacePredicate)
            throws SocketException {
        return inetAddressStream(
                interfacePredicate,
                (ni, ia) -> ia.isLoopbackAddress() && ia instanceof Inet6Address
        );
    }

    public static <R> Optional<R> applyFirstLoopbackAddressIPv4(
            final Function<? super InetAddress, ? extends R> function)
            throws SocketException {
        return loopbackAddressStreamIPv4(ni -> true).findFirst().map(function);
    }

    public static <R> Optional<R> applyFirstLoopbackAddressIPv6(
            final Function<? super InetAddress, ? extends R> function)
            throws SocketException {
        return loopbackAddressStreamIPv6(ni -> true).findFirst().map(function);
    }

    private JavaNetUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
