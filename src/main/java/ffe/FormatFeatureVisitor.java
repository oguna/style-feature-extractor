package ffe;

import org.eclipse.jdt.core.dom.ASTVisitor;

public abstract class FormatFeatureVisitor extends ASTVisitor {
    protected final TokenSequence tokenSequence;
    protected final FeatureCollector featureCollector;

    public FormatFeatureVisitor(TokenSequence tokenSequence, FeatureCollector featureCollector) {
        this.tokenSequence = tokenSequence;
        this.featureCollector = featureCollector;
    }
}
