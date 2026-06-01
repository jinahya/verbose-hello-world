package com.github.jinahya.hello.api._java_net;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
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

import com.github.jinahya.hello.api.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.net.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.mockito.Mockito.*;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Append_DatagramPacket__Test extends HelloWorld__Test {

    @BeforeEach
    void __stubService() {
        doAnswer(i -> {
            final var packet = i.getArgument(0, DatagramPacket.class);
            final var src = hello_world_byte_array();
            System.arraycopy(src, 0, packet.getData(), packet.getOffset(), src.length);
            return packet;
        }).when(service()).append(ArgumentMatchers.<DatagramPacket>argThat(p -> {
            return p.getOffset() + p.getLength() + HelloWorld.BYTES <= p.getData().length;
        }));
    }
}
