package com.github.jinahya.hello.util.jtabletree;

import javax.swing.tree.TreePath;
import java.awt.*;

public interface TreeTableCellRenderer {

    Component getTreeCellRendererComponent(TreePath path, boolean selected,
                                           boolean expanded, boolean leaf, int row,
                                           boolean hasFocus);
}
