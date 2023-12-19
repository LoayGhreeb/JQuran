package org.jquran.jquran;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.geometry.Side;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.util.Callback;
import org.controlsfx.control.HiddenSidesPane;


import java.io.IOException;
import java.util.ArrayList;

public class Main extends Application {
    int pageNum = 1;
    int fontVersion = 1;
    int fontSize = 25;
    TextFlow textFlow;
    HiddenSidesPane hiddenSidesPane = new HiddenSidesPane();
    ListView<CustomThing> listView;
    QuranChapters quranChapters = new QuranChapters();

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane borderPane = new BorderPane();
        borderPane.setPrefWidth(550);
        borderPane.setPrefHeight(1000);

        showSurahList();

        quranChapters = Query.getChapters();
        textFlow = new TextFlow();
        textFlow.setTextAlignment(TextAlignment.CENTER);
        textFlow.setStyle("-fx-background-color: #222222");
        setCurrentPage(pageNum);

        Button appendix = new Button("الفهرس");

        hiddenSidesPane.setContent(textFlow);
        hiddenSidesPane.setRight(listView);


        borderPane.setRight(listView);
        listView.setOnMouseClicked(event -> setCurrentPage(listView.getSelectionModel().getSelectedItem().getFirstPage()));

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
        stage.setScene(scene);
        stage.setWidth(800);
        stage.show();
    }

    private void showSurahList() throws IOException {
        QuranChapters quranChapters = Query.getChapters();

        ObservableList<CustomThing> data = FXCollections.observableArrayList();
        for (int i = 0; i < quranChapters.getChapters().size(); i++) {
            /// surah name
            String surahName = quranChapters.getChapters().get(i).getName_arabic();
            /// surah info
            int surahNumber = quranChapters.getChapters().get(i).getId();
            int verseCount = quranChapters.getChapters().get(i).getVerses_count();
            String place = quranChapters.getChapters().get(i).getRevelation_place();
            if( place.equals("makkah")){
                place = "مكيّة";
            }else{
                place = "مدنيّة";
            }
            int firstPage = quranChapters.getChapters().get(i).getPages().get(0);
            String surahInfo = "رقمها"+"_"+ surahNumber + "_" + "آياتها"+ "_" + verseCount + "_" + place;

            data.add(new CustomThing(surahName, surahInfo, firstPage));
            listView = new ListView<CustomThing>(data);

        }
        /** https://stackoverflow.com/questions/27438629/listview-with-custom-content-in-javafx */
        listView.setCellFactory(new Callback<ListView<CustomThing>, ListCell<CustomThing>>() {
            @Override
            public ListCell<CustomThing> call(ListView<CustomThing> listView) {
                return new CustomListCell();
            }
        });

//        listView.setCellFactory(listView -> new CustomListCell());
//        sidebar.getChildren().add( new Label(quranChapters.getChapters().get(1).getName_arabic()));
    }

    public void setCurrentPage(int newPageNum){
        try {
            Page page = Query.getPage(newPageNum, fontVersion);
            if (page != null) {

                ArrayList<Text>fullPages = page.getLines(fontVersion, quranChapters, fontSize, newPageNum);

                textFlow.getChildren().clear();

                for (int i = 1; i < 16; i++) {
                    fullPages.get(i).setFill(Color.WHITE);
                    textFlow.getChildren().addAll(fullPages.get(i));
                }

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






