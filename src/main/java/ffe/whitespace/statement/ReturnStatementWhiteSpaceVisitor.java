package ffe.whitespace.statement;

import ffe.FeatureCollector;
import ffe.Token;
import ffe.TokenSequence;
import ffe.whitespace.Direction;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.jetbrains.annotations.NotNull;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameLPAREN;

public class ReturnStatementWhiteSpaceVisitor extends WhiteSpaceVisitor {
    public ReturnStatementWhiteSpaceVisitor(@NotNull TokenSequence tokenSequence, @NotNull FeatureCollector featureCollector) {
        super(tokenSequence, featureCollector);
    }

    @Override
    public boolean visit(ReturnStatement node) {
        if (node.getExpression() instanceof ParenthesizedExpression) {
            Token leftParen = tokenSequence.searchForwardInNode(TokenNameLPAREN, node);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_PARENTHESIZED_EXPRESSION_IN_RETURN, leftParen, Direction.BEFORE);
        }
        return super.visit(node);
    }
}
