package ffe.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MainGUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private MainWindowController controller;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainWindow.fxml"));
        Parent root = loader.load();
        this.controller = loader.getController();
        primaryStage.setTitle("Format Feature Extractor");
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/java-keywords.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (this.controller != null) {
            controller.stop();
        }
    }
}
