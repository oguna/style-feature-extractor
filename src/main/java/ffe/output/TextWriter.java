package ffe.output;

import ffe.whitespace.WhiteSpaceFormatFeature;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class TextWriter implements IFeatureWriter {
    private final Writer writer;
    private final Set<String> targetFeatures;
    public TextWriter(Writer writer, String[] targetFeatures) {
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
        if (targetFeatures == null || targetFeatures.contains(feature.format)) {
            int lineNumber = getLineNumber(content, feature.token.originalStart);
            String line = getLine(content, feature.token.originalStart);
            writer.write(name + " : " + lineNumber);
            writer.write("\n");
            writer.write(feature.format + " = " + feature.value);
            writer.write("\n");
            writer.write(line);
            writer.write("\n");
            int columnNumber = getColumnNumber(content, feature.token.originalStart);
            for (int i = 0; i < columnNumber; i++) {
                writer.write(' ');
            }
            int tokenLength = feature.token.originalEnd - feature.token.originalStart + 1;
            for (int i = 0; i < tokenLength; i++) {
                writer.write('^');
            }
            writer.write("\n");
        }
    }

    private static int getLineNumber(String content, int index) {
        int lineNumber = 1;
        for (int i = 0; i < index; i++) {
            if (content.charAt(i) == '\n') {
                lineNumber++;
            }
        }
        return lineNumber;
    }

    private static int getColumnNumber(String content, int index) {
        int start = index;
        while (start > 0 && content.charAt(start - 1) != '\n' && content.charAt(start) - 1 != '\r') {
            start--;
        }
        return index - start;
    }

    private static String getLine(String content, int index) {
        int start = index;
        while (start > 0 && content.charAt(start-1) != '\n' && content.charAt(start-1) != '\r') {
            start--;
        }
        int end = index;
        while (end < content.length() && content.charAt(end) != '\n' && content.charAt(end) != '\r') {
            end++;
        }
        return content.substring(start, end);
    }
}
