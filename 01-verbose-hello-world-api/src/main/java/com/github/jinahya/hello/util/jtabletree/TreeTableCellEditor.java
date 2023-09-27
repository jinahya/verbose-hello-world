package com.github.jinahya.hello.util.jtabletree;

import javax.swing.tree.TreePath;
import java.awt.*;

public interface TreeTableCellEditor {

    Component getTableCellEditorComponent(TreePath path, boolean isSelected, int row, int column);
}
