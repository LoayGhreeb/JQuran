package org.jquran.jquran;

import atlantafx.base.theme.CupertinoDark;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class MainWindow extends Application {
    private static final double PERCENTAGE = 0.9;
    @Override
    public void start(Stage primaryStage) throws Exception {
        Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
        // Set the stage size
        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        primaryStage.setWidth(screenWidth * PERCENTAGE);
        primaryStage.setHeight(screenHeight * PERCENTAGE);

        // The root Pane
        BorderPane root = new BorderPane();
        root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        // Menubar
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(new Menu("ملف"), new Menu("عرض"), new Menu("بحث"));
        root.setTop(menuBar);

        /* create Accordion (appendix) contains the chaptersPane &
         * chaptersPane used to combine search field with listview of chapters name into one component  */
        VBox chaptersPane = new VBox();
        chaptersPane.setSpacing(10);
        Accordion appendix = new Accordion(new TitledPane("الفهرس", chaptersPane));
        appendix.setExpandedPane(appendix.getPanes().getFirst());
        StackPane accordionContainer = new StackPane(appendix);
        accordionContainer.setPadding(new Insets(10));
        root.setLeft(accordionContainer);

        // text field to search for chapter name or chapter number & add it to chapterPane
        TextField searchField = new TextField();
        searchField.setPromptText("اسم السورة");
        chaptersPane.getChildren().add(searchField);

        // chaptersListView to list all chapters
        ListView<Chapter> chaptersListView = new ListView<>();
        chaptersListView.prefHeightProperty().bind(chaptersPane.heightProperty());

        // get & list all chapters
        List<Chapter> chapters = Query.loadChapters().getChapters();
        chaptersListView.getItems().addAll(chapters);
        chaptersPane.getChildren().add(chaptersListView);

        // text filed listener to handel search queries
        searchField.textProperty().addListener((observable, oldText, newText) -> {
            chaptersListView.getItems().setAll(chapters.stream().filter(chapter -> chapter.getName_arabic().contains(newText) || String.valueOf(chapter.getId()).contains(newText)).collect(Collectors.toList()));
        });

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles/styles.css").toExternalForm());
        primaryStage.setTitle("JQuran");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}