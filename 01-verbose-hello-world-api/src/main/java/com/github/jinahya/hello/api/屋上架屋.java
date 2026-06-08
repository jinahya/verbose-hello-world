package com.github.jinahya.hello.api;

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

import java.lang.annotation.*;

/**
 * A roof above a roof.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://namu.wiki/w/%EC%98%A5%EC%83%81%EA%B0%80%EC%98%A5">옥상가옥</a> (namu.wiki)
 * @see 屋下架屋
 * @see 畵蛇添足
 */
@Documented
@Repeatable(屋上架屋Container.class)
@Retention(RetentionPolicy.SOURCE)
@SuppressWarnings({"UnicodeInCode"})
public @interface 屋上架屋 {

    /**
     * An optional short note describing the redundancy the marker calls out.
     *
     * @return the note; defaults to the empty string.
     */
    String value() default "";
}
