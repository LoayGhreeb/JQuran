
package org.jquran.jquran;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import atlantafx.base.theme.Styles;
import javafx.css.Style;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DownloadAudio {

    public static void display() throws IOException {
        Stage downloadStage = new Stage();

        QuranChapters quranChapters = Query.getChapters();
        QuranReciters reciters = Query.getReciters();

        ListView surahListView = new ListView();
        ListView reciterListView = new ListView();

        surahListView.setStyle("-fx-control-inner-background: #222222");
        surahListView.setMinWidth(200);
        reciterListView.setStyle("-fx-control-inner-background: #222222");
        reciterListView.setMinWidth(200);

        for (int i = 0; i < reciters.getRecitations().size(); i++) {
            /// reciters name
            String reciterSName = reciters.getRecitations().get(i).getReciter_name();
            /// reciters style
            String reciterStyle = reciters.getRecitations().get(i).getStyle();
            if (reciterStyle == null)
                reciterStyle = "";

            reciterListView.getItems().add(reciterSName + " " + reciterStyle);
        }

        for (int i = 0; i < quranChapters.getChapters().size(); i++) {
            surahListView.getItems().add(quranChapters.getChapters().get(i).getName_arabic());
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

        VBox vb = new VBox(audioDownloadButton, cancel);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-color: #222222");

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
                    JsonNode jsonNode = objectMapper.readTree(reader.readLine().toString());
                    for (int i = 0; i < jsonNode.get("audio_files").size(); i++) {
                        String path = "src/main/resources/org/Quran_Audio/"
                                + reciterListView.getSelectionModel().getSelectedItem() + "/";
                        String downloadUrl = jsonNode.get("audio_files").get(i).get("url").toString().replace("\"", "");
                        path = path + downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
                        downloadUrl = "https://verses.quran.com/"
                                + downloadUrl;
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

        var sp = new SplitPane(
                reciterListView,
                surahListView,
                vb);
        sp.setOrientation(Orientation.HORIZONTAL);
        sp.setDividerPositions(0.25, 0.5);
        downloadStage.setTitle("اختر السورة والقارئ");
        downloadStage.setScene(new Scene(sp));
        downloadStage.show();
    }
}
