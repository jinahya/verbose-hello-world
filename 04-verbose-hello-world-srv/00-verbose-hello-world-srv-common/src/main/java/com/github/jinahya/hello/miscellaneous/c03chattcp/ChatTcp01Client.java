package com.github.jinahya.hello.miscellaneous.c03chattcp;

import com.github.jinahya.hello.miscellaneous.ChatTcpConstants;
import com.github.jinahya.hello.miscellaneous.Message;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
class ChatTcp01Client {

    private record Receiver(Socket client) implements Runnable {

        private Receiver {
            Objects.requireNonNull(client, "client is null");
        }

        @Override
        public void run() {
            var message = new Message();
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    message.read(client.getInputStream());
                } catch (IOException ioe) {
                    break;
                }
                String address;
                try {
                    address = message.getAddress().toString();
                } catch (UnknownHostException uhe) {
                    address = "unknown";
                }
                System.out.printf("[C] %1$s: %2$s%n", address, message.getCotentAsString());
            }
            try {
                client.close();
            } catch (IOException ioe) {
                log.error("[C] failed to close", ioe);
            }
        }
    }

    private record Sender(Socket client) implements Runnable {

        private Sender {
            Objects.requireNonNull(client, "client is null");
        }

        @Override
        public void run() {
            var message = new Message();
            var reader = new BufferedReader(new InputStreamReader(System.in));
            for (String line; !Thread.currentThread().isInterrupted(); ) {
                try {
                    line = reader.readLine();
                } catch (InterruptedIOException iioe) {
                    break;
                } catch (IOException ioe) {
                    log.error("[C] failed to read line", ioe);
                    break;
                }
                if (line == null) {
                    log.error("[C] null read");
                    break;
                }
                if (line.strip().equalsIgnoreCase("quit!")) {
                    break;
                }
                message.setContentAsString(line);
                try {
                    message.write(client.getOutputStream());
                    client.getOutputStream().flush();
                } catch (IOException ioe) {
                    if (!client.isClosed()) {
                        log.error("failed to write message", ioe);
                    }
                    break;
                }
            }
            try {
                client.close();
            } catch (IOException ioe) {
                log.error("[C] failed to close", ioe);
            }
        }
    }

    public static void main(String... args) throws Exception {
        InetAddress addr;
        try {
            addr = InetAddress.getByName(args[0]);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            addr = InetAddress.getLocalHost();
        }
        var executor = Executors.newFixedThreadPool(2);
        try (var client = new Socket()) {
            client.connect(new InetSocketAddress(addr, ChatTcpConstants.PORT));
            log.debug("[S] connected to {}", client.getRemoteSocketAddress());
            executor.submit(new Sender(client));
            executor.submit(new Receiver(client));
            executor.shutdown();
            while (!executor.awaitTermination(8L, TimeUnit.SECONDS)) {
                // empty
            }
        }
    }
}
