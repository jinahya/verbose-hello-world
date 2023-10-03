package com.github.jinahya.hello.util.jtabletree;

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

import com.github.jinahya.hello.util.LoggingUtils;

import javax.swing.*;

class JTreeTableMain {

    static {
        LoggingUtils.setLevel("java.awt", "INFO");
        LoggingUtils.setLevel("javax.swing", "INFO");
        LoggingUtils.setLevel("sun", "INFO");
    }

    public static void main(final String... args) {
        final JTable table = new JTreeTable();
        {
            table.setSize(300, 200);
        }
        final var frame = new JFrame("test");
        frame.setContentPane(new JScrollPane(table));
        frame.pack();
        frame.setVisible(true);
    }
}
