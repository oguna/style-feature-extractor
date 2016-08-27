package ffe.gui;

import ffe.whitespace.WhiteSpaceFormatFeature;
import javafx.application.Platform;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainWindowController implements Initializable {

    private final SourceCodeManager manager = new SourceCodeManager();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

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
    private SourceCodeView textArea;

    @FXML
    private TableView<WhiteSpaceFormatFeature> tableView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        attributeColumn.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().format.substring(31)));
        positionColumn.setCellValueFactory(e -> new SimpleStringProperty(manager.getPositionDescription(e.getValue().token.position)));
        tokenColumn.setCellValueFactory(e -> new SimpleStringProperty(manager.getToken(e.getValue().token)));
        valueColumn.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().value.toString()));
        tableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                int offset = manager.calculateCodeAreaPosition(newValue.token.position);
                textArea.getContent().selectRange(offset, offset + newValue.token.length);
            }
        });
        tableView.setItems(manager.features);
        manager.text.addListener((observable, oldValue, newValue) -> {
            textArea.getContent().clear();
            textArea.getContent().replaceText(0, 0, newValue);
        });
        textArea.getContent().replaceText(0, 0, manager.text.getValue());
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
                manager.loadContent(selectedFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            lastSelectedDirectory = selectedFile.getParentFile();
        }
    }

    @FXML
    public void save(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        if (lastSelectedDirectory != null) {
            fileChooser.setInitialDirectory(lastSelectedDirectory);
        }
        fileChooser.setTitle("Save CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", "*.csv"));
        File selectedFile = fileChooser.showSaveDialog(root.getScene().getWindow());
        if (selectedFile != null) {
            manager.saveFeatures(selectedFile);
            lastSelectedDirectory = selectedFile.getParentFile();
        }
    }

    @FXML
    public void exit(ActionEvent actionEvent) {
        Platform.exit();
    }
}
