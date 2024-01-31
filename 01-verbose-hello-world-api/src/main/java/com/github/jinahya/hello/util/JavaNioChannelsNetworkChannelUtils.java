package com.github.jinahya.hello.util;

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
