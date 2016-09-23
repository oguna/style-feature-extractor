package ffe.gui;

import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;

import java.util.Arrays;
import java.util.List;

public class FontDialog extends Dialog<Font> {
    private static final List<Integer> defaultFontSizes = Arrays.asList(8,9,10,11,12,14,16,18,20,22,24,26,28,36,48,72);
    private final GridPane grid;
    private final Label fontFamilyLabel;
    private final ComboBox<String> fontFamilyComboBox;
    private final Label fontSizeLabel;
    private final ComboBox<Integer> fontSizeComboBox;

    private final Font defaultValue;

    public FontDialog() {
        this(Font.getDefault());
    }

    public FontDialog(@NamedArg("defaultValue") Font defaultValue) {
        this.defaultValue = defaultValue;

        final DialogPane dialogPane = getDialogPane();

        this.fontFamilyComboBox = new ComboBox<>();
        this.fontFamilyComboBox.getItems().addAll(Font.getFamilies());
        GridPane.setHgrow(fontFamilyComboBox, Priority.ALWAYS);
        GridPane.setFillWidth(fontFamilyComboBox, true);

        this.fontSizeComboBox = new ComboBox<>();
        this.fontSizeComboBox.getItems().addAll(defaultFontSizes);
        GridPane.setHgrow(fontSizeComboBox, Priority.ALWAYS);
        GridPane.setFillWidth(fontSizeComboBox, true);

        this.fontFamilyLabel = new Label("Family");
        fontFamilyLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);

        this.fontSizeLabel = new Label("Size");
        fontSizeLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);

        this.grid = new GridPane();
        this.grid.setHgap(10);
        this.grid.setVgap(10);
        this.grid.setMaxWidth(Double.MAX_VALUE);
        this.grid.setAlignment(Pos.CENTER_LEFT);

        dialogPane.contentTextProperty().addListener(o -> updateGrid());
        updateGrid();

        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter((dialogButton) -> {
            ButtonBar.ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
            return data == ButtonBar.ButtonData.OK_DONE && !fontFamilyComboBox.getSelectionModel().isEmpty() && !fontSizeComboBox.getSelectionModel().isEmpty() ?
                    Font.font(fontFamilyComboBox.getSelectionModel().getSelectedItem(), fontSizeComboBox.getSelectionModel().getSelectedItem()) : null;
        });
    }

    public final Font getDefaultValue() {
        return defaultValue;
    }

    private void updateGrid() {
        grid.getChildren().clear();

        grid.add(fontFamilyLabel, 0, 0);
        grid.add(fontFamilyComboBox, 1, 0);
        grid.add(fontSizeLabel, 0, 1);
        grid.add(fontSizeComboBox, 1, 1);
        getDialogPane().setContent(grid);

        Platform.runLater(fontFamilyComboBox::requestFocus);
    }
}
