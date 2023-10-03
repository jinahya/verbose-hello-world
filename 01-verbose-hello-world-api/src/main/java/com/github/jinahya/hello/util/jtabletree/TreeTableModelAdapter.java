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
