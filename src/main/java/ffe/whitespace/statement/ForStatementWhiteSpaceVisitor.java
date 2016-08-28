package ffe.whitespace.statement;

import ffe.FeatureCollector;
import ffe.Token;
import ffe.TokenSequence;
import ffe.whitespace.Direction;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.*;

public class ForStatementWhiteSpaceVisitor extends WhiteSpaceVisitor {

    public ForStatementWhiteSpaceVisitor(@NotNull TokenSequence tokenSequence, @NotNull FeatureCollector featureCollector) {
        super(tokenSequence, featureCollector);
    }

    @Override
    public boolean visit(ForStatement node) {
        Token leftParen = tokenSequence.searchForwardInNode(TokenNameLPAREN, node);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FOR, leftParen, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FOR, leftParen, Direction.AFTER);
        Token rightParen = tokenSequence.searchForwardAfterNode(TokenNameRPAREN, node.getExpression());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FOR, rightParen, Direction.BEFORE);
        List initList = node.initializers();
        for (Object initNode : initList) {
            if (initNode instanceof Assignment) {
            } else {
                VariableDeclarationExpression  vde = (VariableDeclarationExpression )initNode;
                List vdeFragments = vde.fragments();
                if (vdeFragments.size() >= 2) {
                    for (int i = 0; i < vdeFragments.size() - 1; i++) {
                        VariableDeclarationFragment vdef = (VariableDeclarationFragment) vdeFragments.get(i);
                        Token comma = tokenSequence.searchForwardAfterNode(TokenNameCOMMA, vdef);
                        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FOR_INITS, comma, Direction.BEFORE);
                        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FOR_INITS, comma, Direction.AFTER);
                    }
                }
            }
        }
        Token semicolon1 = tokenSequence.searchBackwardBeforeNode(TokenNameSEMICOLON, node.getExpression());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOR, semicolon1, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOR, semicolon1, Direction.AFTER);
        Token semicolon2 = tokenSequence.searchForwardAfterNode(TokenNameSEMICOLON, node.getExpression());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOR, semicolon2, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOR, semicolon2, Direction.AFTER);
        return super.visit(node);
    }

    @Override
    public boolean visit(EnhancedForStatement node) {
        Token leftParen = tokenSequence.searchForwardInNode(TokenNameLPAREN, node);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FOR, leftParen, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FOR, leftParen, Direction.AFTER);
        Token rightParen = tokenSequence.searchForwardAfterNode(TokenNameRPAREN, node.getExpression());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FOR, rightParen, Direction.BEFORE);
        Token colon = tokenSequence.searchForwardAfterNode(TokenNameCOLON, node.getParameter());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_FOR, colon, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COLON_IN_FOR, colon, Direction.AFTER);
        return super.visit(node);
    }
}
