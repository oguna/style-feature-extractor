package ffe;

import ffe.whitespace.WhiteSpaceFormatFeature;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;

public class FeatureWriter implements Closeable {
    private final Writer writer;
    public FeatureWriter(Writer writer) {
        if (writer == null) {
            throw new IllegalArgumentException();
        }
        this.writer = writer;
    }

    public void write(WhiteSpaceFormatFeature feature) throws IOException {
        writer.write(format(feature));
        writer.write("\n");
    }

    public String format(WhiteSpaceFormatFeature feature) {
        return feature.token.originalStart + ", " + feature.token.originalEnd + ", " + feature.format + ", " + feature.value;
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
