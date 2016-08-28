package ffe.whitespace.declaration;

import ffe.FeatureCollector;
import ffe.Token;
import ffe.TokenSequence;
import ffe.whitespace.Direction;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.jetbrains.annotations.NotNull;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.*;

public class EnumVisitor extends WhiteSpaceVisitor {

    public EnumVisitor(@NotNull TokenSequence tokenSequence, @NotNull FeatureCollector featureCollector) {
        super(tokenSequence, featureCollector);
    }

    @Override
    public boolean visit(EnumDeclaration node) {
        Token leftBrace = tokenSequence.searchForwardInNode(TokenNameLBRACE, node);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_ENUM_DECLARATION, leftBrace, Direction.BEFORE);
        if (node.enumConstants().size() > 1) {
            for (int i = 0; i < node.enumConstants().size() - 1; i++) {
                EnumConstantDeclaration ecd = (EnumConstantDeclaration) node.enumConstants().get(i);
                Token comma = tokenSequence.searchForwardAfterNode(TokenNameCOMMA, ecd);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ENUM_DECLARATIONS, comma, Direction.BEFORE);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ENUM_DECLARATIONS, comma, Direction.AFTER);
            }
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(EnumConstantDeclaration node) {
        Token leftParen = tokenSequence.searchForwardInNode(TokenNameLPAREN, node);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ENUM_CONSTANT, leftParen, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_ENUM_CONSTANT, leftParen, Direction.AFTER);
        Token rightParen = tokenSequence.searchBackwardInNode(TokenNameRPAREN, node);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_ENUM_CONSTANT, rightParen, Direction.BEFORE);
        if (node.arguments().size() > 1) {
            for (int i = 0; i < node.arguments().size() - 1; i++) {
                Expression expression = (Expression)node.arguments().get(i);
                Token comma = tokenSequence.searchForwardAfterNode(TokenNameCOMMA, expression);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ENUM_CONSTANT_ARGUMENTS, comma, Direction.BEFORE);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ENUM_CONSTANT_ARGUMENTS, comma, Direction.AFTER);
            }
        } else if (node.arguments().size() == 0) {
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_ENUM_CONSTANT, leftParen, rightParen);
        }
        if (node.getAnonymousClassDeclaration() != null) {
            Token brace = tokenSequence.searchForwardInNode(TokenNameRBRACE, node.getAnonymousClassDeclaration());
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_ENUM_CONSTANT, brace, Direction.BEFORE);
        }
        return super.visit(node);
    }
}
