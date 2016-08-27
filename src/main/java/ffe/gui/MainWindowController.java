package ffe.gui;

import ffe.whitespace.FeatureCollector;
import ffe.whitespace.WhiteSpaceFormatFeature;
import ffe.whitespace.WhiteSpaceVisitor;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.fxmisc.richtext.model.StyleSpans;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainWindowController implements Initializable {
    private final static String sample = "public class HelloWorld {\n" +
            "    public static void main(String[] args){\n" +
            "        System.out.println(\"Hello, world.\");\n" +
            "    }\n" +
            "}";
    private SourceCodeManager manager;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @FXML
    private TableColumn<WhiteSpaceFormatFeature, String> positionColumn;
    @FXML
    private TableColumn<WhiteSpaceFormatFeature, String> tokenColumn;
    @FXML
    private TableColumn<WhiteSpaceFormatFeature, String> attributeColumn;
    @FXML
    private TableColumn<WhiteSpaceFormatFeature, String> valueColumn;

    @FXML
    private BorderPane root;

    @FXML
    private ScrollTextArea textArea;

    @FXML
    private TableView<WhiteSpaceFormatFeature> tableView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        attributeColumn.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().format.substring(31)));
        positionColumn.setCellValueFactory(e -> new SimpleStringProperty(manager.getPositionDescription(e.getValue().token.position)));
        tokenColumn.setCellValueFactory(e -> new SimpleStringProperty(manager.getToken(e.getValue().token)));
        valueColumn.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().value.toString()));
        textArea.getContent().richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .successionEnds(Duration.ofMillis(500))
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(textArea.getContent().richChanges())
                .filterMap(t -> {
                    if (t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(this::applyHighlighting);

        tableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                int offset = manager.calculateCodeAreaPosition(newValue.token.position);
                textArea.getContent().selectRange(offset, offset + newValue.token.length);
            }
        });
        initData("HelloWorld.java", sample);
    }

    private File lastSelectedDirectory = null;

    @FXML
    public void open(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        if (lastSelectedDirectory != null) {
            fileChooser.setInitialDirectory(lastSelectedDirectory);
        }
        fileChooser.setTitle("Open Java File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java File", "*.java"));
        File selectedFile = fileChooser.showOpenDialog(root.getScene().getWindow());
        if (selectedFile != null) {
            try {
                String content = new String(Files.readAllBytes(selectedFile.toPath()));
                initData(selectedFile.getName(), content);
            } catch (Exception e) {
                e.printStackTrace();
            }
            lastSelectedDirectory = selectedFile.getParentFile();
        }

    }
    @FXML
    public void save(ActionEvent actionEvent) {

    }

    @FXML
    public void exit(ActionEvent actionEvent) {
        Platform.exit();
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = textArea.getContent().getText();
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                return JavaSyntaxHighlighter.computeHighlighting(text);
            }
        };
        executor.execute(task);
        return task;
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        textArea.getContent().setStyleSpans(0, highlighting);
    }

    public void initData(String name, String content) {
        this.manager = new SourceCodeManager(content);
        textArea.getContent().clear();
        textArea.getContent().replaceText(0, 0, content);
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        char[] source = content.toCharArray();
        parser.setSource(source);
        CompilationUnit unit = (CompilationUnit) parser.createAST(new NullProgressMonitor());
        FeatureCollector collector = new FeatureCollector();
        List<WhiteSpaceVisitor> statementVisitors = WhiteSpaceVisitor.listStatementWhiteSpaceVisitor(source, collector);
        statementVisitors.forEach(unit::accept);
        List<WhiteSpaceVisitor> expressionVisitors = WhiteSpaceVisitor.listExpressionWhiteSpaceVisitor(source, collector);
        expressionVisitors.forEach(unit::accept);
        List<WhiteSpaceVisitor> arrayVisitors = WhiteSpaceVisitor.listArrayWhiteSpaceVisitor(source, collector);
        arrayVisitors.forEach(unit::accept);
        List<WhiteSpaceVisitor> declarationVisitors = WhiteSpaceVisitor.listDeclarationWhiteSpaceVisitor(source, collector);
        declarationVisitors.forEach(unit::accept);
        List<WhiteSpaceVisitor> parameterizedVisitors  = WhiteSpaceVisitor.listParameterizedWhiteSpaceVisitor(source, collector);
        parameterizedVisitors.forEach(unit::accept);
        tableView.getItems().clear();
        List<WhiteSpaceFormatFeature> features = new ArrayList<>(collector.features);
        features.sort((a,b) -> a.token.position - b.token.position);
        tableView.getItems().addAll(features);
        textArea.getContent().selectRange(0, 0);
    }

    public void stop() {
        if (executor != null) {
            executor.shutdown();
        }
    }
}
