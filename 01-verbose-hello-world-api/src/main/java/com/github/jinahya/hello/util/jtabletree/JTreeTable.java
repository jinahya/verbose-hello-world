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
