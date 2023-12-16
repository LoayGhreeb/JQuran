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
        List <Page> pages = new ArrayList<>();
        try {
            for (int i = 1; i < 605; i++) {
                File jsonFile = new File("assets/quran/" + i + ".json");
                ObjectMapper objectMapper = new ObjectMapper();
                Page page = objectMapper.readValue(jsonFile, Page.class);
                pages.add(page);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(Page page : pages){
            System.out.println(page.getVerses().getFirst().getPage_number());
        }
    }
}
 */