package ffe;

import ffe.output.CsvWriter;
import ffe.output.IFeatureWriter;
import ffe.output.TextWriter;
import ffe.whitespace.WhiteSpaceFormatFeature;
import org.apache.commons.cli.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        Options options = new Options();
        options.addOption("o", "output", true, "output filename");
        options.addOption("s", "style", true, "output style (text/csv; csv is by default)");
        options.addOption("f", "format", true, "formats to output (delimiter is comma; all formats by default)");
        final File outputFile;
        final List<String> targets;
        final String[] targetFeatures;
        final String style;
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("o")) {
                outputFile = new File(line.getOptionValue("o"));
            } else {
                outputFile = null;
            }
            targets = line.getArgList();
            if (line.hasOption("f")) {
                targetFeatures = line.getOptionValue("f").split(",");
            } else {
                targetFeatures = null;
            }
            if (line.hasOption("s")) {
                style = line.getOptionValue("s");
                if (!style.equals("csv") && !style.equals("text")) {
                    throw new ParseException("argument of `s' needs csv or text");
                }
            } else {
                style = "csv";
            }
        } catch (ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
            return;
        }
        Writer writer = outputFile != null ? new FileWriter(outputFile) : new OutputStreamWriter(System.out);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        IFeatureWriter featureWriter = style.equals("csv") ? new CsvWriter(bufferedWriter, targetFeatures) : new TextWriter(bufferedWriter, targetFeatures);
        targets.stream()
                .map(e -> Paths.get(e))
                .flatMap(e -> {
                    try {
                        return Files.walk(e);
                    } catch (IOException exp) {
                        throw new UncheckedIOException(exp);
                    }
                })
                .filter(e -> !Files.isDirectory(e))
                .filter(e -> e.toString().endsWith(".java"))
                .forEach(e -> {
                    try {
                        String content = new String(Files.readAllBytes(e));
                        List<WhiteSpaceFormatFeature> features = FeatureExtractor.extract(content);
                        for (WhiteSpaceFormatFeature feature : features) {
                            featureWriter.write(feature, e.toString(), content);
                        }
                    } catch (IOException exp) {
                        throw new UncheckedIOException(exp);
                    }
                });
        bufferedWriter.flush();
        if (writer.getClass() == FileWriter.class) {
            writer.close();
        }
        System.out.println("end.");
    }
}
