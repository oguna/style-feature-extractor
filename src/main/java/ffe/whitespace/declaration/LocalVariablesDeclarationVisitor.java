package ffe.whitespace.declaration;

import ffe.Token;
import ffe.whitespace.Direction;
import ffe.whitespace.FeatureCollector;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

import java.util.List;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameCOMMA;

public class LocalVariablesDeclarationVisitor extends WhiteSpaceVisitor {
    public LocalVariablesDeclarationVisitor(char[] source, FeatureCollector featureCollector) {
        super(source, featureCollector);
    }
    @Override
    public boolean visit(VariableDeclarationStatement node) {
        List fragment = node.fragments();
        if (fragment.size() > 1) {
            for (int i = 0; i < fragment.size() - 1; i++) {
                VariableDeclarationFragment vdf = (VariableDeclarationFragment)fragment.get(i);
                Token comma = searchForward(TokenNameCOMMA, vdf.getStartPosition() + vdf.getLength());
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_MULTIPLE_LOCAL_DECLARATIONS, comma, Direction.BEFORE);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_MULTIPLE_LOCAL_DECLARATIONS, comma, Direction.AFTER);
            }
        }
        return super.visit(node);
    }
}
