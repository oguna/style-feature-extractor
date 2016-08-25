package ffe.whitespace.statement;

import ffe.Token;
import ffe.whitespace.Direction;
import ffe.whitespace.FeatureCollector;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameLPAREN;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameRPAREN;

public class WhileStatementWhiteSpaceVisitor extends WhiteSpaceVisitor {

    public WhileStatementWhiteSpaceVisitor(char[] source, FeatureCollector featureCollector) {
        super(source, featureCollector);
    }

    @Override
    public boolean visit(WhileStatement node) {
        Token leftParen = searchForward(TokenNameLPAREN, node.getStartPosition());
        Token rightParen = searchForward(TokenNameRPAREN, node.getExpression().getStartPosition() + node.getExpression().getLength());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_WHILE, leftParen, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_WHILE, leftParen, Direction.AFTER);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_WHILE, rightParen, Direction.BEFORE);
        return super.visit(node);
    }

    @Override
    public boolean visit(DoStatement node) {
        Token leftParen = searchForward(TokenNameLPAREN, node.getBody().getStartPosition() + node.getBody().getLength());
        Token rightParen = searchForward(TokenNameRPAREN, node.getExpression().getStartPosition() + node.getExpression().getLength());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_WHILE, leftParen, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_WHILE, leftParen, Direction.AFTER);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_WHILE, rightParen, Direction.BEFORE);
        return super.visit(node);
    }
}
