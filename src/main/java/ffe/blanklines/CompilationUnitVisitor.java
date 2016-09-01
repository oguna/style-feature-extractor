package ffe.blanklines;

import ffe.FeatureCollector;
import ffe.FormatFeatureVisitor;
import ffe.TokenSequence;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

public class CompilationUnitVisitor extends FormatFeatureVisitor {
    public CompilationUnitVisitor(TokenSequence tokenSequence, FeatureCollector featureCollector) {
        super(tokenSequence, featureCollector);
    }

    @Override
    public boolean visit(PackageDeclaration node) {
        //collectBlackLineFeature(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_PACKAGE, node);
        return super.visit(node);
    }

    @Override
    public boolean visit(ImportDeclaration node) {
        //collectBlackLineFeature(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_PACKAGE, node);
        return super.visit(node);
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        return super.visit(node);
    }
}
