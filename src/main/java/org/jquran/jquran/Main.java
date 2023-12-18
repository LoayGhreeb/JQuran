package org.jquran.jquran;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;


public class Main extends Application {
    int pageNum = 591;
    int fontVersion = 1;
    int fontSize = 33;
    Text pageVerses;
    TextFlow textFlow;
    @Override
    public void start(Stage stage) throws Exception {
//        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("sample.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 460, 950);

        BorderPane borderPane = new BorderPane();
        borderPane.setPrefWidth(550);
        borderPane.setPrefHeight(1000);

        pageVerses = new Text(Query.getPage(pageNum, fontVersion).getVersesByLine(fontVersion));
        pageVerses.setFont(Query.getFont(pageNum, fontSize, fontVersion));
        pageVerses.setFill(Color.WHITE);

        textFlow = new TextFlow(pageVerses);
        textFlow.setTextAlignment(TextAlignment.CENTER);
        textFlow.setStyle("-fx-background-color: #222222");
        borderPane.setCenter(textFlow);


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
            Page page = Query.getPage(pageNum + 1, fontVersion);
            if(page != null){
                pageVerses.setText(page.getVersesByLine(fontVersion));
                pageVerses.setFont(Query.getFont(pageNum + 1, fontSize, fontVersion));
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
            Page page = Query.getPage(pageNum - 1, fontVersion);
            if(page != null){
                pageVerses.setText(page.getVersesByLine(fontVersion));
                pageVerses.setFont(Query.getFont(pageNum - 1, fontSize, fontVersion));
                textFlow.getChildren().clear();
                textFlow.getChildren().add(pageVerses);
                pageNum--;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}