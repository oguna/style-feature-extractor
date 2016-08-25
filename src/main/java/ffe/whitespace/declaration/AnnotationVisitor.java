package ffe.whitespace.declaration;

import ffe.Token;
import ffe.whitespace.Direction;
import ffe.whitespace.FeatureCollector;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.*;

public class AnnotationVisitor extends WhiteSpaceVisitor {
    public AnnotationVisitor(char[] source, FeatureCollector featureCollector) {
        super(source, featureCollector);
    }

    @Override
    public boolean visit(MarkerAnnotation node) {
        Token atmark = searchForward(TokenNameAT, node.getStartPosition());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_AT_IN_ANNOTATION, atmark, Direction.AFTER);
        return super.visit(node);
    }

    @Override
    public boolean visit(NormalAnnotation node) {
        Token atmark = searchForward(TokenNameAT, node.getStartPosition());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_AT_IN_ANNOTATION, atmark, Direction.AFTER);
        Token leftParen = searchForward(TokenNameLPAREN, node.getStartPosition());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ANNOTATION, leftParen, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_ANNOTATION, leftParen, Direction.AFTER);
        if (node.values().size() > 1) {
            for (int i = 0; i < node.values().size() - 1; i++) {
                MemberValuePair pair = (MemberValuePair) node.values().get(i);
                Token comma = searchForward(TokenNameCOMMA, pair.getStartPosition() + pair.getLength());
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ANNOTATION, comma, Direction.BEFORE);
                collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ANNOTATION, comma, Direction.AFTER);
            }
        }
        if (node.values().size() > 0) {
            MemberValuePair lastPair = (MemberValuePair)node.values().get(node.values().size() - 1);
            Token rightParent = searchForward(TokenNameRPAREN,lastPair.getStartPosition() + lastPair.getLength());
            collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_ANNOTATION, rightParent, Direction.BEFORE);
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(SingleMemberAnnotation node) {
        Token atmark = searchForward(TokenNameAT, node.getStartPosition());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_AT_IN_ANNOTATION, atmark, Direction.AFTER);
        Token leftParen = searchForward(TokenNameLPAREN, node.getStartPosition());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ANNOTATION, leftParen, Direction.BEFORE);
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_ANNOTATION, leftParen, Direction.AFTER);
        Token rightParent = searchForward(TokenNameRPAREN, node.getValue().getStartPosition() + node.getValue().getLength());
        collectFeature(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_ANNOTATION, rightParent, Direction.BEFORE);
        return super.visit(node);
    }
}
