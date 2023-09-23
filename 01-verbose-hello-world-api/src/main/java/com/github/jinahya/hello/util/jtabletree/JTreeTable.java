package com.github.jinahya.hello.util.jtabletree;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.util.Objects;

public class JTreeTable extends JTable {

    public JTreeTable() {
        super();
        treeTableModelAdapter = new TreeTableModelAdapter(null, getModel(), null);
    }

    @Override
    public final void setModel(final TableModel dataModel) {
        treeTableModelAdapter.tableModel = Objects.requireNonNull(dataModel, "dataModel is null");
    }

    public void setTree(final JTree tree) {
        treeTableModelAdapter.tree = tree;
    }

    public void setTreeTableModel(final TreeTableModel treeTableModel) {
        treeTableModelAdapter.treeTableModel = treeTableModel;
    }

    private final TreeTableModelAdapter treeTableModelAdapter;
}
