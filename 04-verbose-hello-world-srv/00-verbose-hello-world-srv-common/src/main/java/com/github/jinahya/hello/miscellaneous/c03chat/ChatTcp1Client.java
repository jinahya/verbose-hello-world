package com.github.jinahya.hello.miscellaneous.c03chat;

import com.github.jinahya.hello.HelloWorldServerConstants;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
class ChatTcp1Client {

    private record Receiver(Socket client) implements Runnable {

        private Receiver {
            Objects.requireNonNull(client, "client is null");
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] array;
                try {
                    array = client.getInputStream().readNBytes(_ChatMessage.BYTES);
                } catch (IOException ioe) {
                    Thread.currentThread().interrupt();
                    continue;
                }
                if (array.length != _ChatMessage.BYTES) {
                    Thread.currentThread().interrupt();
                    continue;
                }
                System.out.printf("%1$s%n", _ChatMessage.toString(array));
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
            BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
            var thread = new Thread(() -> {
                var reader = new BufferedReader(new InputStreamReader(System.in));
                for (String line; !Thread.currentThread().isInterrupted(); ) {
                    try {
                        line = reader.readLine();
                    } catch (IOException ioe) {
                        Thread.currentThread().interrupt();
                        continue;
                    }
                    if (line == null) {
                        line = HelloWorldServerConstants.QUIT;
                    }
                    try {
                        if (!queue.offer(line, 8L, TimeUnit.SECONDS)) {
                            log.error("[C] failed to offer to the queue");
                        }
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
            for (String line; !Thread.currentThread().isInterrupted(); ) {
                try {
                    if ((line = queue.poll(1L, TimeUnit.SECONDS)) == null) {
                        if (client.isClosed()) {
                            Thread.currentThread().interrupt();
                        }
                        continue;
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    continue;
                }
                if (line.strip().equalsIgnoreCase(HelloWorldServerConstants.QUIT)) {
                    Thread.currentThread().interrupt();
                    continue;
                }
                var array = _ChatMessage.newArray(line);
                try {
                    client.getOutputStream().write(array);
                    client.getOutputStream().flush();
                } catch (IOException ioe) {
                    if (!client.isClosed()) {
                        log.error("[C] failed to send message", ioe);
                    }
                    Thread.currentThread().interrupt();
                }
            }
            thread.interrupt();
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
            addr = InetAddress.getLoopbackAddress();
        }
        var executor = Executors.newFixedThreadPool(2);
        try (var client = new Socket()) {
            client.connect(new InetSocketAddress(addr, _ChatConstants.PORT));
            log.debug("[S] connected to {} through {}", client.getRemoteSocketAddress(),
                      client.getLocalSocketAddress());
            executor.submit(new Sender(client));
            executor.submit(new Receiver(client));
            for (executor.shutdown(); !executor.awaitTermination(8L, TimeUnit.SECONDS); ) {
                // empty
            }
        }
    }
}
