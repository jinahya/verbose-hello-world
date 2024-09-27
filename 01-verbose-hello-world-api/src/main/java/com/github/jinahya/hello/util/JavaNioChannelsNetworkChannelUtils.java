package com.github.jinahya.hello.util;

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

import java.io.IOException;
import java.net.SocketOption;
import java.nio.channels.NetworkChannel;
import java.util.Objects;

/**
 * Utilities for {@link NetworkChannel} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JavaNioChannelsNetworkChannelUtils {

    public static boolean isOptionSupported(final NetworkChannel channel,
                                            final SocketOption<?> option) {
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(option, "option is null");
        return channel.supportedOptions().contains(option);
    }

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
