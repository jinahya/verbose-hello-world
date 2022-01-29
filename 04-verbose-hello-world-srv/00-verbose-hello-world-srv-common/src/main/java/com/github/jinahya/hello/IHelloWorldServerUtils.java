package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-srv
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
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
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * A utility class for Hello World servers.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
final class IHelloWorldServerUtils {

    static boolean isLocal(InetAddress address) {
        return true;
    }

    /**
     * Parses specified command line arguments and returns a socket address to
     * bind.
     *
     * @param args the command line arguments.
     * @return a socket address to bind.
     * @throws UnknownHostException if the {@code host} part is unknown.
     */
    static InetSocketAddress parseEndpointToBind(final String[] args)
            throws UnknownHostException {
        Objects.requireNonNull(args, "args is null");
//        int port = 0;
//        InetAddress host = InetAddress.getByName("0.0.0.0");
//        if (args.length > 0) {
//            try {
//                port = Integer.parseInt(args[0]);
//                if (port < 0 || port > 65535) {
//                    throw new IllegalArgumentException("illegal port number: " + port);
//                }
//            } catch (final NumberFormatException nfe) {
//                throw new IllegalArgumentException("unable to parse port number from " + args[0]);
//            }
//            if (args.length > 1) {
//                try {
//                    host = InetAddress.getByName(args[1]);
//                    host.loca
//                    if (host.isAnyLocalAddress()) {
//                    }
//                } catch (final UnknownHostException uhe) {
//                    log.error("unable to parse binding address from " + args[1]);
//                }
//            }
//        }
        final Options options = new Options();
        options.addOption(Option.builder("h")
                                  .longOpt("host")
                                  .desc("local host to bind")
                                  .type(String.class)
                                  .required(false)
                                  .build());
        options.addOption(Option.builder("p")
                                  .longOpt("port")
                                  .desc("local port to bind")
                                  .type(int.class)
                                  .required(false)
                                  .build());
        try {
            final CommandLine values = new DefaultParser().parse(options, args);
            final String host = Optional.ofNullable(values.getOptionValue("h"))
                    .orElse("0.0.0.0");
            final int port = Integer.parseInt(
                    Optional.ofNullable(values.getOptionValue("p"))
                            .orElse("0"));
            return new InetSocketAddress(InetAddress.getByName(host), port);
        } catch (final ParseException pe) {
            throw new RuntimeException("failed to parse args", pe);
        }
    }

    /**
     * Starts a new {@link Thread#setDaemon(boolean) daemon} thread which reads
     * '{@code quit\n}' from {@link System#in} and {@link Closeable#close()
     * closes} specified server instance.
     *
     * @param server the server to close.
     */
    static void readQuitToClose(final IHelloWorldServer server) {
        if (server == null) {
            throw new NullPointerException("server is null");
        }
        final Thread thread = new Thread(() -> {
            final BufferedReader reader
                    = new BufferedReader(new InputStreamReader(System.in));
            try {
                for (String line; (line = reader.readLine()) != null; ) {
                    if (line.trim().equalsIgnoreCase("quit")) {
                        break;
                    }
                }
                server.close();
            } catch (final IOException ioe) {
                log.error("failed to read 'quit' and/or close the server", ioe);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Invokes {@link Callable#call()} method on specified callable and writes
     * '{@code quit\n}' to a pipe connected to {@link System#in}.
     *
     * @param callable the callable to call.
     * @throws IOException if an I/O error occurs.
     */
    static void writeQuitToClose(final Callable<Void> callable)
            throws IOException {
        Objects.requireNonNull(callable, "callable is null");
        final InputStream in = System.in;
        try {
            final PipedOutputStream pos = new PipedOutputStream();
            System.setIn(new PipedInputStream(pos));
            try {
                callable.call();
            } catch (final Exception e) {
                log.debug("failed to call {}", callable, e);
            }
            pos.write("quit\n".getBytes(StandardCharsets.US_ASCII));
            pos.flush();
        } finally {
            System.setIn(in);
        }
    }

    private IHelloWorldServerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
