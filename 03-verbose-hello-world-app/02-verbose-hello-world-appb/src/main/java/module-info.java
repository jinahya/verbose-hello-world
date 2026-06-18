/*-
 * #%L
 * verbose-hello-world-appb
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
 * The Verbose Hello World Appb — obtains a
 * {@link com.github.jinahya.hello.api.HelloWorld HelloWorld} through
 * {@link java.util.ServiceLoader ServiceLoader} and writes {@code hello, world} to
 * {@link java.lang.System#out System.out} via a
 * {@link java.nio.channels.WritableByteChannel WritableByteChannel}.
 */
module com.github.jinahya.hello.appb {
    requires com.github.jinahya.hello.api;
    // no `uses` directive: this module discovers its HelloWorld provider through the
    // classpath SPI mechanism (META-INF/services), not the module-path one.
}
