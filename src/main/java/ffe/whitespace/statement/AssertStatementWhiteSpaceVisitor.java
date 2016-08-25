package ffe.whitespace.statement;

import ffe.Token;
import ffe.whitespace.Direction;
import ffe.whitespace.FeatureCollector;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameCOLON;

public class AssertStatementWhiteSpaceVisitor extends WhiteSpaceVisitor {
    public AssertStatementWhiteSpaceVisitor(char[] source, FeatureCollector featureCollector) {
        super(source, featureCollector);
    }

    @Override
    public boolean visit(AssertStatement node) {
        if (node.getMessage() != null) {
            Token token = searchForward(TokenNameCOLON, node.getExpression().getStartPosition() + node.getExpression().getLength());
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_ASSERT, token, Direction.BEFORE);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COLON_IN_ASSERT, token, Direction.AFTER);
        }
        return super.visit(node);
    }
}
