package org.jquran.jquran;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.ProgressSliderSkin;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import atlantafx.base.theme.Styles;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material.Material;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class MainWindow extends Application {
    private static final double PERCENTAGE = 0.9;
    private static final int fontSize = 33;
    private static int fontVersion = 1;
    private static TextFlow pageTextFlow;
    private static List<Chapter> chapters;
    private static ListView<Chapter> chaptersList;
    private static final SimpleIntegerProperty pageNumber = new SimpleIntegerProperty(76);

    private static MediaPlayer mediaPlayer;
    @Override
    public void start(Stage primaryStage) throws URISyntaxException {
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
            setCurrentPage(pageNumber.get());
            v2.setDisable(false);
            v1.setDisable(true);
        });

        v2.selectedProperty().addListener((observable, oldValue, newValue) -> {
            fontVersion = 2;
            setCurrentPage(pageNumber.get());
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
        CustomTextField searchField = new CustomTextField();
        searchField.setPromptText("اسم السورة");
        searchField.setLeft(new FontIcon(Material.SEARCH));
        chaptersPane.getChildren().add(searchField);

        // chaptersList to list all chapters
        chaptersList = new ListView<>();
        chaptersList.prefHeightProperty().bind(chaptersPane.heightProperty());

        // get & list all chapters
        chapters = Query.loadChapters();
        chaptersList.getItems().addAll(chapters);
        chaptersPane.getChildren().add(chaptersList);
        chaptersList.getSelectionModel().select(pageNumber.get() - 1);

        chaptersList.setOnMouseClicked(event -> setCurrentPage(chaptersList.getSelectionModel().getSelectedItem().getPages().getFirst()));

        // text filed listener to handel search queries
        searchField.textProperty().addListener((observable, oldText, newText) -> chaptersList.getItems().setAll(chapters.stream().filter(chapter -> chapter.getName_arabic().contains(newText) || String.valueOf(chapter.getId()).contains(newText)).collect(Collectors.toList())));

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

        Button nextButton = new Button(null,new FontIcon(Material.ARROW_LEFT));
        nextButton.setOnAction(e -> setCurrentPage(pageNumber.get() + 1));

        Button prevButton = new Button(null, new FontIcon(Material.ARROW_RIGHT));
        prevButton.setOnAction(e -> setCurrentPage(pageNumber.get() - 1));

        // Navigation buttons
        HBox navigationContainer = new HBox(10);
        navigationContainer.setPadding(new Insets(10));
        navigationContainer.setAlignment(Pos.CENTER);
        navigationContainer.getChildren().addAll(prevButton, nextButton);
        mushafLayout.setBottom(navigationContainer);

        // audio player
        HBox audioPlayer = new HBox();
        audioPlayer.setPadding(new Insets(20));
        audioPlayer.setSpacing(20);
        audioPlayer.setAlignment(Pos.CENTER);
        audioPlayer.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        root.setBottom(audioPlayer);

        // media player
        Media media = Query.loadMedia(7, 1);
        if(media != null) mediaPlayer = new MediaPlayer(media);
        else return;
        // reciter combo boxes
        List<Reciter> reciters = Query.loadReciters();
        ComboBox<Reciter> reciterComboBox = new ComboBox<>();
        reciterComboBox.getItems().addAll(reciters);
        reciterComboBox.getSelectionModel().select(6);
        reciterComboBox.setPrefWidth(300);


        // surah combo boxes
        ComboBox<Chapter> surahComboBox = new ComboBox<>();
        surahComboBox.getItems().addAll(chapters);
        surahComboBox.getSelectionModel().selectFirst();
        surahComboBox.setPrefWidth(150);


        // time slider
        Slider timeSlider = new Slider();
        timeSlider.setSkin(new ProgressSliderSkin(timeSlider));
        timeSlider.getStyleClass().add(Styles.SMALL);
        timeSlider.setMinWidth(300);

        // time labels, play & pause button
        Label currentTimeLabel = new Label();
        Label totalTimeLabel = new Label("00:00:00");
        ToggleButton playPauseButton = new ToggleButton(null, new FontIcon(Material.PLAY_ARROW));

        // set the total time label when the media is ready
        mediaPlayer.setOnReady(() -> {
            totalTimeLabel.setText(String.format("%02d:%02d:%02d", (int)mediaPlayer.getTotalDuration().toHours(), (int)mediaPlayer.getTotalDuration().toMinutes() % 60, (int)mediaPlayer.getTotalDuration().toSeconds() % 60));
            currentTimeLabel.setText("00:00:00");
            timeSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
        });

        // set the play & pause button to pause when the media is finished
        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.stop();
            playPauseButton.setSelected(false);
        });

        // play & pause button listener
        playPauseButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                playPauseButton.setGraphic(new FontIcon(Material.PAUSE));
                mediaPlayer.play();
                totalTimeLabel.setText(String.format("%02d:%02d:%02d", (int)mediaPlayer.getTotalDuration().toHours(), (int)mediaPlayer.getTotalDuration().toMinutes() % 60, (int)mediaPlayer.getTotalDuration().toSeconds() % 60));
            } else {
                playPauseButton.setGraphic(new FontIcon(Material.PLAY_ARROW));
                mediaPlayer.pause();
            }
        });

        // handle seeking and update the current media time, used atomic boolean to executed as a single, indivisible operation without interference from other threads.
        AtomicBoolean isSeeking = new AtomicBoolean(false);

        timeSlider.setOnMousePressed(e -> isSeeking.set(true));

        // update the current time label when the user drag the time slider
        timeSlider.setOnMouseDragged(e -> currentTimeLabel.setText(String.format("%02d:%02d:%02d", (int)timeSlider.getValue() / 3600, (int)timeSlider.getValue() / 60 % 60, (int)timeSlider.getValue() % 60)));

        // seek to the new time when the user release the mouse button
        timeSlider.setOnMouseReleased(e -> {
            if(isSeeking.get()) {
                if(playPauseButton.isSelected()) mediaPlayer.play();
                currentTimeLabel.setText(String.format("%02d:%02d:%02d", (int)timeSlider.getValue() / 3600, (int)timeSlider.getValue() / 60 % 60, (int)timeSlider.getValue() % 60));
                mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
                isSeeking.set(false);
            }
        });

        // update the current time label & time slider when the media is playing
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (!isSeeking.get()) {
                timeSlider.setValue(newValue.toSeconds());
                currentTimeLabel.setText(String.format("%02d:%02d:%02d", (int) newValue.toHours(), (int) newValue.toMinutes() % 60, (int) newValue.toSeconds() % 60));
            }
        });

        // volume slider
        Slider volumeSlider = new Slider(0, 1, 1);
        volumeSlider.getStyleClass().add(Styles.SMALL);
        volumeSlider.setSkin(new ProgressSliderSkin(volumeSlider));
        volumeSlider.setMinWidth(100);

        mediaPlayer.volumeProperty().bind(volumeSlider.valueProperty());

        // muteButton button
        ToggleButton muteButton = new ToggleButton(null, new FontIcon(Material.VOLUME_UP));
        muteButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                muteButton.setGraphic(new FontIcon(Material.VOLUME_OFF));
                mediaPlayer.setMute(true);
            } else {
                muteButton.setGraphic(new FontIcon(Material.VOLUME_UP));
                mediaPlayer.setMute(false);
            }
        });

        // disable muteButton button if volume is 0 & vice versa
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == 0) {
                muteButton.setGraphic(new FontIcon(Material.VOLUME_OFF));
                muteButton.setSelected(true);
                muteButton.setDisable(true);
            } else {
                muteButton.setGraphic(new FontIcon(Material.VOLUME_UP));
                muteButton.setSelected(false);
                muteButton.setDisable(false);
            }
        });

        /*
        reciterComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Media newMedia = Query.loadMedia(newValue.getId(), surahComboBox.getSelectionModel().getSelectedItem().getId());
                if(newMedia != null) {
                    mediaPlayer.stop();
                    playPauseButton.setSelected(false);
                    mediaPlayer = new MediaPlayer(newMedia);
                }
            }
        });

        surahComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Media newMedia = Query.loadMedia(reciterComboBox.getSelectionModel().getSelectedItem().getId(), newValue.getId());
                if(newMedia != null) {
                    mediaPlayer.stop();
                    playPauseButton.setSelected(false);
                    mediaPlayer = new MediaPlayer(newMedia);
                }
            }
        });
        */

        // download manager button
        Button downloadManager = new Button( "ادارة التحميلات", new FontIcon(Material.CLOUD_DOWNLOAD));
        downloadManager.setOnAction(e -> DownloadAudio.display());

        audioPlayer.getChildren().addAll(reciterComboBox, surahComboBox, playPauseButton, currentTimeLabel, timeSlider, totalTimeLabel, downloadManager, muteButton, volumeSlider);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles/styles.css").toExternalForm());
        scene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent e) -> {
            if (e.getCode() == KeyCode.LEFT) setCurrentPage(pageNumber.get() + 1);
            else if (e.getCode() == KeyCode.RIGHT) setCurrentPage(pageNumber.get() - 1);
        });
        primaryStage.setTitle("JQuran");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setCurrentPage(int newPageNumber) {
        List<List<String>> lines = getFormattedPage(newPageNumber);
        if (lines == null) return;
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
                    if (fontVersion == 1) box.setFont(Query.loadSurahNameFont(60));
                    else box.setFont(Query.loadSurahNameFont(65));
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

    public List<List<String>> getFormattedPage(int newPageNumber) {
        List<List<String>> lines = new ArrayList<>();
        for (int i = 0; i < 15; i++)
            lines.add(new ArrayList<>());

        int curLineNum = 0;
        Page page = Query.loadPage(newPageNumber, fontVersion);
        if (page == null) return null;
        List<Verse> pageVerses = page.getVerses();
        for (Verse verse : pageVerses) {
            List<Word> verseWords = verse.getWords();
            String[] verseKey = verseWords.getFirst().getVerse_key().split(":");
            int verseChapter = Integer.parseInt(verseKey[0]);
            int currentVerse = Integer.parseInt(verseKey[1]);

            String chapterCode = "\\"; // (سورة) Surah unicode in QCF_BSML

            if (currentVerse == 1) {
                if (newPageNumber == 1 || newPageNumber == 187) {
                    lines.get(curLineNum).add(chapterCode + Query.loadSurahNameCode(verseChapter)); // The surah name unicode in QCF_BSML
                } else if (verseWords.getFirst().getLine_number() == 2) {
                    lines.get(curLineNum).add("ó");
                } else {
                    lines.get(curLineNum).add(chapterCode + Query.loadSurahNameCode(verseChapter));
                    lines.get(curLineNum + 1).add("ó");
                }
            }
            // handle if the last line of the page at line 14 -> add box with the next surah name (not completed, yet)
            else if(currentVerse == chapters.get(verseChapter - 1).getVerses_count() && verseWords.getLast().getLine_number() == 14) {
                lines.get(14).add(chapterCode + Query.loadSurahNameCode(verseChapter + 1));
            }

            // Add words to the curLine
            for (Word verseWord : verseWords) {
                curLineNum = verseWord.getLine_number();
                lines.get(curLineNum - 1).add(verseWord.getCode(fontVersion));
            }
        }
        pageNumber.set(newPageNumber);
        return lines;
    }
}