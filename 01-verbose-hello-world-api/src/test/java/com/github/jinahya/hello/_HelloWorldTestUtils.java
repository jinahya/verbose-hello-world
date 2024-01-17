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
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import java.time.Duration;

@Slf4j
@SuppressWarnings({
        "java:S101"
})
public final class _HelloWorldTestUtils {

    // ---------------------------------------------------------------------------------------------
    public static void await(final Duration duration) {
        log.debug("awaiting for {}...", duration);
        Awaitility.await()
                .timeout(duration.plusMillis(1L))
                .pollDelay(duration)
                .untilAsserted(() -> Assertions.assertTrue(true));
    }

    public static void awaitForOneSecond() {
        await(Duration.ofSeconds(1L));
    }

    // ---------------------------------------------------------------------------------------------

    private _HelloWorldTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
