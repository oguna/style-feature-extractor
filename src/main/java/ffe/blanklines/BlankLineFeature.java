package ffe.blanklines;

import ffe.FormatFeature;

public class BlankLineFeature extends FormatFeature {
    public final String attribute;
    public final int value;

    public BlankLineFeature(String attribute, int value) {
        this.attribute = attribute;
        this.value = value;
    }
}
