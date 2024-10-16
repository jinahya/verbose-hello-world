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

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A roof above a roof.
 *
 * @see <a href="https://namu.wiki/w/%EC%98%A5%EC%83%81%EA%B0%80%EC%98%A5">옥상가옥</a> (manu.wiki)
 * @see 屋下架屋
 * @see 畵蛇添足
 */
@Documented
@Repeatable(屋上架屋Container.class)
@Retention(RetentionPolicy.SOURCE)
public @interface 屋上架屋 {

    String value() default "";
}
