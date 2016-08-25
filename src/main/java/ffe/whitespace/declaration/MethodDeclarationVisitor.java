package ffe.whitespace.declaration;

import ffe.Token;
import ffe.whitespace.Direction;
import ffe.whitespace.FeatureCollector;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.*;

public class MethodDeclarationVisitor extends WhiteSpaceVisitor {
    public MethodDeclarationVisitor(char[] source, FeatureCollector featureCollector) {
        super(source, featureCollector);
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        Token leftParen = searchForward(TokenNameLPAREN, node.getStartPosition());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_METHOD_DECLARATION, leftParen, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_METHOD_DECLARATION, leftParen, Direction.AFTER);
        Token rightParen = searchForward(TokenNameRPAREN, node.getStartPosition());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_METHOD_DECLARATION, rightParen, Direction.BEFORE);
        if (node.parameters().size() == 0) {
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_METHOD_DECLARATION, leftParen, rightParen);
        }
        if (node.parameters().size() > 1) {
            for (int i = 0; i < node.parameters().size() - 1; i++) {
                SingleVariableDeclaration svd = (SingleVariableDeclaration)node.parameters().get(i);
                Token comma = searchForward(TokenNameCOMMA, svd.getStartPosition() + svd.getLength());
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_METHOD_DECLARATION_PARAMETERS, comma, Direction.BEFORE);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_METHOD_DECLARATION_PARAMETERS, comma, Direction.AFTER);
            }
        }
        for (Object i : node.parameters()) {
            SingleVariableDeclaration svd = (SingleVariableDeclaration)i;
            if (svd.isVarargs()) {
                Token dotdotdot = searchForward(TokenNameAT308DOTDOTDOT, svd.getStartPosition() + svd.getLength());
                // TODO
            }
        }
        if (node.thrownExceptionTypes().size() > 1) {
            for (int i = 0; i < node.thrownExceptionTypes().size() - 1; i++) {
                Type type = (Type)node.thrownExceptionTypes().get(i);
                Token comma = searchForward(TokenNameCOMMA, type.getStartPosition() + type.getLength());
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_METHOD_DECLARATION_THROWS, comma, Direction.BEFORE);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_METHOD_DECLARATION_THROWS, comma, Direction.AFTER);
            }
        }
        return super.visit(node);
    }
}
