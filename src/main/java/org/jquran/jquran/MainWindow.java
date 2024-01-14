package org.jquran.jquran;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.ProgressSliderSkin;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.*;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.*;
import javafx.scene.Cursor;
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
    private static Page currentPage;
    private static Chapter selectedChapter;
    private static List<Chapter> chaptersList;
    private static ListView<Chapter> chaptersListView;
    private static int pageNumber = 1;

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
            setCurrentPage(pageNumber);
            v2.setDisable(false);
            v1.setDisable(true);
        });

        v2.selectedProperty().addListener((observable, oldValue, newValue) -> {
            fontVersion = 2;
            setCurrentPage(pageNumber);
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

        // search field to search for chaptersList
        CustomTextField searchField = new CustomTextField();
        searchField.setPromptText("اسم السورة");
        searchField.setLeft(new FontIcon(Material.SEARCH));
        chaptersPane.getChildren().add(searchField);

        // load chaptersList name & add it to the listview
        chaptersList = Query.loadChapters();

        chaptersListView = new ListView<>();
        chaptersListView.getStyleClass().addAll(Tweaks.EDGE_TO_EDGE);
        chaptersListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        chaptersListView.getStyleClass().add("listView");
        chaptersListView.prefHeightProperty().bind(chaptersPane.heightProperty());

        chaptersListView.getItems().addAll(chaptersList);
        chaptersPane.getChildren().add(chaptersListView);
        chaptersListView.getSelectionModel().select(pageNumber - 1);

        // set the current page to the first page of the selected surah
        chaptersListView.setOnMouseClicked(e -> setCurrentPage(chaptersListView.getSelectionModel().getSelectedItem().getPages().getFirst()));

        // search field listener
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Save selection before filtering
            Chapter currentSelection = chaptersListView.getSelectionModel().getSelectedItem();
            if (currentSelection != null) selectedChapter = currentSelection;

            // Filter the chaptersList
            chaptersListView.getItems().setAll(chaptersList.stream().filter(chapter -> chapter.getName_arabic().contains(newValue) || String.valueOf(chapter.getId()).contains(newValue)).collect(Collectors.toList()));

            // Restore selection if still in filtered list
            if (selectedChapter != null && chaptersListView.getItems().contains(selectedChapter))
                chaptersListView.getSelectionModel().select(selectedChapter);
            else
                chaptersListView.getSelectionModel().clearSelection();

            //show button to clear the search field if it's not empty and hide it if it's empty, and clear the search field when click on it.
            if (newValue.isEmpty()) {
                searchField.setRight(null);
                chaptersListView.getSelectionModel().select(selectedChapter);
            }
            else {
                Button clearButton = new Button(null, new FontIcon(Material.CLOSE));
                clearButton.cursorProperty().set(Cursor.HAND);
                clearButton.getStyleClass().addAll(Styles.FLAT, Styles.BUTTON_CIRCLE, Styles.SMALL, Styles.DANGER);
                searchField.setRight(clearButton);
                clearButton.setOnAction(e -> searchField.clear());
            }
        });

        // Set Mushaf layout
        pageTextFlow = new TextFlow();
        pageTextFlow.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        pageTextFlow.setTextAlignment(TextAlignment.CENTER);
        pageTextFlow.setMinWidth(500);
        ScrollPane scrollPane = new ScrollPane(pageTextFlow);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        // add the scroll pane to the center of the root
        root.setCenter(scrollPane);

        // audio player container to hold the audio player components
        HBox audioPlayer = new HBox();
        audioPlayer.setPadding(new Insets(20));
        audioPlayer.setSpacing(20);
        audioPlayer.setAlignment(Pos.CENTER);
        audioPlayer.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        root.setBottom(new VBox(new Separator(Orientation.HORIZONTAL), audioPlayer));

        // load reciters & add it to the reciter combo box
        List<Reciter> reciters = Query.loadReciters();
        reciterComboBox = new ComboBox<>();
        reciterComboBox.getStyleClass().add("comboBox");
        reciterComboBox.getItems().addAll(reciters);
        reciterComboBox.getSelectionModel().select(6); // default reciter Mishari Rashid
        reciterComboBox.setPrefWidth(250);

        // add chapters to the surah combo box
        surahComboBox = new ComboBox<>();
        surahComboBox.getStyleClass().add("comboBox");
        surahComboBox.getItems().addAll(chaptersList);
        surahComboBox.setPrefWidth(130);

        // time labels, play & pause button & muteButton button
        currentTimeLabel = new Label("00:00:00");
        totalTimeLabel = new Label("00:00:00");
        playPauseButton = new ToggleButton(null, new FontIcon(Material.PLAY_ARROW));
        playPauseButton.cursorProperty().set(Cursor.HAND);
        muteButton = new ToggleButton(null, new FontIcon(Material.VOLUME_UP));
        muteButton.getStyleClass().add(Styles.BUTTON_CIRCLE);
        muteButton.cursorProperty().set(Cursor.HAND);

        // time slider to seek the audio
        timeSlider = new Slider();
        timeSlider.setSkin(new ProgressSliderSkin(timeSlider));
        timeSlider.getStyleClass().add(Styles.SMALL);
        timeSlider.cursorProperty().set(Cursor.HAND);
        timeSlider.setMinWidth(300);

        // volume slider to control the volume
        volumeSlider = new Slider(0, 1, 1);
        volumeSlider.getStyleClass().add(Styles.SMALL);
        volumeSlider.setSkin(new ProgressSliderSkin(volumeSlider));
        volumeSlider.cursorProperty().set(Cursor.HAND);
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

        // reciter & surah comboBox listeners to stop the current media player
        reciterComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setupMediaPlayer(null);
        });

        surahComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setupMediaPlayer(null);
        });

        // play & pause button listener
        playPauseButton.setOnAction(e-> {
            // if the media player is null, load the media and play it
            if (mediaPlayer == null) {
                Media newMedia = Query.loadMedia(reciterComboBox.getSelectionModel().getSelectedItem().getId(), surahComboBox.getSelectionModel().getSelectedItem().getId());
                if(setupMediaPlayer(newMedia)) {
                    setCurrentPage(surahComboBox.getSelectionModel().getSelectedItem().getPages().getFirst());
                    chaptersListView.getSelectionModel().clearSelection();
                    chaptersListView.getSelectionModel().select(surahComboBox.getSelectionModel().getSelectedItem().getId() - 1);
                }
                else {
                    showAlert();
                    playPauseButton.setSelected(false);
                }
            }
            // if the media player is not null, play or pause the media
            else {
                if (playPauseButton.isSelected()) {
                    playPauseButton.setGraphic(new FontIcon(Material.PAUSE));
                    mediaPlayer.play();
                } else {
                    playPauseButton.setGraphic(new FontIcon(Material.PLAY_ARROW));
                    mediaPlayer.pause();
                }
            }
        });

        // download manager button
        Button downloadManager = new Button( "ادارة التحميلات", new FontIcon(Material.CLOUD_DOWNLOAD));
        downloadManager.cursorProperty().set(Cursor.HAND);
        downloadManager.setOnAction(e -> DownloadAudio.display());

        // set the default surah & reciter
        surahComboBox.getSelectionModel().selectFirst();

        // add components to the audio player
        audioPlayer.getChildren().addAll(reciterComboBox, surahComboBox, playPauseButton, currentTimeLabel, timeSlider, totalTimeLabel, downloadManager, muteButton, volumeSlider);

        // set the current page to the default page at the start
        setCurrentPage(pageNumber);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles/styles.css").toExternalForm());

        // add keyboard shortcuts
        scene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent e) -> {
            // play & pause the media when the user press the space bar
            if (e.getCode() == KeyCode.SPACE) {
                playPauseButton.fire();
            }
            // change the current page to the next or previous page when the user press the left or right arrow keys
            if(e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT) {
                // set the current page to the first page of the selected surah
                if (e.getCode() == KeyCode.LEFT) setCurrentPage(pageNumber + 1);
                else setCurrentPage(pageNumber - 1);

                // update selected chapter in the chapters list view
                chaptersListView.getSelectionModel().clearSelection();
                chaptersListView.getSelectionModel().select(chaptersList.get(currentPage.getVerses().getFirst().getChapter_id() - 1));
            }
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

    // show an alert if the media is not found
    private static void showAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        alert.setTitle("خطأ");
        alert.setHeaderText("لم يتم تحميل الملف الصوتي");
        alert.setContentText("الرجاء التأكد من تحميل الملف الصوتي");
        alert.showAndWait();
    }

    // setup the media player with the new media, return true if the media is found and false if not
    private boolean setupMediaPlayer(Media newMedia) {
        // stop the current media player, reset the time labels & time slider, set the play & pause button to pause
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.volumeProperty().unbind();
            mediaPlayer.dispose();
            mediaPlayer = null;
            currentTimeLabel.setText("00:00:00");
            totalTimeLabel.setText("00:00:00");
            timeSlider.setValue(0);
            timeSlider.setMax(0);
            playPauseButton.setSelected(false);
            playPauseButton.setGraphic(new FontIcon(Material.PLAY_ARROW));
        }
        // check if the media is not found
        if (newMedia == null) return false;
        // if the media is found, setup the media player
        else {
            mediaPlayer = new MediaPlayer(newMedia);
            // set the total time label to the media total duration, current time to 00:00:00, set the time slider max value to the total duration of the media, set the play & pause button to pause
            mediaPlayer.setOnReady(() -> {
                currentTimeLabel.setText("00:00:00");
                totalTimeLabel.setText(String.format("%02d:%02d:%02d", (int)mediaPlayer.getTotalDuration().toHours(), (int)mediaPlayer.getTotalDuration().toMinutes() % 60, (int)mediaPlayer.getTotalDuration().toSeconds() % 60));
                timeSlider.setValue(0);
                timeSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
                playPauseButton.setGraphic(new FontIcon(Material.PAUSE));
                mediaPlayer.play();
            });

            // set the play & pause button to pause when the media is finished and don't dispose the media player to be able to play the media again
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.stop();
                playPauseButton.setSelected(false);
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
        return true;
    }

    // set the current page to the new page number
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
            // handle if the last line of the page at line 14 -> add box with the next surah name
            else if(currentVerse == chaptersList.get(verseChapter - 1).getVerses_count() && verseWords.getLast().getLine_number() == 14) {
                lines.get(14).add(chapterCode + Query.loadSurahNameCode(verseChapter + 1));
            }

            // Add words to the curLine
            for (Word verseWord : verseWords) {
                curLineNum = verseWord.getLine_number();
                lines.get(curLineNum - 1).add(verseWord.getCode(fontVersion));
            }
        }
        currentPage = page;
        pageNumber = newPageNumber;
        return lines;
    }
}