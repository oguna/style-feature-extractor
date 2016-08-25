package ffe.whitespace.statement;

import ffe.Token;
import ffe.whitespace.Direction;
import ffe.whitespace.FeatureCollector;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.*;

public class SwitchStatementWhiteSpaceVisitor extends WhiteSpaceVisitor {

    public SwitchStatementWhiteSpaceVisitor(char[] source, FeatureCollector featureCollector) {
        super(source, featureCollector);
    }

    @Override
    public boolean visit(SwitchStatement node) {
        Token leftParen = searchForward(TokenNameLPAREN, node.getStartPosition());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_SWITCH, leftParen, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_SWITCH, leftParen, Direction.AFTER);
        Token rightParen = searchForward(TokenNameRPAREN, node.getExpression().getStartPosition() + node.getExpression().getLength());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_SWITCH, rightParen, Direction.BEFORE);
        Token brace = searchForward(TokenNameRBRACE, node.getExpression().getStartPosition() + node.getExpression().getLength());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_SWITCH, brace, Direction.BEFORE);
        return super.visit(node);
    }

    @Override
    public boolean visit(SwitchCase node) {
        if (node.isDefault()) {
            Token colon = searchForward(TokenNameCOLON, node.getStartPosition());
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_DEFAULT, colon, Direction.BEFORE);
        } else {
            Token colon = searchForward(TokenNameCOLON, node.getStartPosition());
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_CASE, colon, Direction.BEFORE);
        }
        return super.visit(node);
    }
}
