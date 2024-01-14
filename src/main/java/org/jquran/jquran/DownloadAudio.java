package org.jquran.jquran;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import atlantafx.base.theme.Styles;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
public final class DownloadAudio {

    private static final double PERCENTAGE = 0.6;

    public static void display() {
        Stage downloadStage = new Stage();
        // Set the stage to be 60% of the screen width and height
        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        downloadStage.setWidth(screenWidth * PERCENTAGE);
        downloadStage.setHeight(screenHeight * PERCENTAGE);

        // load the chapters and reciters
        List<Chapter> quranChapters = Query.loadChapters();
        ListView<Chapter> chaptersListView = new ListView<>();
        chaptersListView.getItems().addAll(quranChapters);
        chaptersListView.getStyleClass().add("listView");
        chaptersListView.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        List<Reciter> reciters = Query.loadReciters();
        ListView<Reciter> reciterListView = new ListView<>();
        reciterListView.getItems().addAll(reciters);
        reciterListView.getStyleClass().add("listView");
        reciterListView.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        // download button
        Button audioDownloadButton = new Button("تحميل");
        audioDownloadButton.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.SUCCESS);
        audioDownloadButton.setMnemonicParsing(true);

        // cancel button
        Button cancel = new Button("الغاء");
        cancel.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.DANGER);
        cancel.setMnemonicParsing(true);
        cancel.setOnAction(e -> downloadStage.close());

        // hbox for the buttons
        HBox hBox = new HBox(30);
        hBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(10));
        hBox.getChildren().addAll(audioDownloadButton, cancel);

        // download the audio file
        audioDownloadButton.setOnAction(e -> {
            // if no reciter or chapter is selected, show an error message
            if(reciterListView.getSelectionModel().getSelectedItem() == null || chaptersListView.getSelectionModel().getSelectedItem() == null) {
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

            // get JSON file from the API (audio file details)
            String audioFileDetails = "https://api.qurancdn.com/api/qdc/audio/reciters/" + recitationId + "/audio_files?chapter=" + surahId + "&segments=true";
            String outputFilePath = "src/main/resources/org/jquran/jquran/Quran_Audio/" + reciterListView.getSelectionModel().getSelectedItem().getId() + "/" + surahId + ".json";
            try {
                // check if the directory exists, if not create it
                Path path = Paths.get(outputFilePath);
                if (!Files.exists(path.getParent()))
                    Files.createDirectories(path.getParent());

                URL url = URI.create(audioFileDetails).toURL();

                // download the JSON file
                try (InputStream in = url.openStream()) {
                    Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("File downloaded successfully to: " + path);

                    // Parse the downloaded JSON to extract audio_url
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode rootNode = objectMapper.readTree(path.toFile());

                    JsonNode audioFilesNode = rootNode.path("audio_files");
                    JsonNode firstAudioFileNode = audioFilesNode.get(0);
                    String audioUrl = firstAudioFileNode.path("audio_url").asText();

                    // Download the audio file
                    String outputAudioFilePath = "src/main/resources/org/jquran/jquran/Quran_Audio/" + reciterListView.getSelectionModel().getSelectedItem().getId() + "/" + surahId + ".mp3";
                    Path audioPath = Paths.get(outputAudioFilePath);

                    URL audioUrlObject = URI.create(audioUrl).toURL();
                    try (InputStream in2 = audioUrlObject.openStream()) {
                        Files.copy(in2, audioPath, StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("File downloaded successfully to: " + audioPath);
                    }
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
        // vbox for the listviews and hbox
        VBox vBox = new VBox();
        vBox.getChildren().addAll(reciterListView, chaptersListView, hBox);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(vBox);
        downloadStage.setTitle("اختر السورة والقارئ");
        Scene scene = new Scene(borderPane);
        scene.getStylesheets().add(DownloadAudio.class.getResource("styles/styles.css").toExternalForm());
        downloadStage.setScene(scene);
        downloadStage.show();
    }
}