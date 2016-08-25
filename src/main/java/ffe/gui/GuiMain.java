package ffe.gui;

import ffe.whitespace.FeatureCollector;
import ffe.whitespace.WhiteSpaceFormatFeature;
import ffe.whitespace.WhiteSpaceVisitor;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GuiMain extends Application {

    private static String sample = "public class HelloWorld {\n" +
            "    public static void main(String[] args){\n" +
            "        System.out.println(\"Hello, world.\");\n" +
            "    }\n" +
            "}";

    public static void main(String[] args) {
        launch(args);
    }

    private CodeArea codeArea;
    private TableView<WhiteSpaceFormatFeature> tableView;
    private ExecutorService executor;
    private SourceCodeManager manager;
    private Stage stage;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        executor = Executors.newSingleThreadExecutor();
        codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .successionEnds(Duration.ofMillis(500))
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(codeArea.richChanges())
                .filterMap(t -> {
                    if (t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(this::applyHighlighting);

        tableView = new TableView<>();
        tableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                int offset = manager.calculateCodeAreaPosition(newValue.token.position);
                codeArea.selectRange(offset, offset + newValue.token.length);
            }
        });
        TableColumn<WhiteSpaceFormatFeature, String> attributeColumn = new TableColumn<>("Attribute");
        attributeColumn.setSortable(false);
        attributeColumn.setEditable(false);
        TableColumn<WhiteSpaceFormatFeature, String> valueColumn = new TableColumn<>("Value");
        valueColumn.setSortable(false);
        valueColumn.setEditable(false);
        TableColumn<WhiteSpaceFormatFeature, String> startColumn = new TableColumn<>("Position");
        startColumn.setSortable(false);
        startColumn.setEditable(false);
        TableColumn<WhiteSpaceFormatFeature, String> tokenColumn = new TableColumn<>("Token");
        tokenColumn.setSortable(false);
        tokenColumn.setEditable(false);
        attributeColumn.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().format.substring(31)));
        startColumn.setCellValueFactory(e -> new SimpleStringProperty(manager.getPositionDescription(e.getValue().token.position)));
        tokenColumn.setCellValueFactory(e -> new SimpleStringProperty(manager.getToken(e.getValue().token)));
        valueColumn.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().value.toString()));

        tableView.getColumns().addAll(startColumn, tokenColumn, attributeColumn, valueColumn);
        SplitPane splitPane = new SplitPane(new VirtualizedScrollPane<>(codeArea), tableView);
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem openMenuItem = new MenuItem("Open File..");
        openMenuItem.setOnAction(e -> openFile());
        fileMenu.getItems().add(openMenuItem);
        menuBar.getMenus().add(fileMenu);
        BorderPane root = new BorderPane(splitPane, menuBar, null, null, null);
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(GuiMain.class.getResource("/java-keywords.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Java Format Feature Extractor");

        initData("HelloWorld.java", sample);
        primaryStage.show();
    }

    @Override
    public void stop() {
        executor.shutdown();
    }

    private File lastSelectedDirectory = null;
    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        if (lastSelectedDirectory != null) {
            fileChooser.setInitialDirectory(lastSelectedDirectory);
        }
        fileChooser.setTitle("Open Java File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java File", "*.java"));
        File selectedFile = fileChooser.showOpenDialog(stage);
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

    public void initData(String name, String content) {
        this.manager = new SourceCodeManager(content);
        codeArea.clear();
        codeArea.replaceText(0, 0, content);
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
        codeArea.selectRange(0, 0);
        this.stage.setTitle(name + " - Format Feature Extractor");
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = codeArea.getText();
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
        codeArea.setStyleSpans(0, highlighting);
    }
}
