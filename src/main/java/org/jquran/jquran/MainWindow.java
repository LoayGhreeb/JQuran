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

    // MediaPlayer components
    private static MediaPlayer mediaPlayer;
    private static ComboBox<Chapter> surahComboBox;
    private static ComboBox<Reciter> reciterComboBox;
    private static Slider timeSlider, volumeSlider;
    private static Label currentTimeLabel, totalTimeLabel;
    private static ToggleButton playPauseButton,  muteButton;

    @Override
    public void start(Stage primaryStage) {
        Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());

        // The root Pane
        BorderPane root = new BorderPane();
        root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        // Menubar
        MenuBar menuBar = new MenuBar();
        Menu viewMenu = new Menu("عرض");

        // theme button group
        ToggleGroup themeButtonGroup = new ToggleGroup();

        ToggleSwitch light = new ToggleSwitch("Light");
        light.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);
        light.setToggleGroup(themeButtonGroup);

        ToggleSwitch dark = new ToggleSwitch("Dark");
        dark.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);
        dark.setToggleGroup(themeButtonGroup);
        dark.setSelected(true);
        dark.setDisable(true);

        // font version button group
        ToggleGroup fontVersionGroup = new ToggleGroup();

        ToggleSwitch v1 = new ToggleSwitch("الخط الأول");
        v1.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);
        v1.setToggleGroup(fontVersionGroup);
        v1.setDisable(true);
        v1.setSelected(true);

        ToggleSwitch v2 = new ToggleSwitch("الخط الثاني");
        v2.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);
        v2.setToggleGroup(fontVersionGroup);

        // add menus to the view menu
        viewMenu.getItems().addAll(new MenuItem(null, light), new MenuItem(null, dark), new SeparatorMenuItem(), new MenuItem(null, v1), new MenuItem(null, v2));

        // theme listener
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

        // font version listener
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

        // add menus to the menu bar
        menuBar.getMenus().addAll(new Menu("ملف"), viewMenu, new Menu("بحث"));
        root.setTop(menuBar);

        // create chaptersPane & add it to the appendix
        VBox chaptersPane = new VBox();
        chaptersPane.setSpacing(10);
        Accordion appendix = new Accordion(new TitledPane("الفهرس", chaptersPane));
        appendix.setExpandedPane(appendix.getPanes().getFirst());
        StackPane accordionContainer = new StackPane(appendix);
        accordionContainer.setPadding(new Insets(10));
        root.setLeft(accordionContainer);

        // search field to search for chapters
        CustomTextField searchField = new CustomTextField();
        searchField.setPromptText("اسم السورة");
        searchField.setLeft(new FontIcon(Material.SEARCH));
        chaptersPane.getChildren().add(searchField);

        // load chapters name & add it to the listview
        chapters = Query.loadChapters();

        chaptersList = new ListView<>();
        chaptersList.getItems().addAll(chapters);
        chaptersList.prefHeightProperty().bind(chaptersPane.heightProperty());
        chaptersPane.getChildren().add(chaptersList);
        chaptersList.getSelectionModel().select(pageNumber.get() - 1);
        chaptersList.setOnMouseClicked(e -> setCurrentPage(chaptersList.getSelectionModel().getSelectedItem().getPages().getFirst()));

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

        // next & previous page buttons
        Button nextButton = new Button(null,new FontIcon(Material.ARROW_LEFT));
        nextButton.setOnAction(e -> setCurrentPage(pageNumber.get() + 1));

        Button prevButton = new Button(null, new FontIcon(Material.ARROW_RIGHT));
        prevButton.setOnAction(e -> setCurrentPage(pageNumber.get() - 1));

        // navigation container to hold the next & previous page buttons
        HBox navigationContainer = new HBox(10);
        navigationContainer.setPadding(new Insets(10));
        navigationContainer.setAlignment(Pos.CENTER);
        navigationContainer.getChildren().addAll(prevButton, nextButton);
        mushafLayout.setBottom(navigationContainer);

        // audio player container to hold the audio player components
        HBox audioPlayer = new HBox();
        audioPlayer.setPadding(new Insets(20));
        audioPlayer.setSpacing(20);
        audioPlayer.setAlignment(Pos.CENTER);
        audioPlayer.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        root.setBottom(audioPlayer);

        // load reciters & add it to the reciter combo box
        List<Reciter> reciters = Query.loadReciters();
        reciterComboBox = new ComboBox<>();
        reciterComboBox.getItems().addAll(reciters);
        reciterComboBox.getSelectionModel().select(6); // default reciter Mishari Rashid
        reciterComboBox.setPrefWidth(250);

        // add chapters to the surah combo box
        surahComboBox = new ComboBox<>();
        surahComboBox.getItems().addAll(chapters);
        surahComboBox.setPrefWidth(130);

        // time labels, play & pause button & muteButton button
        currentTimeLabel = new Label("00:00:00");
        totalTimeLabel = new Label("00:00:00");
        playPauseButton = new ToggleButton(null, new FontIcon(Material.PLAY_ARROW));
        muteButton = new ToggleButton(null, new FontIcon(Material.VOLUME_UP));
        muteButton.getStyleClass().add(Styles.BUTTON_CIRCLE);

        // time slider to seek the audio
        timeSlider = new Slider();
        timeSlider.setSkin(new ProgressSliderSkin(timeSlider));
        timeSlider.getStyleClass().add(Styles.SMALL);
        timeSlider.setMinWidth(300);

        // volume slider to control the volume
        volumeSlider = new Slider(0, 1, 1);
        volumeSlider.getStyleClass().add(Styles.SMALL);
        volumeSlider.setSkin(new ProgressSliderSkin(volumeSlider));
        volumeSlider.setMinWidth(100);

        // volume slider listener to change the mute button icon
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

        // reciter & surah comboBox listeners
        reciterComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Media newMedia = Query.loadMedia(newValue.getId(), surahComboBox.getSelectionModel().getSelectedItem().getId());
                setupMediaPlayer(newMedia);
            }
        });

        surahComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Media newMedia = Query.loadMedia(reciterComboBox.getSelectionModel().getSelectedItem().getId(), newValue.getId());
                setupMediaPlayer(newMedia);
            }
        });

        // download manager button
        Button downloadManager = new Button( "ادارة التحميلات", new FontIcon(Material.CLOUD_DOWNLOAD));
        downloadManager.setOnAction(e -> DownloadAudio.display());

        // set the default surah & reciter
        surahComboBox.getSelectionModel().selectFirst();

        // add components to the audio player
        audioPlayer.getChildren().addAll(reciterComboBox, surahComboBox, playPauseButton, currentTimeLabel, timeSlider, totalTimeLabel, downloadManager, muteButton, volumeSlider);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles/styles.css").toExternalForm());

        scene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent e) -> {
            if (e.getCode() == KeyCode.LEFT) setCurrentPage(pageNumber.get() + 1);
            else if (e.getCode() == KeyCode.RIGHT) setCurrentPage(pageNumber.get() - 1);
        });

        primaryStage.setTitle("JQuran");
        primaryStage.setScene(scene);

        // set the stage size to 90% of the screen size
        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();

        primaryStage.setWidth(screenWidth * PERCENTAGE);
        primaryStage.setHeight(screenHeight * PERCENTAGE);

        primaryStage.show();
    }

    private void setupMediaPlayer(Media newMedia) {
        // check if the media is not found
        if(newMedia != null) {
            if(mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.volumeProperty().unbind();
                mediaPlayer.dispose();
            }
            // set the current page to the first page of the surah, create a new media player
            setCurrentPage(surahComboBox.getSelectionModel().getSelectedItem().getPages().getFirst());
            mediaPlayer = new MediaPlayer(newMedia);

            // set the total time label to the media total duration, current time to 00:00:00, set the time slider max value to the total duration of the media, set the play & pause button to pause
            mediaPlayer.setOnReady(() -> {
                currentTimeLabel.setText("00:00:00");
                totalTimeLabel.setText(String.format("%02d:%02d:%02d", (int)mediaPlayer.getTotalDuration().toHours(), (int)mediaPlayer.getTotalDuration().toMinutes() % 60, (int)mediaPlayer.getTotalDuration().toSeconds() % 60));
                timeSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
                playPauseButton.setSelected(false);
            });

            // set the play & pause button to pause when the media is finished and don't dispose the media player to be able to play the media again
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.stop();
                playPauseButton.setSelected(false);
            });

            // play & pause button listener
            playPauseButton.selectedProperty().addListener((observableVal, oldVal, newVal) -> {
                if (newVal) {
                    playPauseButton.setGraphic(new FontIcon(Material.PAUSE));
                    mediaPlayer.play();
                } else {
                    playPauseButton.setGraphic(new FontIcon(Material.PLAY_ARROW));
                    mediaPlayer.pause();
                }
            });

            // seek to the new time when the user drag the time slider or click on the time slider
            AtomicBoolean isSeeking = new AtomicBoolean(false);

            timeSlider.setOnMousePressed(e -> isSeeking.set(true));

            // update the current time label when the user drag the time slider
            timeSlider.setOnMouseDragged(e -> currentTimeLabel.setText(String.format("%02d:%02d:%02d", (int)timeSlider.getValue() / 3600, (int)timeSlider.getValue() / 60 % 60, (int)timeSlider.getValue() % 60)));

            // seek to the new time when the user release the time slider (after dragging or clicking on the time slider)
            timeSlider.setOnMouseReleased(e -> {
                if(isSeeking.get()) {
                    mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
                    if(playPauseButton.isSelected()) mediaPlayer.play();
                    currentTimeLabel.setText(String.format("%02d:%02d:%02d", (int)timeSlider.getValue() / 3600, (int)timeSlider.getValue() / 60 % 60, (int)timeSlider.getValue() % 60));
                    isSeeking.set(false);
                }
            });

            // update the current time label & time slider value when the media is playing
            mediaPlayer.currentTimeProperty().addListener((observableVal, oldVal, newVal) -> {
                if (!isSeeking.get()) {
                    timeSlider.setValue(newVal.toSeconds());
                    currentTimeLabel.setText(String.format("%02d:%02d:%02d", (int) newVal.toHours(), (int) newVal.toMinutes() % 60, (int) newVal.toSeconds() % 60));
                }
            });

            // bind the volume slider value to the media player volume
            mediaPlayer.volumeProperty().bind(volumeSlider.valueProperty());

            // mute button listener to mute the media player when the user click on the mute button
            muteButton.selectedProperty().addListener((observableVal, oldVal, newVal) -> {
                if (newVal) {
                    muteButton.setGraphic(new FontIcon(Material.VOLUME_OFF));
                    mediaPlayer.setMute(true);
                } else {
                    muteButton.setGraphic(new FontIcon(Material.VOLUME_UP));
                    mediaPlayer.setMute(false);
                }
            });
        }
    }

    private void setCurrentPage(int newPageNumber) {
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

    private List<List<String>> getFormattedPage(int newPageNumber) {
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