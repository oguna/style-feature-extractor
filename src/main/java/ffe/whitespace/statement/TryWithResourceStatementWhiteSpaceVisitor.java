package ffe.whitespace.statement;

import ffe.FeatureCollector;
import ffe.Token;
import ffe.TokenSequence;
import ffe.whitespace.Direction;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.*;

public class TryWithResourceStatementWhiteSpaceVisitor extends WhiteSpaceVisitor {


    public TryWithResourceStatementWhiteSpaceVisitor(@NotNull TokenSequence tokenSequence, @NotNull FeatureCollector featureCollector) {
        super(tokenSequence, featureCollector);
    }

    @Override
    public boolean visit(TryStatement node) {
        if (node.resources() != null && node.resources().size() >= 2) {
            List nodeList = node.resources();
            VariableDeclarationExpression lastVde = (VariableDeclarationExpression)nodeList.get(nodeList.size() - 1);
            Token leftParen = tokenSequence.searchForwardInNode(TokenNameLPAREN, node);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TRY, leftParen, Direction.BEFORE);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TRY, leftParen, Direction.AFTER);
            Token rightParen = tokenSequence.searchForwardAfterNode(TokenNameRPAREN, lastVde);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_TRY, rightParen, Direction.BEFORE);
            for (int i = 0; i < nodeList.size() - 1; i++) {
                VariableDeclarationExpression vde = (VariableDeclarationExpression)nodeList.get(i);
                Token token = tokenSequence.searchForwardAfterNode(TokenNameSEMICOLON, vde);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_TRY_RESOURCES, token, Direction.BEFORE);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_TRY_RESOURCES, token, Direction.AFTER);
            }
        }
        return super.visit(node);
    }
}
