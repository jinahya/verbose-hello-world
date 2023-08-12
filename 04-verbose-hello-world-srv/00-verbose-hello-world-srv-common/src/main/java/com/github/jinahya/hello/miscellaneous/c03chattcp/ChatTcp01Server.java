package com.github.jinahya.hello.miscellaneous.c03chattcp;

import com.github.jinahya.hello.miscellaneous.ChatTcpConstants;
import com.github.jinahya.hello.miscellaneous.Message;
import com.github.jinahya.hello.util.HelloWorldLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
class ChatTcp01Server {

    private record Reader(Collection<? super Socket> clients, Socket client,
                          BlockingQueue<? super Message> queue)
            implements Runnable {

        private Reader {
            Objects.requireNonNull(client, "client is null");
            Objects.requireNonNull(queue, "queue is null");
        }

        @Override
        public void run() {
            var message = new Message();
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    message.read(client.getInputStream());
                    message.setAddress((InetSocketAddress) client.getRemoteSocketAddress());
                } catch (IOException ioe) {
                    break;
                }
                try {
                    if (!queue.offer(message, 8L, TimeUnit.SECONDS)) {
                        log.error("[C] failed to offer message to the queue");
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
            try {
                client.close();
            } catch (IOException ioe) {
                log.error("[C] failed to close client", ioe);
            }
            clients.remove(client);
        }
    }

    private record Writer(BlockingQueue<? extends Message> queue,
                          Iterable<? extends Socket> clients)
            implements Runnable {

        private Writer {
            Objects.requireNonNull(queue, "queue is null");
            Objects.requireNonNull(clients, "clients is null");
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                Message message;
                try {
                    message = queue.poll(8L, TimeUnit.SECONDS);
                    if (message == null) {
                        log.debug("[S] clients.size: {}", ((Collection<?>) clients).size());
                        continue;
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    continue;
                }
                for (var i = clients.iterator(); i.hasNext(); ) {
                    var client = i.next();
//                    if (client.isClosed()) {
//                        i.remove();
//                        continue;
//                    }
                    try {
                        message.write(client.getOutputStream());
                        client.getOutputStream().flush();
                    } catch (IOException ioe) {
                        if (!client.isClosed()) {
                            log.error("[S] failed to write message", ioe);
                            try {
                                client.close();
                            } catch (IOException ioe2) {
                                log.error("[S] failed to close", ioe2);
                            }
                        }
                        i.remove();
                    }
                }
            }
        }
    }

    public static void main(String... args) throws Exception {
        var executor = Executors.newCachedThreadPool();
        var clients = new CopyOnWriteArrayList<Socket>();
        var queue = new ArrayBlockingQueue<Message>(1024);
        var writer = executor.submit(new Writer(queue, clients));
        try (var server = new ServerSocket()) {
            server.bind(new InetSocketAddress(InetAddress.getLocalHost(),
                                              ChatTcpConstants.PORT));
            log.debug("[S] bound on {}", server.getLocalSocketAddress());
            HelloWorldLangUtils.readQuitAndClose(server);
            while (!server.isClosed()) {
                try {
                    var client = server.accept();
                    log.debug("[S] accepted from {}", client.getRemoteSocketAddress());
                    clients.add(client);
                    executor.submit(new Reader(clients, client, queue));
                } catch (IOException ioe) {
                    if (server.isClosed()) {
                        continue;
                    }
                    log.error("[S] failed to accept", ioe);
                }
            }
        }
        for (var client : clients) {
            try {
                client.close();
            } catch (IOException ioe) {
                log.error("[S] failed to close " + client, ioe);
            }
        }
        var canceled = writer.cancel(true);
        if (!canceled) {
            log.error("[S] writer not canceled");
        }
        executor.shutdown();
        if (!executor.awaitTermination(8L, TimeUnit.SECONDS)) {
            log.error("[S] executor not terminated");
        }
    }
}
