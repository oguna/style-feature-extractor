package ffe.whitespace.declaration;

import ffe.Token;
import ffe.whitespace.Direction;
import ffe.whitespace.FeatureCollector;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameCOMMA;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameLPAREN;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameRPAREN;

public class ConstructorVisitor extends WhiteSpaceVisitor {
    public ConstructorVisitor(char[] source, FeatureCollector featureCollector) {
        super(source, featureCollector);
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        if (node.isConstructor()) {
            Token leftParen = searchForward(TokenNameLPAREN, node.getStartPosition());
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CONSTRUCTOR_DECLARATION, leftParen, Direction.BEFORE);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_CONSTRUCTOR_DECLARATION, leftParen, Direction.AFTER);
            Token rightParen = searchForward(TokenNameRPAREN, node.getStartPosition());
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_CONSTRUCTOR_DECLARATION, rightParen, Direction.BEFORE);
            if (node.parameters().size() == 0) {
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_CONSTRUCTOR_DECLARATION, leftParen, rightParen);
            }
            if (node.parameters().size() > 1) {
                for (int i = 0; i < node.parameters().size() - 1; i++) {
                    SingleVariableDeclaration svd = (SingleVariableDeclaration)node.parameters().get(i);
                    Token comma = searchForward(TokenNameCOMMA, svd.getStartPosition() + svd.getLength());
                    collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_CONSTRUCTOR_DECLARATION_PARAMETERS, comma, Direction.BEFORE);
                    collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_CONSTRUCTOR_DECLARATION_PARAMETERS, comma, Direction.AFTER);
                }
            }
            if (node.thrownExceptionTypes().size() > 1) {
                for (int i = 0; i < node.thrownExceptionTypes().size() - 1; i++) {
                    Type type = (Type)node.thrownExceptionTypes().get(i);
                    Token comma = searchForward(TokenNameCOMMA, type.getStartPosition() + type.getLength());
                    collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_CONSTRUCTOR_DECLARATION_THROWS, comma, Direction.BEFORE);
                    collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_CONSTRUCTOR_DECLARATION_THROWS, comma, Direction.AFTER);
                }
            }
        }
        return super.visit(node);
    }
}
