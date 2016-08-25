package ffe.whitespace.array;

import ffe.Token;
import ffe.whitespace.Direction;
import ffe.whitespace.FeatureCollector;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameLBRACKET;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameRBRACKET;

public class ArrayElementAccessVisitor extends WhiteSpaceVisitor {
    public ArrayElementAccessVisitor(char[] source, FeatureCollector featureCollector) {
        super(source, featureCollector);
    }

    @Override
    public boolean visit(ArrayAccess node) {
        Token leftBracket = searchForward(TokenNameLBRACKET, node.getArray().getStartPosition() + node.getArray().getLength());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACKET_IN_ARRAY_REFERENCE, leftBracket, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_BRACKET_IN_ARRAY_REFERENCE, leftBracket, Direction.AFTER);
        Token rightBracket = searchForward(TokenNameRBRACKET, node.getIndex().getStartPosition() + node.getIndex().getLength());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_BRACKET_IN_ARRAY_REFERENCE, rightBracket, Direction.BEFORE);
        return super.visit(node);
    }
}
