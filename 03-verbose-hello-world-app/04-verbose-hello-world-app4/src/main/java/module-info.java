/*-
 * #%L
 * verbose-hello-world-app4
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
 * The Verbose Hello World App4 — wires {@link com.github.jinahya.hello.api.HelloWorld HelloWorld}
 * via Jakarta CDI (Weld SE) and prints {@code hello, world} through a
 * {@link com.github.jinahya.hello.api.HelloWorldArrayPublisher HelloWorldArrayPublisher}
 * (Reactive-Streams). Declared an {@code open module} because the CDI container reflects over the
 * {@code @Inject} / {@code @Produces} / {@code @Disposes} members of this module.
 */
open module com.github.jinahya.hello.app4 {
    requires com.github.jinahya.hello.api;
    requires jakarta.cdi;
    requires jakarta.inject;
    requires java.logging;
    uses com.github.jinahya.hello.api.HelloWorld;
}
