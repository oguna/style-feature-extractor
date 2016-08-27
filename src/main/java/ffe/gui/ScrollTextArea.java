package ffe.gui;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

public class ScrollTextArea extends VirtualizedScrollPane<CodeArea> {
    public ScrollTextArea() {
        super(new CodeArea());
        getContent().setParagraphGraphicFactory(LineNumberFactory.get(getContent()));
    }
}
