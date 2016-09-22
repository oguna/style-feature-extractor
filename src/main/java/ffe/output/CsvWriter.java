package ffe.output;

import ffe.whitespace.WhiteSpaceFormatFeature;

import java.io.IOException;
import java.io.Writer;

public class CsvWriter implements IFeatureWriter {
    private final static int prefixSize = "org.eclipse.jdt.core.formatter.".length();
    private final Writer writer;
    public CsvWriter(Writer writer) {
        this.writer = writer;
    }

    @Override
    public void write(WhiteSpaceFormatFeature feature, String name, String content) throws IOException {
        writer.write(format(feature, name));
        writer.write("\n");
    }

    private static String format(WhiteSpaceFormatFeature feature, String name) {
        return name + ", " + feature.token.originalStart + ", " + feature.token.originalEnd + ", " + feature.format.substring(prefixSize) + ", " + feature.value;
    }
}
