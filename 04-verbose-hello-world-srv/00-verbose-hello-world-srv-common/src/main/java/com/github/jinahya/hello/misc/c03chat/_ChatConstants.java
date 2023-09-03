package com.github.jinahya.hello.misc.c03chat;

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

import java.util.Optional;

@Slf4j
final class _ChatConstants {

    static final int PORT = 7 + 40000;

    static final String USER_NAME =
            Optional.ofNullable(System.getProperty("user.name"))
                    .orElse("unknown");

    private _ChatConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
