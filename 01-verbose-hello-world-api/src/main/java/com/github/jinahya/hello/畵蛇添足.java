package com.github.jinahya.hello;

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

/**
 * A marker annotation for superfluous codes.
 *
 * @author Jin Kwon &lt;onacit_at_wemakeprice.com&gt;
 * @see <a href="https://zh.wiktionary.org/wiki/%E7%94%BB%E8%9B%87%E6%B7%BB%E8%B6%B3">画蛇添足</a>
 * @see <a href="https://en.wiktionary.org/wiki/%EC%82%AC%EC%A1%B1">사족</a>
 */
public @interface 畵蛇添足 {

    String value() default "";
}
