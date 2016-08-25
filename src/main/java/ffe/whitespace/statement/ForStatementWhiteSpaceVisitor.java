package ffe.whitespace.statement;

import ffe.Token;
import ffe.whitespace.Direction;
import ffe.whitespace.FeatureCollector;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

import java.util.List;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.*;

public class ForStatementWhiteSpaceVisitor extends WhiteSpaceVisitor {

    public ForStatementWhiteSpaceVisitor(char[] source, FeatureCollector featureCollector) {
        super(source, featureCollector);
    }

    @Override
    public boolean visit(ForStatement node) {
        Token leftParen = searchForward(TokenNameLPAREN, node.getStartPosition());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FOR, leftParen, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FOR, leftParen, Direction.AFTER);
        Token rightParen = searchForward(TokenNameRPAREN, node.getExpression().getStartPosition());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FOR, rightParen, Direction.BEFORE);
        List initList = node.initializers();
        int semicolon1Pos = node.getStartPosition();
        for (Object initNode : initList) {
            if (initNode instanceof Assignment) {
                semicolon1Pos = ((Assignment) initNode).getStartPosition() + ((Assignment) initNode).getLength();
            } else {
                VariableDeclarationExpression  vde = (VariableDeclarationExpression )initNode;
                List vdeFragments = vde.fragments();
                if (vdeFragments.size() >= 2) {
                    for (int i = 0; i < vdeFragments.size() - 1; i++) {
                        VariableDeclarationFragment vdef = (VariableDeclarationFragment) vdeFragments.get(i);
                        Token comma = searchForward(TokenNameCOMMA, vdef.getStartPosition() + vdef.getLength());
                        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FOR_INITS, comma, Direction.BEFORE);
                        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FOR_INITS, comma, Direction.AFTER);
                    }
                }
                VariableDeclarationExpression lastVde = (VariableDeclarationExpression)initList.get(initList.size() - 1);
                semicolon1Pos = lastVde.getStartPosition() + lastVde.getLength();
            }
        }
        Token semicolon1 = searchForward(TokenNameSEMICOLON, semicolon1Pos);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOR, semicolon1, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOR, semicolon1, Direction.AFTER);
        Token semicolon2 = searchForward(TokenNameSEMICOLON, node.getExpression().getStartPosition() + node.getExpression().getLength());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOR, semicolon2, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOR, semicolon2, Direction.AFTER);
        return super.visit(node);
    }

    @Override
    public boolean visit(EnhancedForStatement node) {
        Token leftParen = searchForward(TokenNameLPAREN, node.getStartPosition());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FOR, leftParen, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FOR, leftParen, Direction.AFTER);
        Token rightParen = searchForward(TokenNameRPAREN, node.getExpression().getStartPosition());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FOR, rightParen, Direction.BEFORE);
        Token colon = searchForward(TokenNameCOLON, node.getParameter().getStartPosition() + node.getParameter().getLength());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_FOR, colon, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COLON_IN_FOR, colon, Direction.AFTER);
        return super.visit(node);
    }
}
