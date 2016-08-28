package ffe.whitespace.array;

import ffe.FeatureCollector;
import ffe.Token;
import ffe.TokenSequence;
import ffe.whitespace.Direction;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.Dimension;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.jetbrains.annotations.NotNull;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameLBRACKET;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameRBRACKET;

public class ArrayAllocationVisitor extends WhiteSpaceVisitor {


    public ArrayAllocationVisitor(@NotNull TokenSequence tokenSequence, @NotNull FeatureCollector featureCollector) {
        super(tokenSequence, featureCollector);
    }

    public boolean visit(ArrayCreation node) {
        for (int i = 0; i < node.getType().dimensions().size(); i++) {
            Dimension dimension = (Dimension) node.getType().dimensions().get(i);
            Token leftBracket = tokenSequence.searchForwardInNode(TokenNameLBRACKET, dimension);
            Token rightBracket = tokenSequence.searchBackwardInNode(TokenNameRBRACKET, dimension);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACKET_IN_ARRAY_ALLOCATION_EXPRESSION, leftBracket, Direction.BEFORE);
            if (i < node.dimensions().size()) {
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_BRACKET_IN_ARRAY_ALLOCATION_EXPRESSION, leftBracket, Direction.AFTER);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_BRACKET_IN_ARRAY_ALLOCATION_EXPRESSION, rightBracket, Direction.BEFORE);
            } else {
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_BRACKETS_IN_ARRAY_ALLOCATION_EXPRESSION, leftBracket, rightBracket);
            }
        }
        return super.visit(node);
    }
}
