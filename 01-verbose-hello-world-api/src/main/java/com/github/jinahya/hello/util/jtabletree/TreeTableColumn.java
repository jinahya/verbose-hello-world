package com.github.jinahya.hello.util.jtabletree;

import javax.swing.table.TableColumn;

public class TreeTableColumn extends TableColumn {

    public TreeTableColumn(final int modelIndex, final int width) {
        super(modelIndex, width);
    }

    @Override
    public void setHeaderValue(final Object headerValue) {
        super.setHeaderValue(headerValue);
    }
}
