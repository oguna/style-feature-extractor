package ffe.whitespace.array;

import ffe.FeatureCollector;
import ffe.TokenSequence;
import ffe.whitespace.WhiteSpaceFormatFeature;
import ffe.whitespace.WhiteSpaceOption;
import ffe.whitespace.WhiteSpaceVisitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.core.util.Util;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatter;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatterOptions;
import org.eclipse.text.edits.TextEdit;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;

public class ArrayAllocationVisitorTest {
    private Stream<String> getJavaSourceStream() throws IOException {
        String jdkPath = System.getenv("JAVA_HOME");
        if (jdkPath == null) {
            throw new FileNotFoundException("This test uses `JAVA_HOME/src.zip'. Please set JAVA_HOME to your environment path.");
        }
        Path srcPath = Paths.get(jdkPath, "src.zip");
        Predicate<Path> pathPredicate = (e) -> e.toString().endsWith(".java");
        FileSystem fs = FileSystems.newFileSystem(srcPath, ClassLoader.getSystemClassLoader());
        return StreamSupport.stream(fs.getRootDirectories().spliterator(), false)
                .onClose(() -> {
                    try {
                        fs.close();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }).flatMap(rootPath -> {
                    try {
                        return Files.walk(rootPath);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }).filter(path -> !Files.isDirectory(path))
                .filter(pathPredicate)
                .map(path -> {
                    try {
                        return new String(Files.readAllBytes(path));
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }

    private List<WhiteSpaceFormatFeature> extractFeatures(String content) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        char[] source = content.toCharArray();
        parser.setSource(source);
        CompilationUnit unit = (CompilationUnit) parser.createAST(new NullProgressMonitor());
        TokenSequence sequence = new TokenSequence(source);
        FeatureCollector collector = new FeatureCollector();
        List<WhiteSpaceVisitor> visitors = WhiteSpaceVisitor.listWhiteSpaceVisitor(sequence, collector);
        visitors.forEach(unit::accept);
        return collector.features;
    }

    @Test
    public void test() throws IOException {
        DefaultCodeFormatterOptions options = DefaultCodeFormatterOptions.getEclipseDefaultSettings();
        options.insert_space_before_opening_bracket_in_array_allocation_expression = false;
        CodeFormatter formatter = new DefaultCodeFormatter(options);
        getJavaSourceStream()
                .parallel()
                .map(source -> {
                    TextEdit edit = formatter.format(CodeFormatter.K_COMPILATION_UNIT, source, 0, source.length(), 0, "\n");
                    return Util.editedString(source, edit);
                })
                .flatMap(e -> extractFeatures(e).stream())
                .filter(e -> e.format.equals("FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACKET_IN_ARRAY_ALLOCATION_EXPRESSION"))
                .forEach(e -> assertEquals(WhiteSpaceOption.DO_NOT_INSERT, e.value));
    }
}