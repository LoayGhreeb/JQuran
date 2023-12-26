package org.jquran.jquran;

import atlantafx.base.theme.NordLight;
import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.controlsfx.control.HiddenSidesPane;

import java.util.List;
import java.util.stream.Collectors;

public class MainWindow extends Application {
    VBox root;
    MenuBar menuBar;
    SplitPane splitPane;
    HiddenSidesPane hiddenSidesPane;
    ListView<Chapter> listView;
    //Text font color : #e7e9ea
    //Background color: #1f2125
    // secondary color : #44a3aa

    @Override
    public void start(Stage primaryStage) throws Exception {
        // The root Pane
        Application.setUserAgentStylesheet(new NordLight().getUserAgentStylesheet());
        root = new VBox();
        root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);


        root.setSpacing(10);
        root.setPadding(new Insets(10, 10, 10, 10));

        // Menubar
        menuBar = new MenuBar();
        Button aa = new Button("الفهرس");
        Menu appendix = new Menu();
        appendix.setGraphic(aa);
        menuBar.getMenus().addAll(appendix, new Menu("ملف"), new Menu("عرض"));
        root.getChildren().add(menuBar);
        aa.setOnAction(event -> showSideBar());

        // chapters pane to combine search field with listview into one component
        VBox chaptersPane = new VBox();
        chaptersPane.setSpacing(10);
        chaptersPane.setPadding(new Insets(10, 10, 10, 10));

        // text field to search for chapter name or chapter number
        TextField searchField = new TextField();
        searchField.setPromptText("اسم السورة");

        // listview to list all chapters
        listView = new ListView<>();

        // get & list all chapters
        List<Chapter> chapters = Query.loadChapters().getChapters();
        listView.getItems().addAll(chapters);

        // text filed listener to handel search queries
        searchField.textProperty().addListener((observable, oldText, newText) -> {
            listView.getItems().setAll(chapters.stream().filter(chapter -> chapter.getName_arabic().contains(newText) || String.valueOf(chapter.getId()).contains(newText)).collect(Collectors.toList()));
        });


        // combine listview + text field
        chaptersPane.getChildren().add(searchField);
        chaptersPane.getChildren().add(listView);


        hiddenSidesPane = new HiddenSidesPane();
        root.getChildren().add(hiddenSidesPane);
        hiddenSidesPane.setPrefHeight(500);
        Button btn= new Button("Quran Pages");
        hiddenSidesPane.setContent(btn);
        hiddenSidesPane.setLeft(chaptersPane);

        btn.setOnAction(e -> {
            showSideBar();
        });
        hiddenSidesPane.setPinnedSide(Side.LEFT);

        Text text = new Text("Hello");
//        text.getStyleClass().add(Styles.TEXT_BOLD);
//        text.getStyleClass().add(Styles.TITLE_1);
        root.getChildren().add(text);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles/styles.css").toExternalForm());
        primaryStage.setTitle("JQuran");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();

    }

    public void showSideBar() {
        if(hiddenSidesPane.getPinnedSide() == null)
            hiddenSidesPane.setPinnedSide(Side.LEFT);
        else hiddenSidesPane.setPinnedSide(null);
    }
    public static void main(String[] args) {
        launch(args);
    }
}
