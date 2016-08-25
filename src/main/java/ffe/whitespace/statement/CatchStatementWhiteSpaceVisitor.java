package ffe.whitespace.statement;

import ffe.Token;
import ffe.whitespace.Direction;
import ffe.whitespace.FeatureCollector;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameLPAREN;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameRPAREN;

public class CatchStatementWhiteSpaceVisitor extends WhiteSpaceVisitor {

    public CatchStatementWhiteSpaceVisitor(char[] source, FeatureCollector featureCollector) {
        super(source, featureCollector);
    }

    @Override
    public boolean visit(CatchClause node) {
        Token leftParen = searchForward(TokenNameLPAREN, node.getStartPosition());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CATCH, leftParen, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_CATCH, leftParen, Direction.AFTER);
        Token rightParen = searchForward(TokenNameRPAREN, node.getException().getStartPosition() + node.getException().getLength());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_CATCH, rightParen, Direction.BEFORE);
        return super.visit(node);
    }
}
