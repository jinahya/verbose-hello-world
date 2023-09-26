package com.github.jinahya.hello.misc.c01rfc863;

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

import com.github.jinahya.hello.misc._Rfc86_Constants;
import com.github.jinahya.hello.misc._Rfc86_Utils;
import com.github.jinahya.hello.util.LoggingUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc863Tcp1Client {

    public static void main(final String... args) throws Exception {
        if (args.length > 0) {
            LoggingUtils.setLevelForAllLoggers(args[0]);
        }
        try (var client = new Socket()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc86_Constants.HOST, 0));
                log.info("(optionally) bound to {}", client.getLocalSocketAddress());
            }
            client.connect(_Rfc863Constants.ADDR, (int) _Rfc86_Constants.CONNECT_TIMEOUT_IN_MILLIS);
            _Rfc86_Utils.logConnected(client);
            final var digest = _Rfc863Utils.newDigest();
            var bytes = _Rfc863Utils.logClientBytes(_Rfc86_Utils.randomBytes());
            final var array = _Rfc86_Utils.newArray();
            while (bytes > 0) {
                ThreadLocalRandom.current().nextBytes(array);
                final var len = Math.min(array.length, bytes);
                client.getOutputStream().write(array, 0, len);
                digest.update(array, 0, len);
                bytes -= len;
            }
            client.getOutputStream().flush();
            _Rfc863Utils.logDigest(digest);
        }
    }

    private Rfc863Tcp1Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
