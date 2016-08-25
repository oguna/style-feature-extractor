package ffe.whitespace.array;

import ffe.whitespace.FeatureCollector;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class ArrayDeclarationVisitor extends WhiteSpaceVisitor {

    public ArrayDeclarationVisitor(char[] source, FeatureCollector featureCollector) {
        super(source, featureCollector);
    }

    @Override
    public boolean visit(VariableDeclarationFragment node) {
        return super.visit(node);
    }

}
