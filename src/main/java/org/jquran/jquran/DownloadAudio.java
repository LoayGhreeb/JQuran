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

        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        downloadStage.setWidth(screenWidth * PERCENTAGE);
        downloadStage.setHeight(screenHeight * PERCENTAGE);

        List<Chapter> quranChapters = Query.loadChapters();
        ListView<Chapter> chaptersListView = new ListView<>();
        chaptersListView.getItems().addAll(quranChapters);
        chaptersListView.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        List<Reciter> reciters = Query.loadReciters();
        ListView<Reciter> reciterListView = new ListView<>();
        reciterListView.getItems().addAll(reciters);
        reciterListView.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);


        Button audioDownloadButton = new Button("تحميل");

        audioDownloadButton.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.SUCCESS);
        audioDownloadButton.setMnemonicParsing(true);

        Button cancel = new Button("الغاء");
        cancel.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.DANGER);
        cancel.setMnemonicParsing(true);

        HBox hBox = new HBox(30);
        hBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(10));
        hBox.getChildren().addAll(audioDownloadButton, cancel);

        audioDownloadButton.setOnAction(e -> {
            //get reciter id and surah id
            int recitationId = reciterListView.getSelectionModel().getSelectedItem().getId();
            int surahId = chaptersListView.getSelectionModel().getSelectedItem().getId();

            // get the details of the audio file that will be downloaded
            String audioFileDetails = "https://api.qurancdn.com/api/qdc/audio/reciters/" + recitationId + "/audio_files?chapter=" + surahId + "&segments=true";
            String outputFilePath = "src/main/resources/org/jquran/jquran/Quran_Audio/" + reciterListView.getSelectionModel().getSelectedItem().getId() + "/" + surahId + ".json";
            try {
                Path path = Paths.get(outputFilePath);
                if (!Files.exists(path.getParent()))
                    Files.createDirectories(path.getParent());

                URL url = URI.create(audioFileDetails).toURL();

                try (InputStream in = url.openStream()) {
                    Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("File downloaded successfully to: " + path);

                    // Parse the downloaded JSON to extract audio_url
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode rootNode = objectMapper.readTree(path.toFile());

                    JsonNode audioFilesNode = rootNode.path("audio_files");
                    JsonNode firstAudioFileNode = audioFilesNode.get(0);
                    String audioUrl = firstAudioFileNode.path("audio_url").asText();
                    System.out.println("Captured audio_url: " + audioUrl);

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
        VBox vBox = new VBox();
        vBox.getChildren().addAll(reciterListView, chaptersListView, hBox);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(vBox);
        downloadStage.setTitle("اختر السورة والقارئ");
        downloadStage.setScene(new Scene(borderPane));
        downloadStage.show();
    }
}