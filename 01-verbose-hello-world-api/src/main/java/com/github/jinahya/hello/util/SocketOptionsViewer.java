package com.github.jinahya.hello.util;

/*-
 * #%L
 * verbose-hello-world-srv-common
 * %%
 * Copyright (C) 2018 - 2022 Jinahya, Inc.
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

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * A class renders socket options of all kinds of sockets.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorldNetUtils#printSocketOptions(Class, Object)
 */
@Slf4j
class SocketOptionsViewer {

    public static void main(String... args) throws Exception {
        if (GraphicsEnvironment.isHeadless()) {
            SocketOptionsPrinter.main(args);
            return;
        }
        var model = new DefaultTableModel();
        {
            final var list = new ArrayList<String>();
            HelloWorldNetUtils.acceptEachStandardSocketOption(so -> list.add(so.name()));
            model.addColumn("OPTION", list.toArray(Object[]::new));
        }
        for (final Map.Entry<Class<?>, Callable<?>> e : SocketOptionsPrinter.PARIS.entrySet()) {
            final var list = new ArrayList<>();
            HelloWorldNetUtils.acceptSocketOptionsHelper(
                    e.getKey(),
                    e.getValue().call(),
                    o -> t -> v -> list.add(Objects.toString(v))
            );
            model.addColumn(e.getKey().getSimpleName(), list.toArray(Object[]::new));
        }
        final var frame = new JFrame("Socket Options Viewer");
        final var table = new JTable(model);
        table.setDefaultEditor(Object.class, null);
        frame.setContentPane(new JScrollPane(table));
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
