package com.github.jinahya.hello.util.jtabletree;

import javax.swing.tree.TreePath;

public interface TreeTableModel {

    Object getValueAt(TreePath treePath, int columnIndex);
}
