package ffe;

import ffe.whitespace.WhiteSpaceFormatFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        List<Path> targets = Files.walk(Paths.get("."))
                .filter(e -> e.toFile().isFile())
                .filter(e -> e.toFile().getPath().endsWith(".java"))
                .collect(Collectors.toList());
        for (Path target : targets) {
            String content = new String(Files.readAllBytes(target));
            List<WhiteSpaceFormatFeature> features = FeatureExtractor.extract(content);
            features.forEach(System.out::println);
        }
        System.out.println("end.");
    }
}
