package org.jquran.jquran;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import atlantafx.base.theme.Styles;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public final class DownloadAudio {

    private static final double PERCENTAGE = 0.6;

    public static void display() {
        // create a new stage
        Stage downloadStage = new Stage();
        downloadStage.setTitle("تحميل الصوت");
        downloadStage.setResizable(false);

        // Set the stage to be 60% of the screen width and height
        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        downloadStage.setWidth(screenWidth * PERCENTAGE);
        downloadStage.setHeight(screenHeight * PERCENTAGE);

        // create a root pane
        VBox root = new VBox();
        root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        // load the chapters and reciters
        ListView<Chapter> chaptersListView = new ListView<>(FXCollections.observableArrayList(Query.loadChapters()));
        chaptersListView.getStyleClass().add("listView");

        ListView<Reciter> reciterListView = new ListView<>(FXCollections.observableArrayList(Query.loadReciters()));
        reciterListView.getStyleClass().add("listView");

        // download button
        Button audioDownloadButton = new Button("تحميل");
        audioDownloadButton.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.SUCCESS);

        // cancelButton button
        Button cancelButton = new Button("الغاء");
        cancelButton.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.DANGER);
        cancelButton.setOnAction(e -> downloadStage.close());

        // hbox for the buttons
        HBox buttons = new HBox(30, audioDownloadButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10));

        // download the audio file
        audioDownloadButton.setOnAction(e -> {
            // if no reciter or chapter is selected, show an error message
            if (reciterListView.getSelectionModel().getSelectedItem() == null || chaptersListView.getSelectionModel().getSelectedItem() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                alert.setTitle("خطأ");
                alert.setHeaderText("لم يتم اختيار السورة او القارئ");
                alert.setContentText("الرجاء اختيار السورة والقارئ");
                alert.showAndWait();
                return;
            }
            // get the reciter id and chapter id
            int recitationId = reciterListView.getSelectionModel().getSelectedItem().getId();
            int surahId = chaptersListView.getSelectionModel().getSelectedItem().getId();

            // get the audio file details
            String audioFileDetails = "https://api.qurancdn.com/api/qdc/audio/reciters/" + recitationId + "/audio_files?chapter=" + surahId + "&segments=true";
            String outputFilePath = "src/main/resources/org/jquran/jquran/Quran_Audio/" + recitationId + "/" + surahId + ".json";
            try {
                // check if the directory exists, if not create it
                Path path = Paths.get(outputFilePath);
                if (!Files.exists(path.getParent()))
                    Files.createDirectories(path.getParent());

                // download the JSON file
                Files.copy(URI.create(audioFileDetails).toURL().openStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("JSON file downloaded successfully to: " + path);

                // Parse the downloaded JSON to extract audio_url
                ObjectMapper objectMapper = new ObjectMapper();
                String audioUrl = objectMapper.readTree(path.toFile()).path("audio_files").get(0).path("audio_url").asText();

                // Create a new thread to download the audio file
                Path audioPath = Paths.get("src/main/resources/org/jquran/jquran/Quran_Audio/" + recitationId + "/" + surahId + ".mp3");
                new Thread(() -> downloadAudioFile(audioUrl, audioPath)).start();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
        // add the list views and buttons to the root pane
        root.getChildren().addAll(reciterListView, chaptersListView, buttons);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(DownloadAudio.class.getResource("styles/styles.css").toExternalForm());
        downloadStage.setScene(scene);
        downloadStage.show();
    }

    private static void downloadAudioFile(String audioUrl, Path audioPath) {
        try {
            URL url = URI.create(audioUrl).toURL();
            try (InputStream in = url.openStream()) {
                Files.copy(in, audioPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Audio file downloaded successfully to: " + audioPath);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}