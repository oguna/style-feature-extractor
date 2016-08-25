package ffe.whitespace.declaration;

import ffe.Token;
import ffe.whitespace.Direction;
import ffe.whitespace.FeatureCollector;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.*;

public class EnumVisitor extends WhiteSpaceVisitor {
    public EnumVisitor(char[] source, FeatureCollector featureCollector) {
        super(source, featureCollector);
    }

    @Override
    public boolean visit(EnumDeclaration node) {
        Token leftBrace = searchForward(TokenNameLBRACE, node.getStartPosition());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_ENUM_DECLARATION, leftBrace, Direction.BEFORE);
        if (node.enumConstants().size() > 1) {
            for (int i = 0; i < node.enumConstants().size() - 1; i++) {
                EnumConstantDeclaration ecd = (EnumConstantDeclaration) node.enumConstants().get(i);
                Token comma = searchForward(TokenNameCOMMA, ecd.getStartPosition() + ecd.getLength());
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ENUM_DECLARATIONS, comma, Direction.BEFORE);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ENUM_DECLARATIONS, comma, Direction.AFTER);
            }
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(EnumConstantDeclaration node) {
        Token leftParen = searchForward(TokenNameLPAREN, node.getStartPosition());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ENUM_CONSTANT, leftParen, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_ENUM_CONSTANT, leftParen, Direction.AFTER);
        Token rightParen = searchBackward(TokenNameRPAREN, node.getStartPosition() + node.getLength());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_ENUM_CONSTANT, rightParen, Direction.BEFORE);
        if (node.arguments().size() > 1) {
            for (int i = 0; i < node.arguments().size() - 1; i++) {
                Expression expression = (Expression)node.arguments().get(i);
                Token comma = searchForward(TokenNameCOMMA, expression.getStartPosition() + expression.getLength());
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ENUM_CONSTANT_ARGUMENTS, comma, Direction.BEFORE);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ENUM_CONSTANT_ARGUMENTS, comma, Direction.AFTER);
            }
        } else if (node.arguments().size() == 0) {
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_ENUM_CONSTANT, leftParen, rightParen);
        }
        if (node.getAnonymousClassDeclaration() != null) {
            Token brace = searchForward(TokenNameRBRACE, node.getAnonymousClassDeclaration().getStartPosition());
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_ENUM_CONSTANT, brace, Direction.BEFORE);
        }
        return super.visit(node);
    }
}
