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

import java.io.IOException;
import java.net.DatagramSocket;

@Slf4j
class CalcUdp1Client {

    public static void main(final String... args)
            throws IOException {
        try (var client = new DatagramSocket(null)) {
            for (var c = 0; c < _CalcConstants.TOTAL_REQUESTS; c++) {
                _CalcMessage.newInstanceForClient()
                        .sendRequest(client)
                        .receiveResult(client)
                        .log();
            }
        }
    }

    private CalcUdp1Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
