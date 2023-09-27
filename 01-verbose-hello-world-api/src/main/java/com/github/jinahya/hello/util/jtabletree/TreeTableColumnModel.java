package com.github.jinahya.hello.util.jtabletree;

import javax.swing.tree.TreePath;

public interface TreeTableColumnModel {

    Object getValueAt(TreePath treePath, int columnIndex);
}
