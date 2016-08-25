package ffe.whitespace.array;

import ffe.Token;
import ffe.whitespace.Direction;
import ffe.whitespace.FeatureCollector;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.Dimension;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameLBRACKET;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameRBRACKET;

public class ArrayAllocationVisitor extends WhiteSpaceVisitor {

    public ArrayAllocationVisitor(char[] source, FeatureCollector featureCollector) {
        super(source, featureCollector);
    }

    public boolean visit(ArrayCreation node) {
        for (Object i : node.getType().dimensions()) {
            Dimension expression = (Dimension) i;
            Token leftBracket = searchForward(TokenNameLBRACKET, expression.getStartPosition());
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACKET_IN_ARRAY_ALLOCATION_EXPRESSION, leftBracket, Direction.BEFORE);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_BRACKET_IN_ARRAY_ALLOCATION_EXPRESSION, leftBracket, Direction.AFTER);
            Token rightBracket = searchBackward(TokenNameRBRACKET, expression.getStartPosition() + expression.getLength());
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_BRACKET_IN_ARRAY_ALLOCATION_EXPRESSION, rightBracket, Direction.BEFORE);
        }
        return super.visit(node);
    }
}
