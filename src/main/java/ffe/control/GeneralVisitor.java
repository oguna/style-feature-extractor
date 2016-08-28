package ffe.control;

import ffe.Token;
import ffe.FeatureCollector;
import ffe.TokenSequence;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.*;

public class GeneralVisitor extends ControlStatementVisitor {

    public GeneralVisitor(TokenSequence tokenSequence, FeatureCollector featureCollector) {
        super(tokenSequence, featureCollector);
    }

    private void collectFeature(String format, Token elseToken, boolean newLine) {

    }

    @Override
    public boolean visit(IfStatement node) {
        if (node.getElseStatement() != null) {
            Token closingBlockToken = tokenSequence.searchBackwardInNode(TokenNameRBRACE, node.getElseStatement());
            Token elseToken = tokenSequence.searchBackwardInNode(TokenNameelse, node.getElseStatement());
            boolean newLine = closingBlockToken.line == elseToken.line;
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_ELSE_IN_IF_STATEMENT, elseToken, newLine);
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(TryStatement node) {
        for (Object i : node.catchClauses()) {
            CatchClause cc = (CatchClause)i;
            Token closingBlockToken = tokenSequence.searchBackwardInNode(TokenNameRBRACE, cc);
            Token catchToken = tokenSequence.searchBackwardInNode(TokenNamecatch, cc);
            boolean newLine = closingBlockToken.line == catchToken.line;
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_CATCH_IN_TRY_STATEMENT, catchToken, newLine);
        }
        if (node.getFinally() != null) {
            Token closingBlockToken = tokenSequence.searchBackwardInNode(TokenNameRBRACE, node.getFinally());
            Token finallyToken = tokenSequence.searchBackwardInNode(TokenNamefinally, node.getFinally());
            boolean newLine = closingBlockToken.line == finallyToken.line;
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_FINALLY_IN_TRY_STATEMENT, finallyToken, newLine);
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(DoStatement node) {
        Token closingBlockToken = tokenSequence.searchForwardAfterNode(TokenNameRBRACE, node.getBody());
        Token whileToken = tokenSequence.searchForwardAfterNode(TokenNamewhile, node.getBody());
        boolean newLine = closingBlockToken.line == whileToken.line;
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_WHILE_IN_DO_STATEMENT, whileToken, newLine);
        return super.visit(node);
    }
}
