package ffe.whitespace.statement;

import ffe.FeatureCollector;
import ffe.Token;
import ffe.TokenSequence;
import ffe.whitespace.Direction;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.jetbrains.annotations.NotNull;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameLBRACE;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameRBRACE;

public class BlockStatementWhiteSpaceVisitor extends WhiteSpaceVisitor {

    public BlockStatementWhiteSpaceVisitor(@NotNull TokenSequence tokenSequence, @NotNull FeatureCollector featureCollector) {
        super(tokenSequence, featureCollector);
    }

    @Override
    public boolean visit(Block node) {
        if ((node.getParent() instanceof Statement || node.getParent() instanceof MethodDeclaration) && node.statements().size() > 0) {
            // TODO: I also want to analyze when statements.size == 0
            Token openingBrace = tokenSequence.searchForwardInNode(TokenNameLBRACE, node);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACE_IN_BLOCK, openingBrace, Direction.BEFORE);

            Token closingBrace = tokenSequence.searchBackwardInNode(TokenNameRBRACE, node);
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_CLOSING_BRACE_IN_BLOCK, closingBrace, Direction.AFTER);
        }
        return super.visit(node);
    }
}
