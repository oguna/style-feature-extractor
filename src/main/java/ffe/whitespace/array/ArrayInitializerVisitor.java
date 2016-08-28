package ffe.whitespace.array;

import ffe.FeatureCollector;
import ffe.Token;
import ffe.TokenSequence;
import ffe.whitespace.Direction;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.jetbrains.annotations.NotNull;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.*;

public class ArrayInitializerVisitor extends WhiteSpaceVisitor {
    public ArrayInitializerVisitor(@NotNull TokenSequence tokenSequence, @NotNull FeatureCollector featureCollector) {
        super(tokenSequence, featureCollector);
    }

    @Override
    public boolean visit(ArrayInitializer node) {
        Token leftBrace = tokenSequence.searchForwardInNode(TokenNameLBRACKET, node);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_ARRAY_INITIALIZER, leftBrace, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_BRACE_IN_ARRAY_INITIALIZER, leftBrace, Direction.AFTER);
        Token rightBrace = tokenSequence.searchForwardAfterNode(TokenNameRBRACKET, node);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_BRACE_IN_ARRAY_INITIALIZER, rightBrace, Direction.BEFORE);
        if (node.expressions().size() == 0) {
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_BRACES_IN_ARRAY_INITIALIZER, leftBrace, rightBrace);
        } else if (node.expressions().size() > 1) {
            for (int i = 0; i < node.expressions().size() - 1; i++) {
                Expression  expression = (Expression)node.expressions().get(i);
                Token comma = tokenSequence.searchForwardAfterNode(TokenNameCOMMA, expression);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ARRAY_INITIALIZER, comma, Direction.BEFORE);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ARRAY_INITIALIZER, comma, Direction.AFTER);
            }
        }
        return super.visit(node);
    }
}
