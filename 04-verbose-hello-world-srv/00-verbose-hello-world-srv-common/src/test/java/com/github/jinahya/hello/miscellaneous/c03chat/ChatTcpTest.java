package com.github.jinahya.hello.miscellaneous.c03chat;

/*-
 * #%L
 * verbose-hello-world-srv-common
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
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

import com.github.jinahya.hello.HelloWorldServerConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
class ChatTcpTest {

    private static final List<Class<?>> SERVER_CLASSES = List.of(
            ChatTcp1Server.class,
            ChatTcp2Server.class
    );

    private static List<Class<?>> getServerClasses() {
        return SERVER_CLASSES;
    }

    private static final List<Class<?>> CLIENT_CLASSES = List.of(
            ChatTcp1Client.class,
            ChatTcp2Client.class
    );

    @Disabled
    @MethodSource({"getServerClasses"})
    @ParameterizedTest
    void __(Class<?> serverClass) throws Exception {
        log.debug(".: {}", new File(".").getCanonicalFile());
        log.debug("server: {}", serverClass.getSimpleName());
        log.debug("server.name: {}", serverClass.getName());
        var classPath = Paths.get("target", "classes").normalize().toFile().getCanonicalPath();
        log.debug("classPath: {}", classPath);
        var serverProcess = new ProcessBuilder()
                .inheritIO()
                .directory(new File(new File(".").getCanonicalFile(), "target"))
                .command("java", "-ea", "-cp", classPath, serverClass.getName())
                .start();
        log.debug("info: {}", serverProcess.info());
        Thread.sleep(100L);
        var clientProcesses = new ArrayList<>();
        var generator = new RandomStringGenerator.Builder().build();
        for (var clientClass : CLIENT_CLASSES) {
            log.debug("client: {}", clientClass.getSimpleName());
            log.debug("client: {}", new File(".").getCanonicalFile());
            var clientProcess = new ProcessBuilder()
                    .directory(new File("target"))
//                    .command("java", "-ea", "-cp", ".", clientClass.getName())
                    .command("java", "-ea", clientClass.getName())
                    .inheritIO()
                    .start();
            clientProcesses.add(clientProcess);
            Thread.sleep(100L);
            var thread = new Thread(() -> {
                var writer = clientProcess.outputWriter();
                for (int i = 0; i < 1; i++) {
                    var string = generator.generate(ThreadLocalRandom.current().nextInt(1024));
                    try {
                        writer.write(string);
                        writer.newLine();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    try {
                        Thread.sleep(TimeUnit.MILLISECONDS.toMillis(10L));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
                try {
                    writer.write(HelloWorldServerConstants.QUIT);
                    writer.newLine();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            });
            thread.start();
            thread.join();
        }
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(4L));
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        var serverExitValue = serverProcess.waitFor();
    }
}
