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
import javax.swing.event.CellEditorListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serial;
import java.net.NetworkInterface;
import java.util.EventObject;
import java.util.HexFormat;
import java.util.Objects;
import java.util.Optional;

/**
 * A class renders properties of all {@link NetworkInterface}s.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see NetworkInterfacesPrinter
 */
@SuppressWarnings({
        "java:S1199" // nested code block
})
@Slf4j
class NetworkInterfacesViewer extends AbstractViewer {

    private static final String NAME = "Network Interfaces Properties";

    private static class PropertyNode
            extends DefaultMutableTreeNode
            implements JavaBeansUtils.PropertyInfoHolder {

        @Serial
        private static final long serialVersionUID = -7457100976727315195L;

        private PropertyNode(final JavaBeansUtils.PropertyInfo userObject) {
            super(Objects.requireNonNull(userObject, "userObject is null"));
        }

        @Override
        public JavaBeansUtils.PropertyInfo get() {
            return (JavaBeansUtils.PropertyInfo) getUserObject();
        }
    }

    public static void main(final String... args) throws Exception {
        if (!init(args, NetworkInterfacesPrinter.class)) {
            return;
        }
        final var root = new DefaultMutableTreeNode();
        {
            var index = 0;
            for (final var e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ) {
                final var value = e.nextElement();
                final var child = new PropertyNode(
                        new JavaBeansUtils.PropertyInfo(null, "networkInterface",
                                                        NetworkInterface.class, index++, value)
                );
                root.add(child);
                JavaBeansUtils.acceptEachProperty(child, value, p -> i -> {
                    final var c = new PropertyNode(i);
                    p.add(c);
                    return c;
                });
            }
        }
        final int[] renderingRow = new int[1];
        final var tree = new JTree[1];
        final var table = new JTable[1];
        tree[0] = new JTree() { // @formatter:off
            @Override  public void setBounds(int x, int y, int width, int height) {
                super.setBounds(table[0].getX(), 0, width, table[0].getHeight());
            }
            @Override public void paint(final Graphics g) {
                g.translate(0, -renderingRow[0] * table[0].getRowHeight());
                super.paint(g);
            }
        }; // @formatter:on
//        tree[0].getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
//        tree[0].addFocusListener(new FocusListener() {
//            @Override
//            public void focusGained(FocusEvent e) {
//                log.debug("focusGained: {}", e);
//            }
//
//            @Override
//            public void focusLost(FocusEvent e) {
//            }
//        });
//        tree[0].setToggleClickCount(1);
        tree[0].addMouseListener(new MouseAdapter() { // @formatter:off
            @Override public void mouseClicked(MouseEvent e) {
                log.debug("mouseClicked({})", e);
                super.mouseClicked(e);
            }
        }); // @formatter:on
        final var treeModel = new DefaultTreeModel(root);
        tree[0].setModel(treeModel);
        tree[0].setCellRenderer(new DefaultTreeCellRenderer() { // @formatter:on
            @Override
            public Component getTreeCellRendererComponent(final JTree tree, Object value,
                                                          final boolean sel, final boolean expanded,
                                                          final boolean leaf, final int row,
                                                          final boolean hasFocus) {
                if (value instanceof PropertyNode node) {
                    final var info = node.get();
                    value = info.name + Optional.ofNullable(info.index).map(i -> "[" + i + ']')
                            .orElse("");
                }
                return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
                                                          hasFocus);
            }
        }); // @formatter:on

        UIManager.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                log.debug("UIManager property changed: {}", evt);
            }
        });

        table[0] = new JTable() { // @formatter:off
            @Override public void updateUI() {
                super.updateUI();
                tree[0].updateUI();
            }
        }; // formatter:on
        table[0].setColumnSelectionAllowed(false);
        table[0].setDefaultEditor(Object.class, null);
        final var tableModel = new DefaultTableModel() { // @formatter:off
            @Serial private static final long serialVersionUID=7137590333486505662L;
            @Override public int getRowCount() { return tree[0].getRowCount(); }
            @Override public Object getValueAt(final int rowIndex, final int columnIndex) {
                return tree[0].getPathForRow(rowIndex);
            }
        }; // @formatter:on
        {
            final var treeCellRenderer = (DefaultTreeCellRenderer) tree[0].getCellRenderer();
            treeCellRenderer.setLeafIcon(null);
            treeCellRenderer.setClosedIcon(null);
            treeCellRenderer.setOpenIcon(null);
        }
