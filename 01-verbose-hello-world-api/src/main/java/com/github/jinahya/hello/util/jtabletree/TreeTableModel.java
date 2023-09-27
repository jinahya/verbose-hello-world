package com.github.jinahya.hello.util.jtabletree;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;
import java.util.Objects;

class TreeTableModel extends AbstractTableModel {

    TreeTableModel(final JTree tree) {
        super();
        this.tree = Objects.requireNonNull(tree, "treeModel is null");
    }

    @Override
    public int getRowCount() {
        return tree.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return 0;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return null;
    }

    private final JTree tree;
}
