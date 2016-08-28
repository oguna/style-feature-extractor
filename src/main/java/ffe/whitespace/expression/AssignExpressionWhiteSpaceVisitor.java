package ffe.whitespace.expression;

import ffe.FeatureCollector;
import ffe.Token;
import ffe.TokenSequence;
import ffe.whitespace.Direction;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.jetbrains.annotations.NotNull;

public class AssignExpressionWhiteSpaceVisitor extends WhiteSpaceVisitor {

    public AssignExpressionWhiteSpaceVisitor(@NotNull TokenSequence tokenSequence, @NotNull FeatureCollector featureCollector) {
        super(tokenSequence, featureCollector);
    }

    @Override
    public boolean visit(Assignment node) {
        int tokenType = TokenSequence.getTokenType(node.getOperator().toString());
        Token operator = tokenSequence.searchForwardAfterNode(tokenType, node.getLeftHandSide());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_ASSIGNMENT_OPERATOR, operator, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_ASSIGNMENT_OPERATOR, operator, Direction.AFTER);
        return super.visit(node);
    }
}
