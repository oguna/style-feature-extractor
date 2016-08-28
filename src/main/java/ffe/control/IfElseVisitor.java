package ffe.control;

import ffe.Token;
import ffe.FeatureCollector;
import ffe.TokenSequence;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.*;

public class IfElseVisitor extends ControlStatementVisitor {

    public IfElseVisitor(TokenSequence tokenSequence, FeatureCollector featureCollector) {
        super(tokenSequence, featureCollector);
    }

    private void collectFeature(String format, Token elseToken, boolean newLine) {

    }

    @Override
    public boolean visit(IfStatement node) {
        // TODO
        if (node.getElseStatement() != null && node.getElseStatement() instanceof IfStatement) {
            Token elseToken = tokenSequence.searchBackwardBeforeNode(TokenNameelse, node.getElseStatement());
            Token ifToken = tokenSequence.searchBackwardBeforeNode(TokenNameif, node.getElseStatement());
            boolean newLine = elseToken.line == ifToken.line;
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_KEEP_ELSE_STATEMENT_ON_SAME_LINE, elseToken, newLine);
        }
        return super.visit(node);
    }
}
