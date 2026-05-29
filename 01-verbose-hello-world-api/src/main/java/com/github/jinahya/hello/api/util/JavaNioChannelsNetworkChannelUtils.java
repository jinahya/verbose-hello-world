package com.github.jinahya.hello.api.util;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2024 Jinahya, Inc.
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

import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.util.*;

/**
 * Helpers for {@link NetworkChannel java.nio.channels.NetworkChannel} — option-set helpers that
 * fail-fast against an unsupported {@link SocketOption} rather than passing the call straight
 * through to the channel.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JavaNioChannelsNetworkChannelUtils {

    /**
     * Tests whether {@code option} appears in {@code channel.supportedOptions()}.
     *
     * @param channel the network channel to query; must not be {@code null}.
     * @param option  the socket option to test; must not be {@code null}.
     * @return {@code true} if {@code channel} supports {@code option}; {@code false} otherwise.
     * @throws NullPointerException if either argument is {@code null}.
     */
    public static boolean isOptionSupported(final NetworkChannel channel,
                                            final SocketOption<?> option) {
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(option, "option is null");
        return channel.supportedOptions().contains(option);
    }

    /**
     * Sets {@code option} to {@code value} on {@code channel} — but first checks
     * {@link #isOptionSupported(NetworkChannel, SocketOption)} and throws
     * {@link UnsupportedOperationException} with a more descriptive message than the JDK's
     * default if the option isn't supported.
     *
     * @param channel the network channel to configure; must not be {@code null}.
     * @param option  the socket option to set; must not be {@code null}.
     * @param value   the value to set; may be {@code null} (and may be rejected by the channel).
     * @param <T>     the concrete {@link NetworkChannel} subtype.
     * @param <U>     the option's value type.
     * @return the given {@code channel} for chaining; never {@code null}.
     * @throws NullPointerException          if {@code channel} or {@code option} is {@code null}.
     * @throws UnsupportedOperationException if {@code channel} does not support {@code option}.
     * @throws IOException                   if {@link NetworkChannel#setOption(SocketOption, Object)
     *                                       channel.setOption(option, value)} throws.
     */
    @SuppressWarnings({"unchecked"})
    public static <T extends NetworkChannel, U> T setOption(final T channel,
                                                            final SocketOption<U> option,
                                                            final U value)
            throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(option, "option is null");
        if (!isOptionSupported(channel, option)) {
            throw new UnsupportedOperationException(option + " is not supported with " + channel);
        }
        return (T) channel.setOption(option, value);
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private JavaNioChannelsNetworkChannelUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
