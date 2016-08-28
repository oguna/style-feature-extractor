package ffe.whitespace.array;

import ffe.FeatureCollector;
import ffe.Token;
import ffe.TokenSequence;
import ffe.whitespace.Direction;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.jetbrains.annotations.NotNull;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameLBRACKET;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameRBRACKET;

public class ArrayElementAccessVisitor extends WhiteSpaceVisitor {

    public ArrayElementAccessVisitor(@NotNull TokenSequence tokenSequence, @NotNull FeatureCollector featureCollector) {
        super(tokenSequence, featureCollector);
    }

    @Override
    public boolean visit(ArrayAccess node) {
        Token leftBracket = tokenSequence.searchForwardAfterNode(TokenNameLBRACKET, node.getArray());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACKET_IN_ARRAY_REFERENCE, leftBracket, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_BRACKET_IN_ARRAY_REFERENCE, leftBracket, Direction.AFTER);
        Token rightBracket = tokenSequence.searchForwardAfterNode(TokenNameRBRACKET, node.getIndex());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_BRACKET_IN_ARRAY_REFERENCE, rightBracket, Direction.BEFORE);
        return super.visit(node);
    }
}