//        tree[0].addTreeSelectionListener(e -> {
//            log.debug("\ntree selection: {}", e);
//            final var path = e.getPath();
//            final var node = (PropertyNode) path.getLastPathComponent();
//            if (node.isLeaf()) {
//                return;
//            }
//            final var row = tree[0].getRowForPath(path);
//            log.debug("row: {}", row);
//            if (tree[0].isCollapsed(row)) {
//                tree[0].expandPath(path);
////                table[0].getSelectionModel().setSelectionInterval(row, row);
//            } else {
//                tree[0].collapsePath(path);
////                    tree[0].setSelectionRow(row);
////                table[0].getSelectionModel().setSelectionInterval(row, row);
//            }
////            tree[0].setSelectionRow(row);
//            tableModel.fireTableDataChanged();
////            table[0].getSelectionModel().setSelectionInterval(row, row);
//        });
        tree[0].addTreeWillExpandListener(new TreeWillExpandListener() { // @formatter:off
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                tableModel.fireTableDataChanged();
            }
            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
                tableModel.fireTableDataChanged();
            }
        }); // @formatter:on
        final var nameColumn = 0;
        final var valueColumn = nameColumn + 1;
        final var typeColumn = valueColumn + 1;
        final var columnModel = new DefaultTableColumnModel();
        {
            final var column = new TableColumn(nameColumn);
            column.setHeaderValue("name");
            column.setCellRenderer(new DefaultTableCellRenderer() { // @formatter:off
                @Override public Component getTableCellRendererComponent(
                        final JTable table, Object value, final boolean isSelected,
                        final boolean hasFocus, final int row, final int column) {
                    renderingRow[0] = row;
                    return tree[0];
                }
            }); // @formatter:on
            column.setCellEditor(new TableCellEditor() { // @formatter:off
                @Override public Component getTableCellEditorComponent(
                        JTable table, Object value, boolean isSelected, int row, int column) {
                    if (column != nameColumn) { return null; }
                    return tree[0];
                }
                @Override public Object getCellEditorValue() { return null; }
                @Override public boolean isCellEditable(final EventObject anEvent) {
                    if (!(anEvent instanceof MouseEvent me)) { return false; }
                    final var point = me.getPoint();
                    final var row = table[0].rowAtPoint(point);
                    final var column = table[0].columnAtPoint(point);
                    if (column != nameColumn) { return false; }
                    final var me2 = new MouseEvent(
                            tree[0], me.getID(), me.getWhen(), me.getModifiersEx(),
                            me.getX() - table[0].getCellRect(0, column, true).x, me.getY(),
                            me.getClickCount(), me.isPopupTrigger(), me.getButton()
                    );
                    tree[0].dispatchEvent(me2);
                    tableModel.fireTableDataChanged();
                    return false;
                }
                @Override public boolean shouldSelectCell(EventObject anEvent) { return false; }
                @Override public boolean stopCellEditing() { return true; }
                @Override public void cancelCellEditing() { }
                @Override public void addCellEditorListener(final CellEditorListener l) { }
                @Override public void removeCellEditorListener(CellEditorListener l) { }
            }); // @formatter:on
            columnModel.addColumn(column);
        }
        {
            final var column = new TableColumn(valueColumn);
            column.setHeaderValue("value");
            column.setCellRenderer(new DefaultTableCellRenderer() { // @formatter:on
                @Override
                public Component getTableCellRendererComponent(
                        final JTable table, Object value, final boolean isSelected,
                        final boolean hasFocus, final int row, final int column) {
                    final var path = tree[0].getPathForRow(row);
                    final var node = (PropertyNode) path.getLastPathComponent();
                    value = node.get().value;
                    if (value instanceof byte[] b) {
                        value = HexFormat.of().formatHex(b);
                    } else {
                        value = Objects.toString(value);
                    }
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                                                               row, column);
                }
            }); // @formatter:on
            columnModel.addColumn(column);
        }
        {
            final var column = new TableColumn(typeColumn);
            column.setHeaderValue("type");
            column.setCellRenderer(new DefaultTableCellRenderer() { // @formatter:off
                @Override public Component getTableCellRendererComponent(
                        final JTable table, Object value, final boolean isSelected,
                        final boolean hasFocus, final int row, final int column) {
                    final var path = tree[0].getPathForRow(row);
                    final var node = (PropertyNode) path.getLastPathComponent();
                    value = node.get().type.getName();
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                                                               row, column);
                }
            }); // @formatter:on
            columnModel.addColumn(column);
        }
        table[0].setModel(tableModel);
        table[0].setColumnModel(columnModel);
        table[0].getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table[0].getSelectionModel().addListSelectionListener(e -> {
            final var selectedRow = table[0].getSelectedRow();
            tree[0].setSelectionInterval(selectedRow, selectedRow);
        });
        tree[0].setModel(treeModel);
        tree[0].setRootVisible(false);
        tree[0].setShowsRootHandles(true);
        tree[0].setRowHeight(table[0].getRowHeight());
        final var frame = new JFrame(NAME);
        frame.setContentPane(new JScrollPane(table[0]));
        show(frame);
    }
}
