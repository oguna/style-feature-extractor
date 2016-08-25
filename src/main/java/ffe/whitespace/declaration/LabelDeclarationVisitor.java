package ffe.whitespace.declaration;

import ffe.Token;
import ffe.whitespace.Direction;
import ffe.whitespace.FeatureCollector;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameCOLON;

public class LabelDeclarationVisitor extends WhiteSpaceVisitor {

    public LabelDeclarationVisitor(char[] source, FeatureCollector featureCollector) {
        super(source, featureCollector);
    }

    public boolean visit(LabeledStatement node) {
        Token token = searchForward(TokenNameCOLON, node.getStartPosition());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_LABELED_STATEMENT, token, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COLON_IN_LABELED_STATEMENT, token, Direction.AFTER);
        return super.visit(node);
    }
}
