package com.github.jinahya.hello.util.jtabletree;

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
