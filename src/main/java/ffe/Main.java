package ffe;

import ffe.output.CsvWriter;
import ffe.output.IFeatureWriter;
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
        options.addOption("f", "format", true, "output style (text/txt/csv/html)");
        File outputFile;
        List<String> targets;
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("o")) {
                outputFile = new File(line.getOptionValue("o"));
            } else {
                outputFile = null;
            }
            targets = line.getArgList();
        } catch (ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
            return;
        }
        Writer writer = outputFile != null ? new FileWriter(outputFile) : new OutputStreamWriter(System.out);
        IFeatureWriter featureWriter = new CsvWriter(writer);
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
        if (writer.getClass() == FileWriter.class) {
            writer.close();
        }
        System.out.println("end.");
    }
}
