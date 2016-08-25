package ffe.whitespace.expression;

import ffe.Token;
import ffe.whitespace.Direction;
import ffe.whitespace.FeatureCollector;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

public class OperatorWhiteSpaceVisitor extends WhiteSpaceVisitor {
    public OperatorWhiteSpaceVisitor(char[] source, FeatureCollector featureCollector) {
        super(source, featureCollector);
    }
    // TODO

    @Override
    public boolean visit(InfixExpression node) {
        Token token = searchForward(node.getOperator().toString(), node.getLeftOperand().getStartPosition() + node.getLeftOperand().getLength());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_BINARY_OPERATOR, token, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_BINARY_OPERATOR, token, Direction.AFTER);
        for (Object i : node.extendedOperands()) {
            Expression expression = (Expression) i;
            Token extendedToken = searchBackward(node.getOperator().toString(), expression.getStartPosition());
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_BINARY_OPERATOR, extendedToken, Direction.BEFORE);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_BINARY_OPERATOR, extendedToken, Direction.AFTER);
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(PostfixExpression node) {
        Token token = searchForward(node.getOperator().toString(), node.getOperand().getStartPosition() + node.getOperand().getLength());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_POSTFIX_OPERATOR, token, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_POSTFIX_OPERATOR, token, Direction.AFTER);
        return super.visit(node);
    }

    @Override
    public boolean visit(PrefixExpression node) {
        if (node.getOperator() == PrefixExpression.Operator.INCREMENT || node.getOperator() == PrefixExpression.Operator.DECREMENT) {
            Token token = searchForward(node.getOperator().toString(), node.getStartPosition());
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_PREFIX_OPERATOR, token, Direction.BEFORE);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_PREFIX_OPERATOR, token, Direction.AFTER);
        } else {
            Token token = searchForward(node.getOperator().toString(), node.getStartPosition());
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_UNARY_OPERATOR, token, Direction.BEFORE);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_UNARY_OPERATOR, token, Direction.AFTER);
        }
        return super.visit(node);
    }
}
