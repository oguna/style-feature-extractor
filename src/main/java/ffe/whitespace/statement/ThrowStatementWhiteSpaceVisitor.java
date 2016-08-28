package ffe.whitespace.statement;

import ffe.FeatureCollector;
import ffe.Token;
import ffe.TokenSequence;
import ffe.whitespace.Direction;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.jetbrains.annotations.NotNull;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameLPAREN;

public class ThrowStatementWhiteSpaceVisitor extends WhiteSpaceVisitor {


    public ThrowStatementWhiteSpaceVisitor(@NotNull TokenSequence tokenSequence, @NotNull FeatureCollector featureCollector) {
        super(tokenSequence, featureCollector);
    }

    @Override
    public boolean visit(ThrowStatement node) {
        if (node.getExpression() instanceof ParenthesizedExpression) {
            Token leftParen = tokenSequence.searchForwardInNode(TokenNameLPAREN, node);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_PARENTHESIZED_EXPRESSION_IN_THROW, leftParen, Direction.BEFORE);
        }
        return super.visit(node);
    }
}
