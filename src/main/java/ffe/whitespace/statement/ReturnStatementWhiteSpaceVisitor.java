package ffe.whitespace.statement;

import ffe.Token;
import ffe.whitespace.Direction;
import ffe.whitespace.FeatureCollector;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameLPAREN;

public class ReturnStatementWhiteSpaceVisitor extends WhiteSpaceVisitor {

    public ReturnStatementWhiteSpaceVisitor(char[] source, FeatureCollector featureCollector) {
        super(source, featureCollector);
    }

    @Override
    public boolean visit(ReturnStatement node) {
        if (source[node.getExpression().getStartPosition()] == '(') {
            Token leftParen = searchForward(TokenNameLPAREN, node.getExpression().getStartPosition());
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_PARENTHESIZED_EXPRESSION_IN_RETURN, leftParen, Direction.BEFORE);
        }
        return super.visit(node);
    }
}
