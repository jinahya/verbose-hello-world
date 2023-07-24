package com.github.jinahya.hello.miscellaneous.rfc862;

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

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
class Rfc862Udp2Client {

    public static void main(String... args) throws IOException, InterruptedException {
        var host = InetAddress.getLoopbackAddress();
        var endpoint = new InetSocketAddress(host, Rfc862Udp2Server.PORT);
        var executor = Executors.newCachedThreadPool();
        for (int i = 0; i < 4; i++) {
            executor.submit(() -> {
                Rfc862Udp1Client.sendAndReceive(endpoint);
                return null;
            });
        }
        executor.shutdown();
        {
            var timeout = 4L;
            var unit = TimeUnit.SECONDS;
            if (!executor.awaitTermination(timeout, unit)) {
                log.error("executor not terminated in {} {}", timeout, unit);
            }
        }
    }

    private Rfc862Udp2Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
