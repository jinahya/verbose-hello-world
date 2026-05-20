/*-
 * #%L
 * verbose-hello-world-api
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
module com.github.jinahya.hello.api {
    requires transitive org.jspecify;
    requires transitive org.slf4j;
//    requires jakarta.validation;
    requires org.reactivestreams;
    requires static jdk.httpserver;
    requires static java.net.http;
    requires java.sql;
    requires java.sql.rowset;
    requires java.desktop;
//    requires org.apache.commons.lang3;
    exports com.github.jinahya.hello.api;
}
