package com.github.jinahya.hello.util.jtabletree;

import com.github.jinahya.hello.util.LoggingUtils;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerEvent;
import java.io.IOException;
import java.util.Objects;

@Slf4j
public class JTreeTable2 extends JComponent {

    static {
        LoggingUtils.setLevel("java.awt", "INFO");
        LoggingUtils.setLevel("javax.swing", "INFO");
        LoggingUtils.setLevel("sun", "INFO");
    }

    public static void main(final String... args) throws IOException {
//        final var component = new JTreeTable2();
//        log.debug("layout: {}", component.getLayout());
//        final var frame = new Frame();
//        frame.add(new JTreeTable2(new JTree()));
//        frame.setVisible(true);
    }

    public JTreeTable2(final JTree tree) {
        super();
        this.tree = Objects.requireNonNull(tree, "tree is null");
        table = new JTable();
        table.setLocation(0, 0);
        add(table);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        table.setBounds(0, 0, width, height);
    }

    @Override
    protected void processComponentEvent(final ComponentEvent e) {
        if (e instanceof ContainerEvent containerEvent) {
//            containerEvent.get
        }
        super.processComponentEvent(e);
    }

    private final JTree tree;

    private final JTable table;
}
