/*-
 * #%L
 * verbose-hello-world-app1
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
 * The Verbose Hello World App1 — directly instantiates
 * {@link com.github.jinahya.hello.lib.HelloWorldImpl} and writes {@code hello, world} to
 * {@link java.lang.System#out System.out}.
 */
module com.github.jinahya.hello.app1 {
    requires com.github.jinahya.hello.api;
    requires com.github.jinahya.hello.lib;
}
