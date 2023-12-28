package org.jquran.jquran;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainWindow extends Application {
    private static final double PERCENTAGE = 0.9;
    private static int pageNumber = 1;
    private static final int fontSize = 33;
    private static final int fontVersion = 1;
    TextFlow pageTextFlow;
    @Override
    public void start(Stage primaryStage) throws Exception {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
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

        // chaptersList to list all chapters
        ListView<Chapter> chaptersList = new ListView<>();
        chaptersList.prefHeightProperty().bind(chaptersPane.heightProperty());
        chaptersList.setOnMouseClicked(event -> {
            try {
                setCurrentPage(chaptersList.getSelectionModel().getSelectedItem().getPages().getFirst());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        // get & list all chapters
        List<Chapter> chapters = Query.loadChapters();
        chaptersList.getItems().addAll(chapters);
        chaptersPane.getChildren().add(chaptersList);

        // text filed listener to handel search queries
        searchField.textProperty().addListener((observable, oldText, newText) -> {
            chaptersList.getItems().setAll(chapters.stream().filter(chapter -> chapter.getName_arabic().contains(newText) || String.valueOf(chapter.getId()).contains(newText)).collect(Collectors.toList()));
        });
        // Download audio Stage
        Button btn = new Button("القارئ");
        btn.setOnAction(e-> DownloadAudio.display());
        root.setBottom(btn);

        // Set Mushaf layout
        pageTextFlow = new TextFlow();
        pageTextFlow.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        pageTextFlow.setTextAlignment(TextAlignment.CENTER);
        pageTextFlow.setMinWidth(500);
        ScrollPane scrollPane = new ScrollPane(pageTextFlow);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        root.setCenter(scrollPane);
        setCurrentPage(pageNumber);


        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles/styles.css").toExternalForm());

        scene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent e) -> {
            if (e.getCode() == KeyCode.LEFT) {
                try {
                    setCurrentPage(pageNumber + 1);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            } else if (e.getCode() == KeyCode.RIGHT) {
                try {
                    setCurrentPage(pageNumber - 1);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
            e.consume();
        });

        primaryStage.setTitle("JQuran");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setCurrentPage(int newPageNumber) throws Exception {
        List<List<String>> lines = getFormattedPage(newPageNumber);
        if(lines == null) return;
        Font pageFont = Query.loadPageFont(newPageNumber, fontVersion, fontSize);
        pageTextFlow.getChildren().clear();
        for (List<String> line : lines) {
            for (String word : line) {
                // Surah name
                if(word.charAt(0) == '\\') {
                    Text surahName = new Text(word);
                    surahName.setFont(Query.loadSurahNameFont(fontSize + 10));
                    // Surah name box
                    Text box = new Text("ò");
                    box.setFont(Query.loadSurahNameFont(fontSize + 20));
                    surahName.setId("surahName");
                    box.setId("surahName");
                    StackPane stackPane = new StackPane();
                    stackPane.setAlignment(Pos.CENTER);
                    StackPane.setMargin(surahName, new Insets(13, 25, 0, 25));
                    stackPane.getChildren().addAll(box, surahName);
                    pageTextFlow.getChildren().add(stackPane);
                }
                // every word in a single Text object
                else {
                    Text currentWord = new Text(word);
                    // the current word is bismillah
                    if(word.charAt(0) == 'ó')
                        currentWord.setFont(Query.loadSurahNameFont(40));
                    else
                        currentWord.setFont(pageFont);

                    pageTextFlow.getChildren().add(currentWord);
                }
            }
            pageTextFlow.getChildren().add(new Text("\n"));
        }
        pageNumber = newPageNumber;
    }

    public List<List<String>> getFormattedPage(int pageNumber) throws Exception {
        List<List<String>> lines = new ArrayList<>();
        for (int i = 0; i < 15; i++)
            lines.add(new ArrayList<>());

        int curLineNum = 0;
        Page page = Query.loadPage(pageNumber, fontVersion);
        if(page == null) return null;
        List<Verse> pageVerses = page.getVerses();
        for (Verse verse : pageVerses) {
            List<Word> verseWords = verse.getWords();
            String[] verseKey = verseWords.getFirst().getVerse_key().split(":");
            int verseChapter = Integer.parseInt(verseKey[0]);
            int currentVerse = Integer.parseInt(verseKey[1]);

            String chapterCode = "\\"; //(سورة) Surah unicode in QCF_BSML
            chapterCode += Query.loadSurahNameCode(verseChapter);  // The surah name unicode in QCF_BSML

            if (currentVerse == 1) {
                if(pageNumber == 1){
                    lines.get(curLineNum).add(chapterCode);
                }
                else if (verseWords.getFirst().getLine_number() == 2) {
                    lines.get(curLineNum).add("ó");
                } else{
                    lines.get(curLineNum).add(chapterCode);
                    lines.get(curLineNum + 1).add("ó");
                }
            }
            // handle if the last line of the page at line 14 -> add box with the next surah name

            // Add words to the curLine
            for (Word verseWord : verseWords) {
                curLineNum = verseWord.getLine_number();
                lines.get(curLineNum - 1).add(verseWord.getCode(fontVersion));
            }
        }
        return lines;
    }

    public static void main(String[] args) {
        launch(args);
    }
}