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
        Token rightParen = tokenSequence.searchBackwardBeforeNode(TokenNameRPAREN, node.getBody());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FOR, rightParen, Direction.BEFORE);
        List initList = node.initializers();
        if (initList.size() == 1 && initList.get(0) instanceof VariableDeclarationExpression) {
            VariableDeclarationExpression vde = (VariableDeclarationExpression) initList.get(0);
            List vdeFragments = vde.fragments();
            if (vdeFragments.size() >= 2) {
                for (int i = 0; i < vdeFragments.size() - 1; i++) {
                    VariableDeclarationFragment vdef = (VariableDeclarationFragment) vdeFragments.get(i);
                    Token comma = tokenSequence.searchForwardAfterNode(TokenNameCOMMA, vdef);
                    collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FOR_INITS, comma, Direction.BEFORE);
                    collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FOR_INITS, comma, Direction.AFTER);
                }
            }
        } else if (initList.size() >= 2) {
            for (int i = 0; i < initList.size() - 1; i++) {
                Token comma = tokenSequence.searchForwardAfterNode(TokenNameCOMMA, (ASTNode) initList.get(i));
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FOR_INITS, comma, Direction.BEFORE);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FOR_INITS, comma, Direction.AFTER);
            }
        }
        Token semicolon1;
        if (node.initializers().size() == 0) {
            semicolon1 = tokenSequence.searchForwardInNode(TokenNameSEMICOLON, node);
        } else {
            semicolon1 = tokenSequence.searchForwardAfterNode(TokenNameSEMICOLON, (ASTNode) node.initializers().get(node.initializers().size()-1));
        }
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOR, semicolon1, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOR, semicolon1, Direction.AFTER);
        Token semicolon2;
        if (node.updaters().size() == 0) {
            semicolon2 = tokenSequence.searchBackwardBeforeNode(TokenNameSEMICOLON, node.getBody());
        } else {
            semicolon2 = tokenSequence.searchBackwardBeforeNode(TokenNameSEMICOLON, (ASTNode) node.updaters().get(node.updaters().size()-1));
        }
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
