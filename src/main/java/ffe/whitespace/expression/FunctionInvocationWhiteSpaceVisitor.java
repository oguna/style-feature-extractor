package ffe.whitespace.expression;

import ffe.FeatureCollector;
import ffe.Token;
import ffe.TokenSequence;
import ffe.whitespace.Direction;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.jetbrains.annotations.NotNull;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.*;

public class FunctionInvocationWhiteSpaceVisitor extends WhiteSpaceVisitor {

    public FunctionInvocationWhiteSpaceVisitor(@NotNull TokenSequence tokenSequence, @NotNull FeatureCollector featureCollector) {
        super(tokenSequence, featureCollector);
    }

    @Override
    public boolean visit(MethodInvocation node) {
        Token leftParen = tokenSequence.searchForwardAfterNode(TokenNameLPAREN, node.getName());
        Token rightParen = tokenSequence.searchBackwardInNode(TokenNameRPAREN, node);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_METHOD_INVOCATION, leftParen, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_METHOD_INVOCATION, leftParen, Direction.AFTER);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_METHOD_INVOCATION, rightParen, Direction.BEFORE);
        if (node.arguments().size() > 1) {
            for (int i = 0; i < node.arguments().size() - 1; i++) {
                Expression argument = (Expression)node.arguments().get(i);
                Token comma = tokenSequence.searchForwardAfterNode(TokenNameCOMMA, argument);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_METHOD_INVOCATION_ARGUMENTS, comma, Direction.BEFORE);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_METHOD_INVOCATION_ARGUMENTS, comma, Direction.AFTER);
            }
        } else if (node.arguments().size() == 0) {
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_METHOD_INVOCATION, leftParen, rightParen);
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(ClassInstanceCreation node) {
        Token leftParen = tokenSequence.searchForwardInNode(TokenNameLPAREN, node);
        Token rightParen = tokenSequence.searchBackwardInNode(TokenNameRPAREN, node);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_METHOD_INVOCATION, leftParen, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_METHOD_INVOCATION, leftParen, Direction.AFTER);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_METHOD_INVOCATION, rightParen, Direction.BEFORE);
        if (node.arguments().size() > 1) {
            for (int i = 0; i < node.arguments().size() - 1; i++) {
                Expression argument = (Expression)node.arguments().get(i);
                Token comma = tokenSequence.searchForwardAfterNode(TokenNameCOMMA, argument);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ALLOCATION_EXPRESSION, comma, Direction.BEFORE);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ALLOCATION_EXPRESSION, comma, Direction.AFTER);
            }
        } else if (node.arguments().size() == 0) {
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_METHOD_INVOCATION, leftParen, rightParen);
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(SuperMethodInvocation node) {
        Token leftParen = tokenSequence.searchForwardInNode(TokenNameLPAREN, node);
        Token rightParen = tokenSequence.searchBackwardInNode(TokenNameRPAREN, node);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_METHOD_INVOCATION, leftParen, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_METHOD_INVOCATION, leftParen, Direction.AFTER);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_METHOD_INVOCATION, rightParen, Direction.BEFORE);
        if (node.arguments().size() > 1) {
            for (int i = 0; i < node.arguments().size() - 1; i++) {
                Expression argument = (Expression)node.arguments().get(i);
                Token comma = tokenSequence.searchForwardAfterNode(TokenNameCOMMA, argument);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_EXPLICIT_CONSTRUCTOR_CALL_ARGUMENTS, comma, Direction.BEFORE);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_EXPLICIT_CONSTRUCTOR_CALL_ARGUMENTS, comma, Direction.AFTER);
            }
        } else if (node.arguments().size() == 0) {
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_METHOD_INVOCATION, leftParen, rightParen);
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(ConstructorInvocation node) {
        Token leftParen = tokenSequence.searchForwardInNode(TokenNameLPAREN, node);
        Token rightParen = tokenSequence.searchBackwardInNode(TokenNameRPAREN, node);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_METHOD_INVOCATION, leftParen, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_METHOD_INVOCATION, leftParen, Direction.AFTER);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_METHOD_INVOCATION, rightParen, Direction.BEFORE);
        if (node.arguments().size() > 1) {
            for (int i = 0; i < node.arguments().size() - 1; i++) {
                Expression argument = (Expression)node.arguments().get(i);
                Token comma = tokenSequence.searchForwardAfterNode(TokenNameCOMMA, argument);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_EXPLICIT_CONSTRUCTOR_CALL_ARGUMENTS, comma, Direction.BEFORE);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_EXPLICIT_CONSTRUCTOR_CALL_ARGUMENTS, comma, Direction.AFTER);
            }
        } else if (node.arguments().size() == 0) {
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_METHOD_INVOCATION, leftParen, rightParen);
        }
        return super.visit(node);
    }

}
