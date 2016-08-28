package ffe;

import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("hello world");
        List<Path> targets = Files.walk(Paths.get("src/main/java"))
                .filter(e -> e.toFile().isFile())
                .filter(e -> e.toFile().getPath().endsWith(".java"))
                .collect(Collectors.toList());
        new File("out").mkdir();
        for (Path target : targets) {
            ASTParser parser = ASTParser.newParser(AST.JLS8);
            String content = new String(Files.readAllBytes(target));
            char[] source = content.toCharArray();
            parser.setSource(source);
            CompilationUnit unit = (CompilationUnit) parser.createAST(new NullProgressMonitor());
            TokenSequence sequence = new TokenSequence(source);
            FeatureCollector collector = new FeatureCollector();
            List<WhiteSpaceVisitor> visitors = WhiteSpaceVisitor.listWhiteSpaceVisitor(sequence, collector);
            visitors.forEach(unit::accept);
            collector.dump(new File("out/" + target.toFile().getName() + ".csv"));
        }
    }
}
