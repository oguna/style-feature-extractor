package ffe.output;

import ffe.whitespace.WhiteSpaceFormatFeature;

import java.io.IOException;

public interface IFeatureWriter {
    void write(WhiteSpaceFormatFeature feature, String name, String content) throws IOException;
}
