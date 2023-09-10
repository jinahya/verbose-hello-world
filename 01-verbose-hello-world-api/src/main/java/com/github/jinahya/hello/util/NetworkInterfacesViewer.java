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
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.Serial;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;

/**
 * A class renders properties of all {@link NetworkInterface}s.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see NetworkInterfacesPrinter
 */
@Slf4j
class NetworkInterfacesViewer extends AbstractViewer {

    private static final String NAME = "Network Interfaces Properties";

    private static class PropertyNode extends DefaultMutableTreeNode {

        @Serial
        private static final long serialVersionUID = -7457100976727315195L;

        PropertyNode(final JavaBeansUtils.PropertyValue userObject) {
            super(Objects.requireNonNull(userObject, "userObject is null"));
        }

        JavaBeansUtils.PropertyValue getPropertyValue() {
            return (JavaBeansUtils.PropertyValue) getUserObject();
        }
    }

    public static void main(String... args) throws Exception {
        if (!init(args, NetworkInterfacesPrinter.class)) {
            return;
        }
        final var treeRoot = new DefaultMutableTreeNode();
        NetworkInterfacesPrinter.acceptEachNetworkInterface((ni, i) -> {
            final var userObject = JavaBeansUtils.PropertyValue.of(
                    "networkInterface[" + i + ']', NetworkInterface.class, ni);
            treeRoot.add(new PropertyNode(userObject));
        });
        for (final var e = treeRoot.children(); e.hasMoreElements(); ) {
            var parent = (PropertyNode) e.nextElement();
            JavaBeansUtils.acceptEachProperty(
                    parent,
                    ((JavaBeansUtils.PropertyValue) parent.getUserObject()).value,
                    p -> n -> v -> {
//                        log.debug("p: {}, n: {}, v: {}", p, n, v);
                        var child = new PropertyNode(v);
                        p.add(child);
                        return child;
                    },
                    p -> n -> i -> v -> {
//                        log.debug("p: {}, n: {}, i: {}, v: {}", p, n, i, v);
                        var child = new PropertyNode(
                                JavaBeansUtils.PropertyValue.of(
                                        v.name,
                                        v.type,
                                        v.value)
                        );
                        p.add(child);
                        return child;
                    }
            );
        }

        final var treeModel = new DefaultTreeModel(treeRoot);

        final JTree tree = new JTree(treeModel) {
//            @Override
//            public String convertValueToText(Object value, boolean selected, boolean expandedPathComponents,
//                                             boolean leaf, int row, boolean hasFocus) {
////                log.debug("value: {} {}", value,
////                          Optional.ofNullable(value).map(Object::getClass).orElse(null));
//                if (value instanceof PropertyNode) {
//                    return ((PropertyNode) value).getPropertyValue().name;
//                }
//                return super.convertValueToText(value, selected, expandedPathComponents, leaf, row, hasFocus);
//            }
        };
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setCellEditor(null);

        //        tree.setCellRenderer(new DefaultTreeCellRenderer() {
//            @Override
//            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
//                                                          boolean expandedPathComponents, boolean leaf, int row,
//                                                          boolean hasFocus) {
//                final var component = super.getTreeCellRendererComponent(tree, value, sel, expandedPathComponents,
//                                                                         leaf, row,
//                                                                         hasFocus);
//                final var userObject = (JavaBeansUtils.PropertyValue) ((DefaultMutableTreeNode) value).getUserObject();
//                setText(userObject.name);
//                return this;
//            }
//        });
        tree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                log.debug("expandedPathComponents: {}, {}", tree.getRowCount(), event);
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
                log.debug("collapsed: {}, {}", tree.getRowCount(), event);
            }
        });

        final var tableModel = new DefaultTableModel() {

            @Override
            public int getRowCount() {
                final var rowCount = tree.getRowCount();
//                log.debug("rowCount: {}", rowCount);
                return rowCount;
            }

//            @Override
//            public int getColumnCount() {
//                final var columnCount = super.getColumnCount();
//                log.debug("columnCount: {}", columnCount);
//                return columnCount;
//            }

            @Override
            public Object getValueAt(final int rowIndex, final int columnIndex) {
                return tree.getPathForRow(rowIndex);
            }
        };

        {
            final var treeCellRenderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
            treeCellRenderer.setLeafIcon(null);
            treeCellRenderer.setClosedIcon(null);
            treeCellRenderer.setOpenIcon(null);
        }

        final var expandedPathComponents = new HashSet<>();
        tree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                expandedPathComponents.add(event.getPath().getLastPathComponent());
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
                expandedPathComponents.remove(event.getPath().getLastPathComponent());
            }
        });

        var columnModel = new DefaultTableColumnModel();
        {
            final var column = new TableColumn(0);
            column.setHeaderValue("name");
            column.setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                        final JTable table, Object value, final boolean isSelected,
                        final boolean hasFocus, final int row, final int column) {
                    var path = (TreePath) value;
                    var node = (PropertyNode) path.getLastPathComponent();
                    value = node.getPropertyValue().name;
                    final var tableComponent = (JLabel) super.getTableCellRendererComponent(
                            table, value, isSelected, hasFocus, row, column);
//                    if (node.getChildCount() > 0) {
//                        if (expandedPathComponents.contains(node)) {
//                            tableComponent.setIcon((Icon) UIManager.get("Tree.openIcon"));
//                            tableComponent.setIcon((Icon) UIManager.get("Tree.expandedIcon"));
//                        } else {
//                            tableComponent.setIcon((Icon) UIManager.get("Tree.closedIcon"));
//                            tableComponent.setIcon((Icon) UIManager.get("Tree.leafIcon"));
//                        }
//                    } else {
//                        // leaf
//                        tableComponent.setIcon((Icon) UIManager.get("Tree.leafIcon"));
//                    }

                    UIDefaults defaults = UIManager.getDefaults();
                    Enumeration<Object> keysEnumeration = defaults.keys();
                    ArrayList<Object> keysList = Collections.list(keysEnumeration);
                    for (Object key : keysList) {
                        final var string = Objects.toString(key);
                        if (Objects.toString(key).startsWith("Tree.") && string.endsWith("Icon")) {
                            log.debug(">>>> key: {}", key);
                        }
                    }

                    tableComponent.setIcon((Icon) UIManager.get("Tree.expandedIcon")); // v
//                    tableComponent.setIcon((Icon) UIManager.get("Tree.closedIcon")); // directory

                    if (true) {
                        return tableComponent;
                    }
//                    log.debug("table: foreground: {}, background: {}",
//                              tableComponent.getForeground(),
//                              tableComponent.getBackground());
                    final var treeComponent = tree.getCellRenderer()
                            .getTreeCellRendererComponent(tree, value, isSelected, true, false,
                                                          row, false);
                    treeComponent.setBackground(tableComponent.getBackground());
                    treeComponent.setForeground(tableComponent.getForeground());
//                    log.debug("tree foreground: {}, background: {}", treeComponent.getForeground(),
//                              treeComponent.getBackground());
//                    SwingUtilities.updateComponentTreeUI(treeComponent);
                    return treeComponent;
                }
            });
            columnModel.addColumn(column);
        }
        {
            final var column = new TableColumn(1);
            column.setHeaderValue("value");
            column.setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                        final JTable table, Object value, final boolean isSelected,
                        final boolean hasFocus, final int row, final int column) {
                    var path = (TreePath) value;
                    var node = (PropertyNode) path.getLastPathComponent();
                    value = node.getPropertyValue().value;
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                                                               row, column);
                }
            });
            columnModel.addColumn(column);
        }
        {
            final var column = new TableColumn(2);
            column.setHeaderValue("type");
            column.setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                        final JTable table, Object value, final boolean isSelected,
                        final boolean hasFocus, final int row, final int column) {
                    var path = (TreePath) value;
                    var node = (PropertyNode) path.getLastPathComponent();
                    value = node.getPropertyValue().name;
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                                                               row, column);
                }
            });
            columnModel.addColumn(column);
        }

        final var table = new JTable(tableModel, columnModel);
        table.setDefaultEditor(Object.class, null);

        final var frame = new JFrame(NAME);
//        frame.setContentPane(new JScrollPane(tree));
        frame.setContentPane(new JScrollPane(table));
        show(frame);
    }
}
