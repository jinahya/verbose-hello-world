package com.github.jinahya.hello.misc.c01rfc863;

import java.nio.channels.SelectionKey;

import static org.mockito.Mockito.mock;

class Rfc863Tcp3ClientAttachmentTest
        extends _Rfc863AttachmentTest.ClientTest<Rfc863Tcp3ClientAttachment> {

    Rfc863Tcp3ClientAttachmentTest() {
        super(Rfc863Tcp3ClientAttachment.class);
    }

    protected Rfc863Tcp3ClientAttachment newInstance(final SelectionKey selectionKey) {
        return new Rfc863Tcp3ClientAttachment(selectionKey);
    }

    @Override
    protected Rfc863Tcp3ClientAttachment newInstance() {
        return newInstance(mock(SelectionKey.class));
    }
}
