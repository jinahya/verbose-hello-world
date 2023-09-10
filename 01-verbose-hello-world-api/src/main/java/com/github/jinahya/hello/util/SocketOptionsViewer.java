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
import javax.swing.table.TableCellRenderer;
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
class SocketOptionsViewer extends AbstractViewer {

    private static final String NAME = "Socket Options Viewer";

    static {
        System.setProperty("apple.awt.application.name", NAME);
    }

    public static void main(final String... args) throws Exception {
        if (!init(args, SocketOptionsPrinter.class)) {
            return;
        }
        final var model = new DefaultTableModel();
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
        final var table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                final Component c = super.prepareRenderer(renderer, row, column);
                final var value = getModel().getValueAt(row, column);
                final var font = c.getFont();
                // make first column bold
                if (column == 0) {
                    c.setFont(font.deriveFont(Font.BOLD));
                } else {
                    c.setFont(getFont());
                }
                // make 'NOT SUPPORTED' gray
                if (HelloWorldNetUtils.VALUE_OF_UNSUPPORTED_SOCKET_OPTION.equals(value)) {
                    c.setForeground(Color.GRAY);
                } else {
                    c.setForeground(getForeground());
                }
                return c;
            }
        };
        // make header's font bold
        {
            var header = table.getTableHeader();
            header.setFont(header.getFont().deriveFont(Font.BOLD));
        }
        table.setDefaultEditor(Object.class, null);
        final var frame = new JFrame(NAME);
        frame.setContentPane(new JScrollPane(table));
        show(frame);
    }
}
