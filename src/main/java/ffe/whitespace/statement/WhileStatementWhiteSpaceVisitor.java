package ffe.whitespace.statement;

import ffe.FeatureCollector;
import ffe.Token;
import ffe.TokenSequence;
import ffe.whitespace.Direction;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.jetbrains.annotations.NotNull;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameLPAREN;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameRPAREN;

public class WhileStatementWhiteSpaceVisitor extends WhiteSpaceVisitor {
    public WhileStatementWhiteSpaceVisitor(@NotNull TokenSequence tokenSequence, @NotNull FeatureCollector featureCollector) {
        super(tokenSequence, featureCollector);
    }

    @Override
    public boolean visit(WhileStatement node) {
        Token leftParen = tokenSequence.searchForwardInNode(TokenNameLPAREN, node);
        Token rightParen = tokenSequence.searchForwardAfterNode(TokenNameRPAREN, node.getExpression());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_WHILE, leftParen, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_WHILE, leftParen, Direction.AFTER);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_WHILE, rightParen, Direction.BEFORE);
        return super.visit(node);
    }

    @Override
    public boolean visit(DoStatement node) {
        Token leftParen = tokenSequence.searchForwardAfterNode(TokenNameLPAREN, node.getBody());
        Token rightParen = tokenSequence.searchForwardAfterNode(TokenNameRPAREN, node.getExpression());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_WHILE, leftParen, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_WHILE, leftParen, Direction.AFTER);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_WHILE, rightParen, Direction.BEFORE);
        return super.visit(node);
    }
}
