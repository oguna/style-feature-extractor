package ffe.whitespace.expression;

import ffe.FeatureCollector;
import ffe.Token;
import ffe.TokenSequence;
import ffe.whitespace.Direction;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.jetbrains.annotations.NotNull;

public class OperatorWhiteSpaceVisitor extends WhiteSpaceVisitor {
    public OperatorWhiteSpaceVisitor(@NotNull TokenSequence tokenSequence, @NotNull FeatureCollector featureCollector) {
        super(tokenSequence, featureCollector);
    }
    // TODO

    @Override
    public boolean visit(InfixExpression node) {
        int operatorTokenType = TokenSequence.getTokenType(node.getOperator().toString());
        Token token = tokenSequence.searchForwardAfterNode(operatorTokenType, node.getLeftOperand());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_BINARY_OPERATOR, token, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_BINARY_OPERATOR, token, Direction.AFTER);
        for (Object i : node.extendedOperands()) {
            Expression expression = (Expression) i;
            int tokenType = TokenSequence.getTokenType(node.getOperator().toString());
            Token extendedToken = tokenSequence.searchBackwardBeforeNode(tokenType, expression);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_BINARY_OPERATOR, extendedToken, Direction.BEFORE);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_BINARY_OPERATOR, extendedToken, Direction.AFTER);
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(PostfixExpression node) {
        int operatorTokenType = TokenSequence.getTokenType(node.getOperator().toString());
        Token token = tokenSequence.searchForwardAfterNode(operatorTokenType, node.getOperand());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_POSTFIX_OPERATOR, token, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_POSTFIX_OPERATOR, token, Direction.AFTER);
        return super.visit(node);
    }

    @Override
    public boolean visit(PrefixExpression node) {
        if (node.getOperator() == PrefixExpression.Operator.INCREMENT || node.getOperator() == PrefixExpression.Operator.DECREMENT) {
            int tokenType = TokenSequence.getTokenType(node.getOperator().toString());
            Token token = tokenSequence.searchForwardInNode(tokenType, node);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_PREFIX_OPERATOR, token, Direction.BEFORE);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_PREFIX_OPERATOR, token, Direction.AFTER);
        } else {
            int tokenType = TokenSequence.getTokenType(node.getOperator().toString());
            Token token = tokenSequence.searchForwardInNode(tokenType, node);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_UNARY_OPERATOR, token, Direction.BEFORE);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_UNARY_OPERATOR, token, Direction.AFTER);
        }
        return super.visit(node);
    }
}
