package ffe.whitespace.parameterized;

import ffe.FeatureCollector;
import ffe.Token;
import ffe.TokenSequence;
import ffe.whitespace.Direction;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.*;

public class TypeParametersVisitor extends WhiteSpaceVisitor {

    public TypeParametersVisitor(@NotNull TokenSequence tokenSequence, @NotNull FeatureCollector featureCollector) {
        super(tokenSequence, featureCollector);
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        if (node.typeParameters().size() > 0) {
            Token less = tokenSequence.searchForwardInNode(TokenNameLESS, node);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_ANGLE_BRACKET_IN_TYPE_PARAMETERS, less, Direction.BEFORE);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_ANGLE_BRACKET_IN_TYPE_PARAMETERS, less, Direction.AFTER);
            Token greater = tokenSequence.searchForwardInNode(Arrays.asList(TokenNameGREATER ,TokenNameRIGHT_SHIFT), node);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_ANGLE_BRACKET_IN_TYPE_PARAMETERS, greater, Direction.BEFORE);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_CLOSING_ANGLE_BRACKET_IN_TYPE_PARAMETERS, greater, Direction.AFTER);
            if (node.typeParameters().size() > 1) {
                for (int i = 0; i < node.typeParameters().size() - 1; i++) {
                    TypeParameter type = (TypeParameter)node.typeParameters().get(i);
                    Token comma = tokenSequence.searchForwardAfterNode(TokenNameCOMMA, type);
                    collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_TYPE_PARAMETERS, comma, Direction.BEFORE);
                    collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_TYPE_PARAMETERS, comma, Direction.AFTER);
                }
            }
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(TypeParameter node) {
        if (node.typeBounds().size() > 1) {
            for (int i = 0; i < node.typeBounds().size() - 1; i++) {
                Type type = (Type)node.typeBounds().get(i);
                Token and = tokenSequence.searchForwardAfterNode(TokenNameAND, type);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_AND_IN_TYPE_PARAMETER, and, Direction.BEFORE);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_AND_IN_TYPE_PARAMETER, and, Direction.AFTER);
            }
        }
        return super.visit(node);
    }
}
