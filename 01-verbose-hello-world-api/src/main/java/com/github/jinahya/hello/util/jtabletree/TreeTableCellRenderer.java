package com.github.jinahya.hello.util.jtabletree;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Objects;

class TreeTableCellRenderer implements TableCellRenderer {

    TreeTableCellRenderer(final JTree tree) {
        super();
        this.tree = Objects.requireNonNull(tree, "tree is null");
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value,
                                                   final boolean isSelected, final boolean hasFocus,
                                                   final int row, final int column) {
        return tree;
    }

    private final JTree tree;
}
