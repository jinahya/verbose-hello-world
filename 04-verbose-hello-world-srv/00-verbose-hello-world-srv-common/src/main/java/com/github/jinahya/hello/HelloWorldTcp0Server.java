package com.github.jinahya.hello;

import com.github.jinahya.hello.util.HelloWorldServerUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import static com.github.jinahya.hello.HelloWorldServerHelper.loadService;
import static com.github.jinahya.hello.util.HelloWorldLangUtils.readLinesAndCloseWhenTests;

@Slf4j
class HelloWorldTcp0Server {

    private static final int BACKLOG = 50; // default

    public static void main(String... args) throws Exception {
        InetAddress host;
        try {
            host = InetAddress.getByName(args[0]);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            host = InetAddress.getByName("::");
        }
        try (ServerSocket server = new ServerSocket()) {
            server.bind(new InetSocketAddress(host, HelloWorldServerConstants.PORT), BACKLOG);
            log.debug("bound to {}", server.getLocalSocketAddress());
            readLinesAndCloseWhenTests(HelloWorldServerUtils::isQuit, server);
            var service = loadService();
            while (!server.isClosed()) {
                // TODO: accept client
                // TODO: send 'hello, world'
            }
        }
    }

    private HelloWorldTcp0Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
