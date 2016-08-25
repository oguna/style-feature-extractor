package ffe.whitespace.parameterized;

import ffe.Token;
import ffe.whitespace.Direction;
import ffe.whitespace.FeatureCollector;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameCOMMA;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameLESS;

public class TypeReferenceVisitor extends WhiteSpaceVisitor {
    public TypeReferenceVisitor(char[] source, FeatureCollector featureCollector) {
        super(source, featureCollector);
    }

    @Override
    public boolean visit(ParameterizedType node) {
        Token less = searchForward(TokenNameLESS, node.getType().getStartPosition() + node.getType().getLength());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_ANGLE_BRACKET_IN_PARAMETERIZED_TYPE_REFERENCE, less, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_ANGLE_BRACKET_IN_PARAMETERIZED_TYPE_REFERENCE, less, Direction.AFTER);
        //Token gt = searchBackward(TokenNameGREATER, node.getStartPosition() + node.getLength());
        //collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_ANGLE_BRACKET_IN_PARAMETERIZED_TYPE_REFERENCE, gt, Direction.BEFORE);
        if (node.typeArguments().size() > 1) {
            for (int i = 0; i < node.typeArguments().size() - 1; i++) {
                Type type = (Type)node.typeArguments().get(i);
                Token comma = searchForward(TokenNameCOMMA, type.getStartPosition() + type.getLength());
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_PARAMETERIZED_TYPE_REFERENCE, comma, Direction.BEFORE);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_PARAMETERIZED_TYPE_REFERENCE, comma, Direction.AFTER);
            }
        }
        return super.visit(node);
    }
}
