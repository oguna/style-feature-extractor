package ffe.whitespace.declaration;

import ffe.FeatureCollector;
import ffe.Token;
import ffe.TokenSequence;
import ffe.whitespace.Direction;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.jetbrains.annotations.NotNull;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameCOMMA;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameLPAREN;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameRPAREN;

public class ConstructorVisitor extends WhiteSpaceVisitor {
    public ConstructorVisitor(@NotNull TokenSequence tokenSequence, @NotNull FeatureCollector featureCollector) {
        super(tokenSequence, featureCollector);
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        if (node.isConstructor()) {
            Token leftParen = tokenSequence.searchForwardInNode(TokenNameLPAREN, node);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CONSTRUCTOR_DECLARATION, leftParen, Direction.BEFORE);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_CONSTRUCTOR_DECLARATION, leftParen, Direction.AFTER);
            Token rightParen = tokenSequence.searchForwardInNode(TokenNameRPAREN, node);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_CONSTRUCTOR_DECLARATION, rightParen, Direction.BEFORE);
            if (node.parameters().size() == 0) {
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_CONSTRUCTOR_DECLARATION, leftParen, rightParen);
            }
            if (node.parameters().size() > 1) {
                for (int i = 0; i < node.parameters().size() - 1; i++) {
                    SingleVariableDeclaration svd = (SingleVariableDeclaration)node.parameters().get(i);
                    Token comma = tokenSequence.searchForwardAfterNode(TokenNameCOMMA, svd);
                    collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_CONSTRUCTOR_DECLARATION_PARAMETERS, comma, Direction.BEFORE);
                    collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_CONSTRUCTOR_DECLARATION_PARAMETERS, comma, Direction.AFTER);
                }
            }
            if (node.thrownExceptionTypes().size() > 1) {
                for (int i = 0; i < node.thrownExceptionTypes().size() - 1; i++) {
                    Type type = (Type)node.thrownExceptionTypes().get(i);
                    Token comma = tokenSequence.searchForwardAfterNode(TokenNameCOMMA, type);
                    collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_CONSTRUCTOR_DECLARATION_THROWS, comma, Direction.BEFORE);
                    collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_CONSTRUCTOR_DECLARATION_THROWS, comma, Direction.AFTER);
                }
            }
        }
        return super.visit(node);
    }
}
