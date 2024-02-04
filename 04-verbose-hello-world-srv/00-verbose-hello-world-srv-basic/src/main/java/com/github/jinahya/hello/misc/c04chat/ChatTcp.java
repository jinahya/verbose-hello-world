package com.github.jinahya.hello.misc.c04chat;

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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S101"
})
abstract class ChatTcp extends Chat {

    // -------------------------------------------------------------------------------------- SERVER
    static final int SERVER_BACKLOG = 50;

    // -------------------------------------------------------------------------------------- CLIENT
    static final long CONNECT_TIMEOUT = 1L;

    static final TimeUnit CONNECT_TIMEOUT_UNIT = TimeUnit.SECONDS;

    static final long CONNECT_TIMEOUT_MILLIS = CONNECT_TIMEOUT_UNIT.toMillis(CONNECT_TIMEOUT);
}