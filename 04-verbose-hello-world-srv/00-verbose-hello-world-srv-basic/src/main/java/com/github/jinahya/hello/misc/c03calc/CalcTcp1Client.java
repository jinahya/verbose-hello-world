package com.github.jinahya.hello.misc.c03calc;

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

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Slf4j
class CalcTcp1Client extends _CalcTcp {

    public static void main(final String... args) throws Exception {
        try (var executor = newExecutorForClient("tcp-1-client-")) {
            IntStream.range(0, CLIENT_COUNT).mapToObj(i -> executor.submit(() -> {
                try (var client = new Socket()) {
                    // -------------------------------------------------------------- bind(optional)
                    if (ThreadLocalRandom.current().nextBoolean()) {
                        client.bind(new InetSocketAddress(HOST, 0));
                    }
                    // --------------------------------------------------------------------- connect
                    client.connect(ADDR);
                    // ----------------------------------------------------------------------- write
                    final var array = __CalcMessage2.newRandomizedArray();
                    client.getOutputStream().write(array, 0, __CalcMessage2.INDEX_RESULT);
                    client.getOutputStream().flush();
                    // ------------------------------------------------------------------------ read
                    log.debug("reading...");
                    client.setSoTimeout((int) TimeUnit.SECONDS.toMillis(1L));
                    for (var j = __CalcMessage2.LENGTH_RESULT; j > 0; ) {
                        final var r = client.getInputStream().readNBytes(
                                array,
                                __CalcMessage2.INDEX_RESULT -j,
                                j
                        );
                        log.debug("r: {}", r);
                        if (r == -1) {
                            log.error("premature eof");
                            return;
                        }
                        assert r > 0;
                        j -= r;
                    }
                    // ------------------------------------------------------------------------- log
                    __CalcMessage2.log(array);
                } catch (final Exception e) {
                    log.error("failed to request", e);
                }
            })).forEach(f -> {
            });
            // ------------------------------------------------- shutdown-executor/await-termination
            executor.shutdown();
            if (!executor.awaitTermination(1L, TimeUnit.MINUTES)) {
                log.error("executor not terminated");
            }
        }
    }
}
