package org.jquran.jquran;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.geometry.Side;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import org.controlsfx.control.HiddenSidesPane;


import java.io.IOException;

public class Main extends Application {
    int pageNum = 1;
    int fontVersion = 1;
    int fontSize = 33;
    Text pageVerses;
    TextFlow textFlow;
    HiddenSidesPane hiddenSidesPane = new HiddenSidesPane();

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane borderPane = new BorderPane();
        borderPane.setPrefWidth(550);
        borderPane.setPrefHeight(1000);

        showSurahList();

        pageVerses = new Text(Query.loadPage(pageNum, fontVersion).getLines(fontVersion));
        pageVerses.setFont(Query.loadFont(pageNum, fontVersion, fontSize));
        pageVerses.setFill(Color.WHITE);

        textFlow = new TextFlow(pageVerses);
        textFlow.setTextAlignment(TextAlignment.CENTER);
        textFlow.setStyle("-fx-background-color: #222222");

        Button appendix = new Button("الفهرس");


        hiddenSidesPane.setContent(textFlow);
//        listView.setOnMouseClicked(event -> setCurrentPage(listView.getSelectionModel().getSelectedItem().getFirstPage()));

        borderPane.setRight(textFlow);
        borderPane.setLeft(appendix);
        borderPane.setCenter(hiddenSidesPane);

        appendix.setOnAction(e -> {
            showSideBar();
        });
        Scene scene = new Scene(borderPane);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent e) -> {
            if (e.getCode() == KeyCode.LEFT) {
                setCurrentPage(pageNum + 1);
            } else if (e.getCode() == KeyCode.RIGHT) {
                setCurrentPage(pageNum - 1);
            }
            e.consume();
        });
        scene.getStylesheets().add(getClass().getResource("styles/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setWidth(800);
        stage.show();
    }

    private void showSurahList() throws IOException {
//        List<Chapter> chapters = Query.loadChapters();
//
//        ObservableList<CustomThing> data = FXCollections.observableArrayList();
//        for (int i = 0; i < quranChapters.getChapters().size(); i++) {
//            /// surah name
//            String surahName = quranChapters.getChapters().get(i).getName_arabic();
//            /// surah info
//            int surahNumber = quranChapters.getChapters().get(i).getId();
//            int verseCount = quranChapters.getChapters().get(i).getVerses_count();
//            String place = quranChapters.getChapters().get(i).getRevelation_place();
//            if( place.equals("makkah")){
//                place = "مكيّة";
//            }else{
//                place = "مدنيّة";
//            }
//            int firstPage = quranChapters.getChapters().get(i).getPages().getFirst();
//            String surahInfo = "رقمها"+"_"+ surahNumber + "_" + "آياتها"+ "_" + verseCount + "_" + place;
//
//            data.add(new CustomThing(surahName, surahInfo, firstPage));
//            listView = new ListView<CustomThing>(data);
//
//        }
        /** https://stackoverflow.com/questions/27438629/listview-with-custom-content-in-javafx */
//        listView.setCellFactory(new Callback<ListView<CustomThing>, ListCell<CustomThing>>() {
//            @Override
//            public ListCell<CustomThing> call(ListView<CustomThing> listView) {
//                return new CustomListCell();
//            }
//        });

//        chaptersListView.setCellFactory(chaptersListView -> new CustomListCell());
//        sidebar.getChildren().add( new Label(quranChapters.loadChapters().get(1).getName_arabic()));
    }

    public void setCurrentPage(int newPageNum) {
        try {
            Page page = Query.loadPage(newPageNum, fontVersion);
            if (page != null) {
                pageVerses.setText(page.getLines(fontVersion));
                pageVerses.setFont(Query.loadFont(newPageNum, fontVersion, fontSize));
                textFlow.getChildren().clear();
                textFlow.getChildren().add(pageVerses);
                pageNum = newPageNum;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSideBar() {
        if(hiddenSidesPane.getPinnedSide() == null)
            hiddenSidesPane.setPinnedSide(Side.RIGHT);
        else hiddenSidesPane.setPinnedSide(null);
    }
    
    public static void main(String[] args) {
        launch();
    }
}





