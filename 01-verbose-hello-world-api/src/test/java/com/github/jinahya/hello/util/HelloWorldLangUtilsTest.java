package com.github.jinahya.hello.util;

/*-
 * #%L
 * verbose-hello-world-api
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.github.jinahya.hello.util.HelloWorldLangUtils.startStopWatch1;
import static com.github.jinahya.hello.util.HelloWorldLangUtils.stopStopWatch1;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class HelloWorldLangUtilsTest {

    @Nested
    class OfIntTest {

    }

    @DisplayName("trim(string, charset, length)")
    @Nested
    class TrimByCodepointsTest {

        // https://tatoeba.org/ko/
        @ValueSource(strings = {
                "",
                " ",
                "   ",
                "У нього було сиве волосся.",
                "Trovu, kiu vizitas vian profilon.",
                "There is no public toilet.",
                "Ur zmireɣ ad d-sɣeɣ tasewlaft yelhan am tin-nnem.",
                "La réunion s'est tenue hier.",
                "私はあなたのご両親にかなり気に入られているようです。",
                "汝英語會話係啵？",
                "어떻게 그의 아버지가 저 남자의 정체를 간파한 것인지 알 수 없어요."
        })
        @ParameterizedTest
        void __(String string) {
            var charset = StandardCharsets.UTF_8;
            var previous = string;
            for (int bytes = previous.getBytes(charset).length + 1; bytes > 0;
                 bytes--) {
                var trimmed = HelloWorldLangUtils.trimByCodepoints(string,
                                                                   charset,
                                                                   bytes);
                assertTrue(trimmed.getBytes(charset).length <= bytes);
                assertTrue(previous.startsWith(trimmed));
                previous = trimmed;
            }
        }
    }

    @DisplayName("Stopwatch1")
    @Nested
    class StopWatch1Test {

        @Test
        void __() throws Exception {
            var count = 128;
            var futures = new ArrayList<Future<?>>(count);
            final var executor = Executors.newFixedThreadPool(count);
            for (int i = 0; i < count; i++) {
                futures.add(executor.submit(() -> {
                    startStopWatch1();
                    var sleep = Duration.ofMillis(current().nextLong(1024));
                    await().pollDelay(sleep).until(() -> true);
                    var elapsed = stopStopWatch1();
                    assertTrue(elapsed.compareTo(sleep) >= 0);
                }));
            }
            executor.shutdown();
            var terminated = executor.awaitTermination(16L, TimeUnit.SECONDS);
            assert terminated;
            for (var future : futures) {
                future.get();
            }
        }
    }
}
