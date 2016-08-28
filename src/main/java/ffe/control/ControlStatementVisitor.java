package ffe.control;

import ffe.FormatFeatureVisitor;
import ffe.FeatureCollector;
import ffe.TokenSequence;
import org.eclipse.jdt.core.dom.ASTNode;

public abstract class ControlStatementVisitor extends FormatFeatureVisitor {
    public ControlStatementVisitor(TokenSequence tokenSequence, FeatureCollector featureCollector) {
        super(tokenSequence, featureCollector);
    }

    protected void collectFeature(String feature, boolean value, ASTNode node) {

    }
}
