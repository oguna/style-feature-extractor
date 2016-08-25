package ffe.whitespace.parameterized;

import ffe.Token;
import ffe.whitespace.Direction;
import ffe.whitespace.FeatureCollector;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.*;

public class TypeParametersVisitor extends WhiteSpaceVisitor {
    public TypeParametersVisitor(char[] source, FeatureCollector featureCollector) {
        super(source, featureCollector);
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        if (node.typeParameters().size() > 0) {
            Token less = searchForward(TokenNameLESS, node.getStartPosition());
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_ANGLE_BRACKET_IN_TYPE_PARAMETERS, less, Direction.BEFORE);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_ANGLE_BRACKET_IN_TYPE_PARAMETERS, less, Direction.AFTER);
            Token greater = searchForward(TokenNameGREATER, node.getStartPosition());
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_ANGLE_BRACKET_IN_TYPE_PARAMETERS, greater, Direction.BEFORE);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_CLOSING_ANGLE_BRACKET_IN_TYPE_PARAMETERS, greater, Direction.AFTER);
            if (node.typeParameters().size() > 1) {
                for (int i = 0; i < node.typeParameters().size() - 1; i++) {
                    TypeParameter type = (TypeParameter)node.typeParameters().get(i);
                    Token comma = searchForward(TokenNameCOMMA, type.getStartPosition() + type.getLength());
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
                Token and = searchForward(TokenNameAND, type.getStartPosition() + type.getLength());
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_AND_IN_TYPE_PARAMETER, and, Direction.BEFORE);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_AND_IN_TYPE_PARAMETER, and, Direction.AFTER);
            }
        }
        return super.visit(node);
    }
}
