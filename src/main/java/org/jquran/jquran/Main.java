package org.jquran.jquran;

import javafx.application.Application;
import javafx.geometry.NodeOrientation;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;


public class Main extends Application {
    int pageNum = 177;
    int fontSize = 26;
    Text pageVerses;
    TextFlow textFlow;
    @Override
    public void start(Stage stage) throws Exception {
//        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("sample.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 460, 950);

        BorderPane borderPane = new BorderPane();
        borderPane.setPrefWidth(550);
        borderPane.setPrefHeight(1000);

        pageVerses = new Text(Query.getPage(pageNum).getVersesByLine());
        pageVerses.setFont(Query.getFont(pageNum, fontSize));
        pageVerses.setFill(Color.WHITE);

        textFlow = new TextFlow(pageVerses);
        textFlow.setTextAlignment(TextAlignment.CENTER);
        textFlow.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        textFlow.setStyle("-fx-background-color: #222222");
        borderPane.setCenter(textFlow);
        /*
        List<Page> pages= new ArrayList<>();
        List<Font> fonts = new ArrayList<>();
        for(int i = 1; i <= 604; i++){
            File jsonFile = new File("src/main/resources/org/assets/quran/" + i + ".json");
            Page page = objectMapper.readValue(jsonFile, Page.class);
            pages.add(page);
            if(i < 10){
                fonts.add(Font.loadFont(new FileInputStream(new File("src/main/resources/org/assets/fonts/QCFV1/QCF_P00"+i+".ttf")), 33));
            }else if ( i < 100){
                fonts.add(Font.loadFont(new FileInputStream(new File("src/main/resources/org/assets/fonts/QCFV1/QCF_P0"+i+".ttf")), 33));
            }else{
                fonts.add(Font.loadFont(new FileInputStream(new File("src/main/resources/org/assets/fonts/QCFV1/QCF_P"+i+".ttf")), 33));
            }
        }
         */
        Button nextButton = new Button("Next");
        nextButton.setOnAction(e-> {
            nextPage();
        });
        Button prevButton = new Button("Prev");
        prevButton.setOnAction(e-> {
            previousPage();
        });

        borderPane.setBottom(nextButton);
        borderPane.setTop(prevButton);
        Scene scene = new Scene(borderPane);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent e) -> {
            if (e.getCode() == KeyCode.LEFT) {
                nextPage();
            }else if (e.getCode() == KeyCode.RIGHT){
                previousPage();
            }
            e.consume();
        });

        textFlow.widthProperty().addListener(e -> System.out.println(textFlow.getWidth()));
        stage.setScene(scene);
        stage.setWidth(460);
        stage.show();
    }
    public void nextPage(){
        try {
            Page page = Query.getPage(pageNum + 1);
            if(page != null){
                pageVerses.setText(page.getVersesByLine());
                pageVerses.setFont(Query.getFont(pageNum + 1, fontSize));
                textFlow.getChildren().clear();
                textFlow.getChildren().add(pageVerses);
                pageNum++;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    public void previousPage(){
        try {
            Page page = Query.getPage(pageNum - 1);
            if(page != null){
                pageVerses.setText(page.getVersesByLine());
                pageVerses.setFont(Query.getFont(pageNum - 1, fontSize));
                textFlow.getChildren().clear();
                textFlow.getChildren().add(pageVerses);
                pageNum--;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}



