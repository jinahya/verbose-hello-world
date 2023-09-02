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
 * A marker annotation for superfluous test cases.
 *
 * @author Jin Kwon &lt;onacit_at_wemakeprice.com&gt;
 * @see <a
 * href="https://ko.wiktionary.org/wiki/%ED%99%94%EC%82%AC%EC%B2%A8%EC%A1%B1#:~:text=%EC%96%B4%EC%9B%90%3A%20%ED%95%9C%EC%9E%90%20%E7%95%AB%E8%9B%87%E6%B7%BB%E8%B6%B3.,%ED%95%98%EA%B2%8C%20%EB%8D%A7%EB%B6%99%EC%9D%B4%EB%8A%94%20%EA%B2%83%EC%9D%98%20%EB%B9%84%EC%9C%A0.">화사첨족</a>
 */
public @interface 畵蛇添足 {

    String value() default "";
}
