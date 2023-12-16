import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


public class Main extends Application {
    ObjectMapper objectMapper = new ObjectMapper();
    Font customFont;
    AtomicInteger current = new AtomicInteger(0);

    @Override
    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("sample.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 460, 950);

        BorderPane pane = new BorderPane();
//        pane.setPrefWidth(550);
//        pane.setPrefHeight(1000);

//        AtomicReference<File> jsonFile = new AtomicReference<>(new File("assets/quran/" + 3 + ".json"));
//        AtomicReference<Page> page = new AtomicReference<>(objectMapper.readValue(jsonFile.get(), Page.class));

//        Font customFont = Font.loadFont(new FileInputStream(new File("assets/fonts/QCFV1/QCF_P003.ttf")), 33);
        Text text = new Text();
        text.setFont(customFont);
        text.setFill(Color.WHITE);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setTextAlignment(TextAlignment.CENTER);
        textFlow.setStyle("-fx-background-color: #222222");
//        textFlow.setPrefWidth(460);
        pane.setCenter(textFlow);
        Button btn1 = new Button("Next");
        btn1.setOnAction(e-> {
            try {
                current.addAndGet(1);
                File jsonFile = new File("assets/quran/" + current + ".json");
                Page page = objectMapper.readValue(jsonFile, Page.class);
                customFont = Font.loadFont(new FileInputStream(new File("assets/fonts/QCFV1/QCF_P00" + current +".ttf")), 33);
                text.setFont(customFont);
                text.setText(page.getVersesAsString().toString());
                textFlow.getChildren().clear();
                textFlow.getChildren().add(text);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        Button btn2 = new Button("Prev");
        pane.setBottom(btn1);
        pane.setTop(btn2);
        Scene scene = new Scene(pane);
        textFlow.widthProperty().addListener(e -> System.out.println(textFlow.getWidth()));
        stage.setScene(scene);
        stage.setWidth(460);
//        stage.setHeight(1000);
        stage.show();
    }
}

/*
public class Main {
    public static void main(String[] args) {
        try {
            for (int i = 605; i <= 605; i++) {
                URL url = new URL("https://api.quran.com/api/v4/verses/by_page/" + i + "?words=true&word_fields=location,text_uthmani,text_imlaei,verse_key,code_v1,code_v2&fields=text_uthmani_simple,text_imlaei,chapter_id,code_v1,code_v2");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(response.toString());

                    // Write JSON to a file
                    String fileName = i + ".json";
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter("assets/quran" + File.separator + fileName))) {
                        writer.write(jsonNode.toPrettyString());
                        System.out.println("JSON data saved to: " + "assets/quran" + File.separator + fileName);
                    }
                }

                File jsonFile = new File("assets/quran/" + i + ".json");
                ObjectMapper objectMapper = new ObjectMapper();
                Juz juz = objectMapper.readValue(jsonFile, Juz.class);
                for (Verse verse : juz.getVerses()) {
                    System.out.println("Verse Key: " + verse.getVerse_key());
//                List<Word> words = verse.getWords();
//                for(Word word : words)
//                    System.out.println("Word Text Uthmani: " + word.getLocation());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
 */