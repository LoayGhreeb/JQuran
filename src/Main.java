import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("sample.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 460, 950);

        File jsonFile = new File("assets/quran/" + 4 + ".json");
        ObjectMapper objectMapper = new ObjectMapper();
        Page page = objectMapper.readValue(jsonFile, Page.class);
        List<Verse> verses = page.getVerses();

        Font customFont = Font.loadFont(new FileInputStream(new File("assets/fonts/QCFV1/QCF_P004.ttf")), 33);
        Text text = new Text();
        for(Verse verse : verses)
            text.setText(text.getText() + verse.getCode_v1() + ' ');
        text.setFont(customFont);
        text.setFill(Color.WHITE);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setTextAlignment(TextAlignment.RIGHT);
        textFlow.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        textFlow.setStyle("-fx-background-color: #222222");
        Scene scene = new Scene(textFlow, 460, 950);
        stage.setScene(scene);
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