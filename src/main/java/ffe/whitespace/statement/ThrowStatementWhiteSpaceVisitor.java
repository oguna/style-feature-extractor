package ffe.whitespace.statement;

import ffe.Token;
import ffe.whitespace.Direction;
import ffe.whitespace.FeatureCollector;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameLPAREN;

public class ThrowStatementWhiteSpaceVisitor extends WhiteSpaceVisitor {

    public ThrowStatementWhiteSpaceVisitor(char[] source, FeatureCollector featureCollector) {
        super(source, featureCollector);
    }

    @Override
    public boolean visit(ThrowStatement node) {
        if (node.getExpression() instanceof ParenthesizedExpression) {
            Token leftParen = searchForward(TokenNameLPAREN, node.getStartPosition());
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_PARENTHESIZED_EXPRESSION_IN_THROW, leftParen, Direction.BEFORE);
        }
        return super.visit(node);
    }
}
