package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-04-srv2
 * %%
 * Copyright (C) 2018 - 2021 Jinahya, Inc.
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
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Slf4j
class HelloWorldMainTcpTest {

    @Test
    void main__() throws IOException {
        IHelloWorldServerUtils.writeQuitToClose(() -> {
            HelloWorldMainTcp.main("0.0.0.0", "0");
            final InetAddress host = InetAddress.getLocalHost();
            final int port = HelloWorldServerTcp.LOCAL_PORT.get();
            final SocketAddress endpoint = new InetSocketAddress(host, port);
            HelloWorldClientTcp.clients(8, endpoint, b -> {
                // TODO: Verify array!
            });
            return null;
        });
    }
}
