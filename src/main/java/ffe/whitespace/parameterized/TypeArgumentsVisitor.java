package ffe.whitespace.parameterized;

import ffe.FeatureCollector;
import ffe.Token;
import ffe.TokenSequence;
import ffe.whitespace.Direction;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.*;

public class TypeArgumentsVisitor extends WhiteSpaceVisitor {

    public TypeArgumentsVisitor(@NotNull TokenSequence tokenSequence, @NotNull FeatureCollector featureCollector) {
        super(tokenSequence, featureCollector);
    }

    private void handleTypeArguments(List<Type> typeArguments) {
        if (typeArguments.isEmpty()) {
            return;
        }
        handleTokenBefore(typeArguments.get(0), TokenNameLESS,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_ANGLE_BRACKET_IN_TYPE_ARGUMENTS,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_ANGLE_BRACKET_IN_TYPE_ARGUMENTS);
        //handleTokenAfter(typeArguments.get(typeArguments.size() - 1), TokenNameGREATER,
        //        DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_ANGLE_BRACKET_IN_TYPE_ARGUMENTS,
        //        DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_CLOSING_ANGLE_BRACKET_IN_TYPE_ARGUMENTS);
        handleCommas(typeArguments,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_TYPE_ARGUMENTS,
                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_TYPE_ARGUMENTS);

    }

    private void handleCommas(List<? extends ASTNode> nodes, String spaceBefore, String spaceAfter) {
        if (spaceBefore != null || spaceAfter != null) {
            for (int i = 1; i <  nodes.size(); i++) {
                handleTokenBefore(nodes.get(i), TokenNameCOMMA, spaceBefore, spaceAfter);
            }
        }
    }

    private void handleTokenAfter(ASTNode node, int tokenType, String spaceBefore, String spaceAfter) {
        if (spaceBefore != null || spaceAfter != null) {
            Token token = tokenSequence.searchForwardAfterNode(tokenType, node);
            handleToken(token, spaceBefore, spaceAfter);
        }
    }

    private void handleTokenBefore(ASTNode node, int tokenType, String spaceBefore, String spaceAfter) {
        if (spaceBefore != null || spaceAfter != null) {
            Token token = tokenSequence.searchBackwardBeforeNode(tokenType, node);
            handleToken(token, spaceBefore, spaceAfter);
        }
    }

    private void handleToken(Token token, String spaceBefore, String spaceAfter) {
        if (spaceBefore != null) {
            collectFeature(spaceBefore, token, Direction.BEFORE);
        }
        if (spaceAfter != null) {
            collectFeature(spaceAfter, token, Direction.AFTER);
        }
    }

    @Override
    public boolean visit(MethodInvocation node) {
        handleTypeArguments(node.typeArguments());
        return true;
    }

    @Override
    public boolean visit(SuperMethodInvocation node) {
        handleTypeArguments(node.typeArguments());
        return true;
    }

    @Override
    public boolean visit(ClassInstanceCreation node) {
        List<Type> typeArguments = node.typeArguments();
        handleTypeArguments(typeArguments);
        return true;
    }

    @Override
    public boolean visit(ConstructorInvocation node) {
        handleTypeArguments(node.typeArguments());
        return true;
    }

    @Override
    public boolean visit(SuperConstructorInvocation node) {
        handleTypeArguments(node.typeArguments());
        return true;
    }

    @Override
    public boolean visit(TypeMethodReference node) {
        handleTypeArguments(node.typeArguments());
        return true;
    }

    @Override
    public boolean visit(ExpressionMethodReference node) {
        handleTypeArguments(node.typeArguments());
        return true;
    }

    @Override
    public boolean visit(SuperMethodReference node) {
        handleTypeArguments(node.typeArguments());
        return true;
    }

    @Override
    public boolean visit(CreationReference node) {
        handleTypeArguments(node.typeArguments());
        return true;
    }



}
