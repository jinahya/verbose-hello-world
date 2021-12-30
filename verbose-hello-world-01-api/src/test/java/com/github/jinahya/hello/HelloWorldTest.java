package com.github.jinahya.hello;

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

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * An abstract class for testing methods defined in {@link HelloWorld}
 * interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@ExtendWith({MockitoExtension.class})
@TestInstance(TestInstance.Lifecycle.PER_METHOD) // default, implicitly.
@Slf4j
abstract class HelloWorldTest {

    /**
     * Stubs that {@link HelloWorld#set(byte[], int)} method returns the {@code
     * array} argument.
     */
    @BeforeEach
    void stub_SetArrayIndex_ReturnArray() {
        Mockito.lenient()                                        // <1>
                .when(helloWorld.set(ArgumentMatchers.any(),     // <2>
                                     ArgumentMatchers.anyInt()))
                .thenAnswer(i -> i.getArgument(0));              // <3>
    }

    @Spy
    @Accessors(fluent = true)
    @Getter
    private HelloWorld helloWorld;

    // for capturing byte[] argument with set(byte[], int) or set(byte[])
    @Captor
    @Accessors(fluent = true)
    @Getter
    private ArgumentCaptor<byte[]> arrayCaptor;

    // for capturing index argument with set(byte[], index)
    @Captor
    @Accessors(fluent = true)
    @Getter
    private ArgumentCaptor<Integer> indexCaptor;

    // for capturing stream argument with write(OutputStream)
    @Captor
    @Accessors(fluent = true)
    @Getter
    private ArgumentCaptor<OutputStream> streamCaptor;

    // for capturing buffer argument with put(ByteBuffer)
    @Captor
    @Accessors(fluent = true)
    @Getter
    private ArgumentCaptor<ByteBuffer> bufferCaptor;

    // for capturing channel argument with write(WritableByteChannel)
    @Captor
    @Accessors(fluent = true)
    @Getter
    private ArgumentCaptor<WritableByteChannel> channelCaptor;
}
