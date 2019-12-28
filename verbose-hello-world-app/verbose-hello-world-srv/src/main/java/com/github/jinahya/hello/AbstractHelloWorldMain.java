package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

@Slf4j
abstract class AbstractHelloWorldMain {

    // -----------------------------------------------------------------------------------------------------------------


    /**
     * Starts a new thread which reads lines from {@link System#in} and closes specified server socket for "{@code
     * quit}".
     *
     * @param server the server socket to close.
     */
    static void readAndClose(final ServerSocket server) {
        if (server == null) {
            throw new NullPointerException("server is null");
        }
        final Thread thread = new Thread(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                try {
                    for (String line; (line = reader.readLine()) != null; ) {
                        if ("quit".equalsIgnoreCase(line)) {
                            break;
                        }
                    }
                } finally {
                    server.close();
                }
            } catch (final IOException ioe) {
                log.error("failed to read and close", ioe);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Starts a new thread which connects to {@link InetAddress#getLocalHost() localhost} with specified port number and
     * reads {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes and prints it as a {@link
     * StandardCharsets#US_ASCII US-ASCII} string followed by a new line character.
     *
     * @param port the local port number to connect.
     */
    static void connectAndPrint(final int port) {
        final Thread thread = new Thread(() -> {
            try {
                try (Socket client = new Socket()) {
                    final SocketAddress endpoint = new InetSocketAddress(InetAddress.getLocalHost(), port);
                    client.connect(endpoint, 8192);
                    final byte[] array = new byte[HelloWorld.BYTES];
                    new DataInputStream(client.getInputStream()).readFully(array);
                    System.out.printf("%s%n", new String(array, StandardCharsets.US_ASCII));
                }
            } catch (final IOException ioe) {
                log.error("failed to connect and print", ioe);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}
