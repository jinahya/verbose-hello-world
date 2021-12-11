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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.channels.AsynchronousByteChannel;

/**
 * A class for testing {@link HelloWorld#write(AsynchronousByteChannel)} method regarding arguments verification.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_10_Write_AsynchronousByteChannel_Test
 */
@Slf4j
class HelloWorld_10_Write_AsynchronousByteChannel_Arguments_Test extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#write(AsynchronousByteChannel) write(channel)} method throws a {@link
     * NullPointerException} when {@code channel} argument is {@code null}.
     */
    @DisplayName("write((AsynchronousByteChannel) null) throws NullPointerException")
    @Test
    void writeAsync_ThrowNullPointerException_ChannelIsNull() {
        final AsynchronousByteChannel channel = null;
        Assertions.assertThrows(NullPointerException.class, () -> helloWorld().write(channel));
    }
}
