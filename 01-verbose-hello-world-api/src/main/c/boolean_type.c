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
//
// Created by Jin Kwon on 4/16/26.
//

#include <stdbool.h>
#include <stdio.h>

int main(void) {

    // -------------------------------------------- boolean literals (since C23)
    // C23 introduced `true` and `false` as keywords of type `bool`.
    // Prior to C23, `true` and `false` were macros defined in <stdbool.h>
    // expanding to `1` and `0`, respectively.

    bool t = true;
    bool f = false;

    printf("true  : %d\n", t);  // 1
    printf("false : %d\n", f);  // 0

    // ------------------------------------- equivalent integral representations
    // `true` is equivalent to the integer constant `1`.
    // `false` is equivalent to the integer constant `0`.

    printf("true  == 1 : %d\n", t == 1);  // 1
    printf("false == 0 : %d\n", f == 0);  // 1

    // ----------------------------------------- implicit conversions to/from bool
    // Any non-zero integer value converts to `true` (1).
    // Zero converts to `false` (0).

    bool from_zero     = 0;
    bool from_one      = 1;
    bool from_negative = -1;
    bool from_large    = 42;

    printf("(bool)  0 : %d\n", from_zero);      // 0
    printf("(bool)  1 : %d\n", from_one);        // 1
    printf("(bool) -1 : %d\n", from_negative);   // 1
    printf("(bool) 42 : %d\n", from_large);      // 1

    // ----------------------------------------- bool promoted to int in expressions
    // When used in arithmetic, `bool` is promoted to `int`.

    int sum = true + true + false + true;
    printf("true + true + false + true : %d\n", sum);  // 3

    // ----------------------------------------- sizeof(bool)
    printf("sizeof(bool)  : %zu\n", sizeof(bool));   // 1 (typically)
    printf("sizeof(true)  : %zu\n", sizeof(true));
    printf("sizeof(false) : %zu\n", sizeof(false));

    return 0;
}
