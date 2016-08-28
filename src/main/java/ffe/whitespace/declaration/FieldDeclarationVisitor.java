package ffe.whitespace.declaration;

import ffe.FeatureCollector;
import ffe.Token;
import ffe.TokenSequence;
import ffe.whitespace.Direction;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.jetbrains.annotations.NotNull;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameCOMMA;

public class FieldDeclarationVisitor extends WhiteSpaceVisitor {


    public FieldDeclarationVisitor(@NotNull TokenSequence tokenSequence, @NotNull FeatureCollector featureCollector) {
        super(tokenSequence, featureCollector);
    }

    @Override
    public boolean visit(FieldDeclaration node) {
        if (node.fragments().size() > 1) {
            for (int i = 0; i < node.fragments().size() - 1; i++) {
                VariableDeclarationFragment fragment = (VariableDeclarationFragment) node.fragments().get(i);
                Token comma = tokenSequence.searchForwardAfterNode(TokenNameCOMMA, fragment);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS, comma, Direction.BEFORE);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS, comma, Direction.AFTER);
            }
        }
        return super.visit(node);
    }
}
