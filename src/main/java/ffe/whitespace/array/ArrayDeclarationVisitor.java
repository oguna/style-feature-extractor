package ffe.whitespace.array;

import ffe.FeatureCollector;
import ffe.Token;
import ffe.TokenSequence;
import ffe.whitespace.Direction;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Dimension;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.jetbrains.annotations.NotNull;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameLBRACKET;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameRBRACKET;

public class ArrayDeclarationVisitor extends WhiteSpaceVisitor {


    public ArrayDeclarationVisitor(@NotNull TokenSequence tokenSequence, @NotNull FeatureCollector featureCollector) {
        super(tokenSequence, featureCollector);
    }

    @Override
    public boolean visit(SingleVariableDeclaration node) {
        if (node.getType() instanceof ArrayType) {
            ArrayType arrayType = (ArrayType)node.getType();
            for (Object i : arrayType.dimensions()) {
                Dimension dimension = (Dimension)i;
                Token left = tokenSequence.searchForwardInNode(TokenNameLBRACKET, dimension);
                Token right = tokenSequence.searchForwardInNode(TokenNameRBRACKET, dimension);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACKET_IN_ARRAY_TYPE_REFERENCE, left, Direction.BEFORE);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_BRACKETS_IN_ARRAY_TYPE_REFERENCE, left, right);
            }
        }
        return super.visit(node);
    }
}
