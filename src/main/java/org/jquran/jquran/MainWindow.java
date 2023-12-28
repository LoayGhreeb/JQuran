package org.jquran.jquran;

import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import atlantafx.base.theme.Styles;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MainWindow extends Application {
    private static final double PERCENTAGE = 0.9;
    private static final int fontSize = 32;
    private static int fontVersion = 1;
    private static TextFlow pageTextFlow;
    private static ListView<Chapter> chaptersList;
    private static final SimpleIntegerProperty pageNumber = new SimpleIntegerProperty(1);

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

        Menu viewMenu = new Menu("عرض");
        ToggleGroup group1 = new ToggleGroup();

        ToggleSwitch light = new ToggleSwitch("Light");
        light.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);
        MenuItem lightMenuItem = new MenuItem(null, light);
        light.setToggleGroup(group1);

        ToggleSwitch dark = new ToggleSwitch("Dark");
        dark.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);
        MenuItem darkMenuItem = new MenuItem(null, dark);
        dark.setToggleGroup(group1);
        dark.setSelected(true);
        dark.setDisable(true);

        ToggleGroup group2 = new ToggleGroup();

        ToggleSwitch v1 = new ToggleSwitch("الخط الأول");
        v1.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);
        MenuItem v1MenuItem = new MenuItem(null, v1);
        v1.setToggleGroup(group2);
        v1.setDisable(true);
        v1.setSelected(true);

        ToggleSwitch v2 = new ToggleSwitch("الخط الثاني");
        v2.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);
        MenuItem v2MenuItem = new MenuItem(null, v2);
        v2.setToggleGroup(group2);

        viewMenu.getItems().addAll(lightMenuItem, darkMenuItem, new SeparatorMenuItem(), v1MenuItem, v2MenuItem);

        dark.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
            light.setDisable(false);
            dark.setDisable(true);
        });

        light.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
            dark.setDisable(false);
            light.setDisable(true);
        });

        v1.selectedProperty().addListener((observable, oldValue, newValue) -> {
            fontVersion = 1;
            try {
                setCurrentPage(pageNumber.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            v2.setDisable(false);
            v1.setDisable(true);
        });

        v2.selectedProperty().addListener((observable, oldValue, newValue) -> {
            fontVersion = 2;
            try {
                setCurrentPage(pageNumber.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            v1.setDisable(false);
            v2.setDisable(true);
        });

        menuBar.getMenus().addAll(new Menu("ملف"), viewMenu, new Menu("بحث"));
        root.setTop(menuBar);

        /*
         * create Accordion (appendix) contains the chaptersPane &
         * chaptersPane used to combine search field with listview of chapters name into
         * one component
         */
        VBox chaptersPane = new VBox();
        chaptersPane.setSpacing(10);
        Accordion appendix = new Accordion(new TitledPane("الفهرس", chaptersPane));
        appendix.setExpandedPane(appendix.getPanes().getFirst());
        StackPane accordionContainer = new StackPane(appendix);
        accordionContainer.setPadding(new Insets(10));
        root.setLeft(accordionContainer);

        // text field to search for chapter name or chapter number & add it to
        // chapterPane
        TextField searchField = new TextField();
        searchField.setPromptText("اسم السورة");
        chaptersPane.getChildren().add(searchField);

        // chaptersList to list all chapters
        chaptersList = new ListView<>();
        chaptersList.prefHeightProperty().bind(chaptersPane.heightProperty());

        // get & list all chapters
        List<Chapter> chapters = Query.loadChapters();
        chaptersList.getItems().addAll(chapters);
        chaptersPane.getChildren().add(chaptersList);
        chaptersList.getSelectionModel().select(pageNumber.get() - 1);

        chaptersList.setOnMouseClicked(event -> {
            try {
                setCurrentPage(chaptersList.getSelectionModel().getSelectedItem().getPages().getFirst());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // text filed listener to handel search queries
        searchField.textProperty().addListener((observable, oldText, newText) -> {
            chaptersList
                    .getItems().setAll(
                            chapters.stream()
                                    .filter(chapter -> chapter.getName_arabic().contains(newText)
                                            || String.valueOf(chapter.getId()).contains(newText))
                                    .collect(Collectors.toList()));
        });

        // Set Mushaf layout
        BorderPane mushafLayout = new BorderPane();
        pageTextFlow = new TextFlow();
        pageTextFlow.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        pageTextFlow.setTextAlignment(TextAlignment.CENTER);
        pageTextFlow.setMinWidth(500);
        ScrollPane scrollPane = new ScrollPane(pageTextFlow);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        mushafLayout.setCenter(scrollPane);
        root.setCenter(mushafLayout);
        setCurrentPage(pageNumber.get());

        Button nextButton = new Button("التالي");
        nextButton.setOnAction(e -> {
            try {
                setCurrentPage(pageNumber.get() + 1);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        Button prevButton = new Button("السابق");
        prevButton.setOnAction(e -> {
            try {
                setCurrentPage(pageNumber.get() - 1);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        
        HBox navigationContainer = new HBox(10);
        navigationContainer.setAlignment(Pos.CENTER);
        navigationContainer.getChildren().addAll(prevButton, nextButton);
        mushafLayout.setBottom(navigationContainer);
        // audio player
        HBox hB = new HBox();
        hB.setPadding(new Insets(20));
        hB.setSpacing(20);
        var reciterComboBox = new ComboBox<String>();
        List<Reciter> reciters = Query.loadReciters();
        for (Reciter reciter : reciters) {
            /// reciters name
            String reciterSName = reciter.getTranslated_name().getName();
            /// reciters style
            String reciterStyle = reciter.getStyle();
            if (reciterStyle == null)
                reciterComboBox.getItems().add(reciterSName);
            else
                reciterComboBox.getItems().add(reciterSName + ' ' + reciterStyle);
        }
        reciterComboBox.getSelectionModel().selectFirst();
        var surahComboBox = new ComboBox<String>();
        for (Chapter chapter : chapters) {
            /// Surah name
            String surahName = chapter.getName_arabic();
            surahComboBox.getItems().add(surahName);
        }
        surahComboBox.getSelectionModel().selectFirst();

        Button start = new Button("تشغيل");
        start.setOnAction(e -> {
            File f = new File("src/main/resources/org/jquran/jquran/Quran_Audio/"
                    + reciterComboBox.getSelectionModel().getSelectedItem() + "/");
            File[] l = f.listFiles();
            List<File> lf = new ArrayList<File>();
            if (l == null) {
                var alert = new Alert(AlertType.ERROR);
                alert.setTitle("Exception Dialog");
                alert.setHeaderText("No files found");
                alert.setContentText("you have to download the audio files first");

                alert.initOwner(root.getScene().getWindow());
                alert.showAndWait();

                return;
            }
            for (File x : l) {
                System.out.println(String.format("%03d", surahComboBox.getSelectionModel().getSelectedIndex() + 1));
                System.out.println(x.getName()
                        .toString().substring(0, 3));
                System.out.println(String.format("%03d", surahComboBox.getSelectionModel().getSelectedIndex() + 1)
                        .equals(x.getName()
                                .toString().substring(0, 3)));
                if (String.format("%03d", surahComboBox.getSelectionModel().getSelectedIndex() + 1)
                        .equals(x.getName()
                                .toString().substring(0, 3))) {
                    lf.add(x);
                }
            }
            if (lf.isEmpty()) {
                var alert = new Alert(AlertType.ERROR);
                alert.setTitle("Exception Dialog");
                alert.setHeaderText("No files found");
                alert.setContentText("you have to download the audio files first");

                alert.initOwner(root.getScene().getWindow());
                alert.showAndWait();

                return;
            }
            int i = 0;
            ArrayList<MediaPlayer> players = new ArrayList<>();
            Collections.sort(lf);
            while (i < lf.size()) {
                MediaPlayer mp = new MediaPlayer(new Media(lf.get(i).toURI().toString()));
                players.add(mp);
                i++;
            }
            MediaControl mediaControl = new MediaControl(players);
            if (hB.getChildren().size() < 5)
                hB.getChildren().add(mediaControl);

            else {
                hB.getChildren().remove(4);
                hB.getChildren().add(mediaControl);
            }
            primaryStage.setWidth(screenWidth * PERCENTAGE);
        });

        Button btn = new Button("ادارة التحميلات");
        btn.setOnAction(e -> {
            try {
                DownloadAudio.display();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        hB.getChildren().addAll(start, reciterComboBox, surahComboBox, btn);
        hB.setAlignment(Pos.CENTER);
        root.setBottom(hB);
        // Download audio Stage

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles/styles.css").toExternalForm());
        scene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent e) -> {
            if (e.getCode() == KeyCode.LEFT) {
                try {
                    setCurrentPage(pageNumber.get() + 1);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            } else if (e.getCode() == KeyCode.RIGHT) {
                try {
                    setCurrentPage(pageNumber.get() - 1);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        primaryStage.setTitle("JQuran");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setCurrentPage(int newPageNumber) throws Exception {
        List<List<String>> lines = getFormattedPage(newPageNumber);
        if (lines == null)
            return;
        Font pageFont = Query.loadPageFont(newPageNumber, fontVersion, fontSize);
        pageTextFlow.getChildren().clear();
        for (List<String> line : lines) {
            for (String word : line) {
                // Surah name
                if (word.charAt(0) == '\\') {
                    Text surahName = new Text(word);
                    surahName.setFont(Query.loadSurahNameFont(30));
                    // Surah name box
                    Text box = new Text("ò");
                    box.setFont(Query.loadSurahNameFont(50));
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
                    if (word.charAt(0) == 'ó')
                        currentWord.setFont(Query.loadSurahNameFont(40));
                    else
                        currentWord.setFont(pageFont);
                    currentWord.setId("verse");
                    pageTextFlow.getChildren().add(currentWord);
                }
            }
            pageTextFlow.getChildren().add(new Text("\n"));
        }
    }

    public List<List<String>> getFormattedPage(int newPageNumber) throws Exception {
        List<List<String>> lines = new ArrayList<>();
        for (int i = 0; i < 15; i++)
            lines.add(new ArrayList<>());

        int curLineNum = 0;
        Page page = Query.loadPage(newPageNumber, fontVersion);
        if (page == null)
            return null;
        List<Verse> pageVerses = page.getVerses();
        for (Verse verse : pageVerses) {
            List<Word> verseWords = verse.getWords();
            String[] verseKey = verseWords.getFirst().getVerse_key().split(":");
            int verseChapter = Integer.parseInt(verseKey[0]);
            int currentVerse = Integer.parseInt(verseKey[1]);

            String chapterCode = "\\"; // (سورة) Surah unicode in QCF_BSML
            chapterCode += Query.loadSurahNameCode(verseChapter); // The surah name unicode in QCF_BSML

            if (currentVerse == 1) {
                if (newPageNumber == 1 || newPageNumber == 187) {
                    lines.get(curLineNum).add(chapterCode);
                } else if (verseWords.getFirst().getLine_number() == 2) {
                    lines.get(curLineNum).add("ó");
                } else {
                    lines.get(curLineNum).add(chapterCode);
                    lines.get(curLineNum + 1).add("ó");
                }
            }
            // handle if the last line of the page at line 14 -> add box with the next surah
            // name

            // Add words to the curLine
            for (Word verseWord : verseWords) {
                curLineNum = verseWord.getLine_number();
                lines.get(curLineNum - 1).add(verseWord.getCode(fontVersion));
            }
        }

        int chapterId = page.getVerses().getFirst().getChapter_id();
        pageNumber.addListener((observable, old, newValue) -> {
            chaptersList.getSelectionModel().select(chapterId - 1);
        });
        pageNumber.set(newPageNumber);
        return lines;
    }

    public static void main(String[] args) {
        launch(args);
    }
}