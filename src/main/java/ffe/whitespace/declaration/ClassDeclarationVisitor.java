package ffe.whitespace.declaration;

import ffe.FeatureCollector;
import ffe.Token;
import ffe.TokenSequence;
import ffe.whitespace.Direction;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.jetbrains.annotations.NotNull;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameCOMMA;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameLBRACE;

public class ClassDeclarationVisitor extends WhiteSpaceVisitor {

    public ClassDeclarationVisitor(@NotNull TokenSequence tokenSequence, @NotNull FeatureCollector featureCollector) {
        super(tokenSequence, featureCollector);
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        Token openingBrace = tokenSequence.searchForwardInNode(TokenNameLBRACE, node);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_TYPE_DECLARATION, openingBrace, Direction.BEFORE);
        if (node.superInterfaceTypes().size() > 1) {
            for (int i = 0; i < node.superInterfaceTypes().size() - 1; i++) {
                Type type = (Type) node.superInterfaceTypes().get(i);
                Token comma = tokenSequence.searchForwardAfterNode(TokenNameCOMMA, type);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_SUPERINTERFACES, comma, Direction.BEFORE);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_SUPERINTERFACES, comma, Direction.AFTER);
            }
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(AnonymousClassDeclaration node) {
        Token openingBrace = tokenSequence.searchForwardInNode(TokenNameLBRACE, node);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_TYPE_DECLARATION, openingBrace, Direction.BEFORE);
        return super.visit(node);
    }
}
