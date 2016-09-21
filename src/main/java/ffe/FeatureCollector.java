package ffe;

import ffe.whitespace.WhiteSpaceFormatFeature;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FeatureCollector {
    private static final int formatterPrefixSize = "org.eclipse.jdt.core.formatter.".length();
    public final List<WhiteSpaceFormatFeature> features = new ArrayList<>();

    public void collect(WhiteSpaceFormatFeature feature) {
        features.add(feature);
    }

    public void dump(File file) throws IOException {
        try (FileWriter write = new FileWriter(file);
             BufferedWriter bw = new BufferedWriter(write)) {
            for (WhiteSpaceFormatFeature i : features) {
                bw.write(formatLine(i));
                bw.write('\n');
            }
        }
    }

    private static String formatLine(WhiteSpaceFormatFeature feature) {
        return feature.token.originalStart +
                ',' +
                feature.token.originalEnd +
                ',' +
                feature.format.substring(formatterPrefixSize) +
                ',' +
                feature.value;
    }
}
