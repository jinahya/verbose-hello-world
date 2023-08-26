package com.github.jinahya.hello.miscellaneous.c03chat;

import com.github.jinahya.hello.HelloWorldServerUtils;
import com.github.jinahya.hello.util.HelloWorldLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
class ChatTcp1Server {

    private record Receiver(Socket client, Queue<? super byte[]> queue) implements Runnable {

        private Receiver {
            Objects.requireNonNull(client, "client is null");
            Objects.requireNonNull(queue, "queue is null");
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] array;
                try {
                    array = client.getInputStream().readNBytes(_ChatMessage.BYTES);
                } catch (IOException ioe) {
                    if (!client.isClosed()) {
                        log.error("failed to read", ioe);
                    }
                    Thread.currentThread().interrupt();
                    continue;
                }
                assert array.length == _ChatMessage.BYTES;
                if (!queue.offer(array)) {
                    log.error("failed to offer");
                }
            }
            try {
                client.close();
            } catch (IOException ioe) {
                log.error("failed to close {}", client, ioe);
            }
        }
    }

    private record Sender(BlockingQueue<? extends byte[]> queue,
                          Collection<? extends Socket> clients)
            implements Runnable {

        private Sender {
            Objects.requireNonNull(queue, "queue is null");
            Objects.requireNonNull(clients, "clients is null");
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
//                log.debug("clients: {}, {}", clients.size(), clients);
                byte[] array;
                try {
                    if ((array = queue.poll(8L, TimeUnit.SECONDS)) == null) {
                        continue;
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    continue;
                }
                assert array.length == _ChatMessage.BYTES;
                for (var client : clients) {
                    try {
                        client.getOutputStream().write(array);
                        client.getOutputStream().flush();
                    } catch (IOException ioe) {
                        if (!client.isClosed()) {
                            log.error("failed to send to {}", client, ioe);
                        }
                        try {
                            client.close();
                        } catch (IOException ioe2) {
                            log.error("failed to close {}", client, ioe2);
                        }
                        clients.remove(client);
                    }
                }
            }
        }
    }

    public static void main(String... args) throws Exception {
        var executor = Executors.newCachedThreadPool();
        var clients = new CopyOnWriteArrayList<Socket>();
        var queue = new ArrayBlockingQueue<byte[]>(1024);
        var writer = executor.submit(new Sender(queue, clients));
        try (var server = new ServerSocket()) {
            server.bind(new InetSocketAddress(
                    InetAddress.getByName("0.0.0.0"), _ChatConstants.PORT
            ));
            log.info("bound on {}", server.getLocalSocketAddress());
            HelloWorldLangUtils.readLinesAndCallWhenTests(
                    HelloWorldServerUtils::isQuit, // <predicate>
                    () -> {                        // <callable>
                        server.close();
                        return null;
                    },
                    l -> {                        // <consumer>
                        // does nothing
                    }
            );
            while (!server.isClosed()) {
                try {
                    var client = server.accept();
                    log.info("accepted from {} through {}", client.getRemoteSocketAddress(),
                             client.getLocalSocketAddress());
                    clients.add(client);
                    executor.submit(new Receiver(client, queue));
                } catch (IOException ioe) {
                    if (!server.isClosed()) {
                        log.error("failed to accept", ioe);
                    }
                }
            }
        }
        for (var client : clients) {
            try {
                client.close();
            } catch (IOException ioe) {
                log.error("failed to close " + client, ioe);
            }
        }
        if (!writer.cancel(true)) {
            log.error("writer is not canceled");
        }
        executor.shutdown();
        if (!executor.awaitTermination(8L, TimeUnit.SECONDS)) {
            log.error("executor has not been terminated");
        }
    }
}
