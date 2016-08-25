package ffe.whitespace.array;

import ffe.Token;
import ffe.whitespace.Direction;
import ffe.whitespace.FeatureCollector;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Dimension;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameLBRACKET;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameRBRACKET;

public class ArrayDeclarationVisitor extends WhiteSpaceVisitor {

    public ArrayDeclarationVisitor(char[] source, FeatureCollector featureCollector) {
        super(source, featureCollector);
    }

    @Override
    public boolean visit(SingleVariableDeclaration node) {
        if (node.getType() instanceof ArrayType) {
            ArrayType arrayType = (ArrayType)node.getType();
            for (Object i : arrayType.dimensions()) {
                Dimension dimension = (Dimension)i;
                Token left = searchForward(TokenNameLBRACKET, dimension.getStartPosition());
                Token right = searchForward(TokenNameRBRACKET, dimension.getStartPosition());
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACKET_IN_ARRAY_TYPE_REFERENCE, left, Direction.BEFORE);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_BRACKETS_IN_ARRAY_TYPE_REFERENCE, left, right);
            }
        }
        return super.visit(node);
    }
}
