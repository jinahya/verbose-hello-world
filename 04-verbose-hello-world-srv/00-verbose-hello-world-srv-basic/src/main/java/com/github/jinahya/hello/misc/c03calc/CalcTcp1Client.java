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

@Slf4j
class CalcTcp1Client extends _CalcTcp {

    public static void main(final String... args) throws Exception {
        try (var client = new Socket()) {
            // ---------------------------------------------------------------------- bind(optional)
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(HOST, 0));
                logBound(client);
            }
            // ----------------------------------------------------------------------------- connect
            client.connect(ADDR);
            logConnected(client);
            // ------------------------------------------------------------------------------- write
            final var array = new byte[__CalcMessage2.BYTES];
            __CalcMessage2.setOperator(array, _CalcOperator.randomValue());
            __CalcMessage2.setOperand1(array, ThreadLocalRandom.current().nextInt());
            __CalcMessage2.setOperand2(array, ThreadLocalRandom.current().nextInt());
            client.getOutputStream().write(array, 0, __CalcMessage2.INDEX_RESULT);
            client.getOutputStream().flush();
            // -------------------------------------------------------------------------------- read
            client.getInputStream().readNBytes(array, 0, array.length);
            // --------------------------------------------------------------------------------- log
            __CalcMessage2.log(array);
        }
    }
}
