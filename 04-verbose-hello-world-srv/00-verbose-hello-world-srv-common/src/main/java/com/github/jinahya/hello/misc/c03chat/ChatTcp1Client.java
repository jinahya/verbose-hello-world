package com.github.jinahya.hello.misc.c03chat;

import com.github.jinahya.hello.util.HelloWorldServerUtils;
import com.github.jinahya.hello.util.HelloWorldLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
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
                    array = client.getInputStream()
                            .readNBytes(_ChatMessage.BYTES);
                } catch (IOException ioe) {
                    Thread.currentThread().interrupt();
                    continue;
                }
                if (array.length != _ChatMessage.BYTES) {
                    Thread.currentThread().interrupt();
                    continue;
                }
                _ChatMessage.OfArray.printToSystemOut(array);
            }
            try {
                client.close();
            } catch (IOException ioe) {
                log.error("failed to close", ioe);
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
            var current = Thread.currentThread();
            HelloWorldLangUtils.readLinesAndCallWhenTests(
                    HelloWorldServerUtils::isQuit, // <predicate>
                    () -> {                        // <callable>
                        current.interrupt();
                        return null;
                    },
                    l -> {                         // <consumer>
                        if (!queue.offer(l)) {
                            log.error("failed to offer");
                        }
                    }
            );
            while (!Thread.currentThread().isInterrupted()) {
                String line;
                try {
                    if ((line = queue.poll(1L, TimeUnit.SECONDS)) == null) {
                        continue;
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    continue;
                }
                var array = _ChatMessage.OfArray.of(
                        _ChatUtils.prependUsername(line));
                try {
                    client.getOutputStream().write(array);
                    client.getOutputStream().flush();
                } catch (IOException ioe) {
                    if (!client.isClosed()) {
                        log.error("failed to send", ioe);
                    }
                    Thread.currentThread().interrupt();
                }
            }
            try {
                client.close();
            } catch (IOException ioe) {
                log.error("failed to close", ioe);
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
            log.info("connected to {} through {}",
                     client.getRemoteSocketAddress(),
                     client.getLocalSocketAddress());
            executor.submit(new Sender(client));
            executor.submit(new Receiver(client));
            for (executor.shutdown();
                 !executor.awaitTermination(8L, TimeUnit.SECONDS); ) {
                // empty
            }
        }
    }
}
