package com.github.jinahya.hello.util.jtabletree;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;
import java.io.Serial;
import java.util.Objects;

class TreeTableModelAdapter extends AbstractTableModel {

    @Serial
    private static final long serialVersionUID = -7500148899401863418L;

    TreeTableModelAdapter(final JTree tree, final TableModel tableModel,
                          final TreeTableModel treeTableModel) {
        super();
        this.tree = tree;
        this.tableModel = Objects.requireNonNull(tableModel, "tableModel is null");
        this.treeTableModel = Objects.requireNonNull(treeTableModel, "treeTableModel is null");
    }

    @Override
    public final int getRowCount() {
        if (tree != null) {
            return tree.getRowCount();
        }
        return tableModel.getRowCount();
    }

    @Override
    public int getColumnCount() {
        if (tree != null) {
            return tableModel.getColumnCount() + 1;
        }
        return tableModel.getColumnCount();
    }

    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        if (tree != null && columnIndex == treeColumnIndex) {
            return TreeModel.class;
        }
        return super.getColumnClass(columnIndex);
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        if (tree != null) {
            if (columnIndex == treeColumnIndex) {
                return tree.getModel();
            } else {
//                return treeTableModel.getValueAt(tree.getPathForRow(rowIndex), columnIndex);
            }
        }
        return tableModel.getValueAt(rowIndex, columnIndex);
    }

    public void setTreeColumnIndex(final int treeColumnIndex) {
        this.treeColumnIndex = treeColumnIndex;
    }

    JTree tree;

    transient TableModel tableModel;

    transient TreeTableModel treeTableModel;

    int treeColumnIndex = 0;
}
