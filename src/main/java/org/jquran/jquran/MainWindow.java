package org.jquran.jquran;

import atlantafx.base.controls.Notification;
import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MainWindow extends Application {
    private static final double PERCENTAGE = 0.9;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
        // Set the stage size
        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();

        primaryStage.setWidth(screenWidth * PERCENTAGE);
        primaryStage.setHeight(screenHeight * PERCENTAGE);
        // The root Pane
        BorderPane root = new BorderPane();
        root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        // Menubar
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(new Menu("ملف"), new Menu("عرض"), new Menu("بحث"));
        root.setTop(menuBar);

        /*
         * create Accordion (appendix) contains the chaptersPane &
         * chaptersPane used to combine search field with listview of chapters name into
         * one component
         */
        VBox chaptersPane = new VBox();
        chaptersPane.setSpacing(10);
        Accordion appendix = new Accordion(new TitledPane("الفهرس", chaptersPane));
        appendix.setExpandedPane(appendix.getPanes().getFirst());
        StackPane accordionContainer = new StackPane(appendix);
        accordionContainer.setPadding(new Insets(10));
        root.setLeft(accordionContainer);

        // text field to search for chapter name or chapter number & add it to
        // chapterPane
        TextField searchField = new TextField();
        searchField.setPromptText("اسم السورة");
        chaptersPane.getChildren().add(searchField);

        // chaptersList to list all chapters
        ListView<Chapter> chaptersList = new ListView<>();
        chaptersList.prefHeightProperty().bind(chaptersPane.heightProperty());

        // get & list all chapters
        List<Chapter> chapters = Query.loadChapters();
        chaptersList.getItems().addAll(chapters);
        chaptersPane.getChildren().add(chaptersList);

        // audio player
        HBox hB = new HBox();
        var reciterComboBox = new ComboBox<String>();
        List<Reciter> reciters = Query.loadReciters();
        for (Reciter reciter : reciters) {
            /// reciters name
            String reciterSName = reciter.getReciter_name();
            /// reciters style
            String reciterStyle = reciter.getStyle();
            if (reciterStyle == null)
                reciterComboBox.getItems().add(reciterSName);
            else
                reciterComboBox.getItems().add(reciterSName + ' ' + reciterStyle);
        }
        reciterComboBox.getSelectionModel().selectFirst();
        var surahComboBox = new ComboBox<String>();
        for (Chapter chapter : chapters) {
            /// Surah name
            String surahName = chapter.getName_arabic();
            surahComboBox.getItems().add(surahName);
        }
        surahComboBox.getSelectionModel().selectFirst();

        Button start = new Button("تشغيل");
        start.setOnAction(e -> {
            File f = new File("src/main/resources/org/jquran/jquran/Quran_Audio/"
                    + reciterComboBox.getSelectionModel().getSelectedItem() + "/");
            File l[] = f.listFiles();
            List<File> lf = new ArrayList<File>();
            if (l == null) {
                var alert = new Alert(AlertType.ERROR);
                alert.setTitle("Exception Dialog");
                alert.setHeaderText("No files found");
                alert.setContentText("you have to download the audio files first");

                alert.initOwner(root.getScene().getWindow());
                alert.showAndWait();

                return;
            }
            for (File x : l)
                lf.add(x);
            int i = 0;
            ArrayList players = new ArrayList<MediaPlayer>();
            Collections.sort(lf);
            while (i < l.length) {
                MediaPlayer mp = new MediaPlayer(new Media(lf.get(i).toURI().toString()));
                players.add(mp);
                i++;
            }
            MediaControl mediaControl = new MediaControl(players);
            if (hB.getChildren().size() < 5)
                hB.getChildren().add(mediaControl);

            else {
                hB.getChildren().remove(4);
                hB.getChildren().add(mediaControl);
            }
            primaryStage.setWidth(screenWidth * PERCENTAGE);
        });

        // text filed listener to handel search queries
        searchField.textProperty().addListener((observable, oldText, newText) ->

        {
            chaptersList
                    .getItems().setAll(
                            chapters.stream()
                                    .filter(chapter -> chapter.getName_arabic().contains(newText)
                                            || String.valueOf(chapter.getId()).contains(newText))
                                    .collect(Collectors.toList()));
        });

        VBox temp = new VBox(
                10);
        temp.setPrefHeight(100);
        temp.setPrefWidth(100);
        temp.getChildren().add(new Button("Ok"));
        root.setRight(temp);
        Button btn = new Button("القارئ");
        btn.setOnAction(e -> {
            try {
                DownloadAudio.display();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        hB.getChildren().addAll(start, reciterComboBox, surahComboBox, btn);
        hB.setAlignment(Pos.CENTER);
        root.setBottom(hB);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles/styles.css").toExternalForm());
        primaryStage.setTitle("JQuran");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}