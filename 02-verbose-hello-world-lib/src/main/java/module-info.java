/*-
 * #%L
 * verbose-hello-world-lib
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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
/**
 * The Verbose Hello World Lib — a concrete implementation of
 * {@link com.github.jinahya.hello.api.HelloWorld HelloWorld}
 * ({@link com.github.jinahya.hello.lib.HelloWorldImpl HelloWorldImpl}) layered on top of
 * {@code com.github.jinahya.hello.api} and discoverable via
 * {@link java.util.ServiceLoader ServiceLoader}.
 */
module com.github.jinahya.hello.lib {
    requires transitive com.github.jinahya.hello.api;
    exports com.github.jinahya.hello.lib;
    provides com.github.jinahya.hello.api.HelloWorld
            with com.github.jinahya.hello.lib.HelloWorldImpl;
}
