package org.jquran.jquran;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import atlantafx.base.theme.Styles;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;


public final class DownloadAudio {

    private static final double PERCENTAGE = 0.6;

    public static void display() throws IOException {
        Stage downloadStage = new Stage();

        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        downloadStage.setWidth(screenWidth * PERCENTAGE);
        downloadStage.setHeight(screenHeight * PERCENTAGE);


        List<Chapter> quranChapters = Query.loadChapters();
        List<Reciter> reciters = Query.loadReciters();

        ListView<String> surahListView = new ListView<>();
        surahListView.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        ListView<String> reciterListView = new ListView<>();
        reciterListView.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        for (Reciter reciter : reciters) {
            /// reciters name
            String reciterSName = reciter.getTranslated_name().getName();
            /// reciters style
            String reciterStyle = reciter.getStyle();
            if (reciterStyle == null)
                reciterListView.getItems().add(reciterSName);
            else reciterListView.getItems().add(reciterSName + ' ' + reciterStyle);
        }

        for (Chapter quranChapter : quranChapters) {
            surahListView.getItems().add(quranChapter.getName_arabic());
        }

        Button audioDownloadButton = new Button("تحميل");

        audioDownloadButton.getStyleClass().addAll(
                Styles.BUTTON_OUTLINED, Styles.SUCCESS);
        audioDownloadButton.setMnemonicParsing(true);

        Button cancel = new Button("الغاء");
        cancel.getStyleClass().addAll(
                Styles.BUTTON_OUTLINED, Styles.DANGER);
        cancel.setContentDisplay(ContentDisplay.RIGHT);
        cancel.setMnemonicParsing(true);

        HBox hBox = new HBox(30);
        hBox.setPadding(new Insets(10));
        hBox.getChildren().addAll(audioDownloadButton, cancel);
        hBox.setAlignment(Pos.CENTER);

        audioDownloadButton.setOnAction(e -> {
            try {
                ExecutorService pool = Executors.newFixedThreadPool(10);
                int reciterId = reciterListView.getSelectionModel().getSelectedIndex() + 1;
                int surahId = surahListView.getSelectionModel().getSelectedIndex() + 1;
                URL url = URI.create("https://api.quran.com/api/v4/recitations/" + reciterId + "/by_chapter/" + surahId
                        + "?per_page=all").toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {

                    cancel.setOnAction(e2 -> {
                        try {
                            pool.shutdown(); // Disable new tasks from being submitted
                            if (!pool.awaitTermination(1, TimeUnit.SECONDS)) {
                                pool.shutdownNow(); // Cancel currently executing tasks
                                if (!pool.awaitTermination(1, TimeUnit.SECONDS)) {
                                    System.out.println("The pool did not terminate");
                                }
                            }
                        } catch (Exception exception) {
                            System.out.println(exception.getMessage());
                        }
                    });

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(reader.readLine());
                    for (int i = 0; i < jsonNode.get("audio_files").size(); i++) {
                        String path = "src/main/resources/org/jquran/jquran/Quran_Audio/"
                                + reciterListView.getSelectionModel().getSelectedItem() + "/";
                        String downloadUrl = jsonNode.get("audio_files").get(i).get("url").toString().replace("\"", "");
                        path = path + downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
                        downloadUrl = "https://verses.quran.com/" + downloadUrl;
                        System.out.println(path);
                        pool.submit(new Downloader(downloadUrl, path));
                    }
                    reader.close();
                } else {
                    System.out.println(responseCode);
                }
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        });
        VBox vBox = new VBox();
        vBox.getChildren().addAll(reciterListView, surahListView, hBox);
        BorderPane borderPane= new BorderPane();
        borderPane.setCenter(vBox);
        downloadStage.setTitle("اختر السورة والقارئ");
        downloadStage.setScene(new Scene(borderPane));
        downloadStage.show();
    }
}
