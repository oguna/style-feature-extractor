package ffe.output;

import ffe.whitespace.WhiteSpaceFormatFeature;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CsvWriter implements IFeatureWriter {
    private final Writer writer;
    private final Set<String> targetFeatures;
    public CsvWriter(Writer writer, String[] targetFeatures) {
        this.writer = writer;
        if (targetFeatures == null || targetFeatures.length == 0) {
            this.targetFeatures = null;
        } else {
            this.targetFeatures = new HashSet<>();
            Arrays.stream(targetFeatures).forEach(this.targetFeatures::add);
        }
    }

    @Override
    public void write(WhiteSpaceFormatFeature feature, String name, String content) throws IOException {
        if (this.targetFeatures == null || targetFeatures.contains(feature.format)) {
            writer.write(format(feature, name));
            writer.write("\n");
        }
    }

    private static String format(WhiteSpaceFormatFeature feature, String name) {
        return name + ", " + feature.token.originalStart + ", " + feature.token.originalEnd + ", " + feature.format + ", " + feature.value;
    }
}
