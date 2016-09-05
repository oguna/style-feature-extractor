package ffe.whitespace.parameterized;

import ffe.FeatureCollector;
import ffe.Token;
import ffe.TokenSequence;
import ffe.whitespace.Direction;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.*;

public class TypeArgumentsVisitor extends WhiteSpaceVisitor {

    public TypeArgumentsVisitor(@NotNull TokenSequence tokenSequence, @NotNull FeatureCollector featureCollector) {
        super(tokenSequence, featureCollector);
    }

    @Override
    public boolean visit(MethodInvocation node) {
        if (node.typeArguments().size() > 0) {
            Token less = tokenSequence.searchForwardInNode(TokenNameLESS, node);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_ANGLE_BRACKET_IN_TYPE_ARGUMENTS, less, Direction.BEFORE);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_ANGLE_BRACKET_IN_TYPE_ARGUMENTS, less, Direction.AFTER);
            Token closing = tokenSequence.searchForwardAfterNode(Arrays.asList(TokenNameGREATER ,TokenNameRIGHT_SHIFT) , (ASTNode) node.typeArguments().get(node.typeArguments().size() - 1));
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_ANGLE_BRACKET_IN_TYPE_ARGUMENTS, closing, Direction.BEFORE);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_CLOSING_ANGLE_BRACKET_IN_TYPE_ARGUMENTS, closing, Direction.AFTER);
        }
        if (node.typeArguments().size() > 1) {
            for (int i = 0; i < node.typeArguments().size() - 1; i++) {
                Type type = (Type)node.typeArguments().get(i);
                Token comma = tokenSequence.searchForwardAfterNode(TokenNameCOMMA, type);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_TYPE_ARGUMENTS, comma, Direction.BEFORE);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_TYPE_ARGUMENTS, comma, Direction.AFTER);
            }
        }
        return super.visit(node);
    }
}
