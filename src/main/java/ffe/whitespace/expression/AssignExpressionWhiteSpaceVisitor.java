package ffe.whitespace.expression;

import ffe.Token;
import ffe.whitespace.Direction;
import ffe.whitespace.FeatureCollector;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

public class AssignExpressionWhiteSpaceVisitor extends WhiteSpaceVisitor {

    public AssignExpressionWhiteSpaceVisitor(char[] source, FeatureCollector featureCollector) {
        super(source, featureCollector);
    }

    @Override
    public boolean visit(Assignment node) {
        Token token = searchForward(node.getOperator().toString(), node.getLeftHandSide().getStartPosition() + node.getLeftHandSide().getLength());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_ASSIGNMENT_OPERATOR, token, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_ASSIGNMENT_OPERATOR, token, Direction.AFTER);
        return super.visit(node);
    }
}
