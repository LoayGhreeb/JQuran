package org.jquran.jquran;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    public void start(Stage primaryStage) throws Exception {
        AnchorPane root = new AnchorPane();
        root.setStyle("-fx-background-color: #434343;");
        Label titleLabel = new Label("JQuran");
        titleLabel.setStyle("-fx-text-fill: #999999;");
        titleLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        titleLabel.setFont(new Font(80.0));
        AnchorPane.setBottomAnchor(titleLabel, 235.8);
        AnchorPane.setLeftAnchor(titleLabel, 178.0);
        AnchorPane.setRightAnchor(titleLabel, 169.8);
        AnchorPane.setTopAnchor(titleLabel, 75.0);

        Label loadingLabel = new Label("جارى التحميل...");
        loadingLabel.setStyle("-fx-text-fill: #999999;");
        loadingLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        loadingLabel.setFont(Font.font("kfgqpc_hafs_uthmanic_script Regular", 23.0));
        AnchorPane.setBottomAnchor(loadingLabel, 82.8);
        AnchorPane.setLeftAnchor(loadingLabel, 220.0);
        AnchorPane.setRightAnchor(loadingLabel, 194.8);
        AnchorPane.setTopAnchor(loadingLabel, 242.0);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setLayoutX(190.0);
        progressBar.setLayoutY(327.0);
        progressBar.setPrefHeight(11.0);
        progressBar.setPrefWidth(210.0);

        root.getChildren().addAll(titleLabel, loadingLabel, progressBar);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("JQuran");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}