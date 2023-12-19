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

public class Main extends Application {
    int pageNum = 27;
    int fontVersion = 1;
    int fontSize = 33;
    Text pageVerses;
    TextFlow textFlow;
    VBox sidebar;
    HiddenSidesPane hiddenSidesPane = new HiddenSidesPane();
    ListView<CustomThing> listView;
//    ScrollPane sideBarScroller;

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane borderPane = new BorderPane();
        borderPane.setPrefWidth(550);
        borderPane.setPrefHeight(1000);
//        ListSelectionView<String> view = new ListSelectionView<>();

//        sidebar = new VBox();
//        sidebar.setStyle("-fx-background-color: #d3d3d3;");
//        sidebar.setVisible(true);
//        sidebar.setFillWidth(true);
//        sidebar.setPrefWidth(200);
        showSurahList();
//        sideBar = new SideBarView(borderPane);

//        sideBarScroller = new ScrollPane(sidebar);
//        sideBarScroller.setFitToWidth(true);

//        borderPane.setRight(sideBarScroller);

        pageVerses = new Text(Query.getPage(pageNum, fontVersion).getLines(fontVersion));
        pageVerses.setFont(Query.getFont(pageNum, fontVersion, fontSize));
        pageVerses.setFill(Color.WHITE);

        textFlow = new TextFlow(pageVerses);
        textFlow.setTextAlignment(TextAlignment.CENTER);
        textFlow.setStyle("-fx-background-color: #222222");

        Button appendix = new Button("الفهرس");


        hiddenSidesPane.setContent(textFlow);
        hiddenSidesPane.setRight(listView);


//        borderPane.setRight(sideBarScroller);
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
//        data.addAll(new CustomListView.CustomThing("Cheese", 123), new CustomListView.CustomThing("Horse", 456), new CustomListView.CustomThing("Jam", 789));
//        final ListView<CustomListView.CustomThing> listView = new ListView<CustomListView.CustomThing>(data);
        for (int i = 0; i < quranChapters.getChapters().size(); i++) {

            /// surah name
//            Label surahName = n ew Label(quranChapters.getChapters().get(i).getName_arabic());
            String surahName = quranChapters.getChapters().get(i).getName_arabic();

//            Font font = Font.font("Arial", FontWeight.BOLD, FontPosture.ITALIC, 18);
//            surahName.setFont(font);
            /// surah info
//            Label surahInfo = new Label();
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
//            Font font2 = Font.font("Arial", FontWeight.LIGHT, 10);


            data.add(new CustomThing(surahName, surahInfo, firstPage));
            listView = new ListView<CustomThing>(data);

//            surah.setFont(font);
//            sidebar.getChildren().add(surah);
//            surah.setStyle("-fx-pref-width: 100%");

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

    public void setCurrentPage(int newPageNum) {
        try {
            Page page = Query.getPage(newPageNum, fontVersion);
            if (page != null) {
                pageVerses.setText(page.getLines(fontVersion));
                pageVerses.setFont(Query.getFont(newPageNum, fontVersion, fontSize));
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






