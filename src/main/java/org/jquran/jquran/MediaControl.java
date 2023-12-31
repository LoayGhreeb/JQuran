package org.jquran.jquran;

import java.util.ArrayList;

import atlantafx.base.controls.ProgressSliderSkin;
import atlantafx.base.theme.Styles;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material.Material;

public class MediaControl extends BorderPane {
    private final MediaPlayer mp;
    private ArrayList<MediaPlayer> mpl;
    private MediaView mediaView;
    private boolean stopRequested = false;
    private boolean atEndOfMedia = false;
    private Duration duration;
    private Slider timeSlider;
    private Label playTime;
    private Slider volumeSlider;
    private HBox mediaBar;
    private int i = 0;
    private final Pane mvPane;
    private Button playButton;
    private Label spacer;
    private Label timeLabel;
    private Label volumeLabel;

    public MediaControl(final ArrayList<MediaPlayer> mpl) {
        this.mpl = mpl;
        this.mp = mpl.get(i);
        mediaView = new MediaView(mp);
        mvPane = new Pane() {
        };
        mvPane.getChildren().add(mediaView);
        setCenter(mvPane);

        mediaBar = new HBox();
        mediaBar.setAlignment(Pos.CENTER);
        mediaBar.setPadding(new Insets(5, 10, 5, 10));
        BorderPane.setAlignment(mediaBar, Pos.CENTER);

        playButton = new Button(null, new FontIcon(Material.PLAY_ARROW));
        mediaBar.getChildren().add(playButton);
        setBottom(mediaBar);

        // Add spacer
        spacer = new Label("   ");
        mediaBar.getChildren().add(spacer);

        // Add Time label
        timeLabel = new Label("Time: ");
        mediaBar.getChildren().add(timeLabel);

        // Add time slider
        timeSlider = new Slider();
        timeSlider.getStyleClass().add(Styles.SMALL);
        timeSlider.setSkin(new ProgressSliderSkin(timeSlider));
        HBox.setHgrow(timeSlider, Priority.ALWAYS);
        timeSlider.setMinWidth(50);
        timeSlider.setMaxWidth(Double.MAX_VALUE);
        mediaBar.getChildren().add(timeSlider);

        // Add Play label
        playTime = new Label();
        playTime.setPrefWidth(130);
        playTime.setMinWidth(50);
        mediaBar.getChildren().add(playTime);

        // Add the volume label
        volumeLabel = new Label("Vol: ");
        mediaBar.getChildren().add(volumeLabel);

        // Add Volume slider
        volumeSlider = new Slider();
        volumeSlider.getStyleClass().add(Styles.SMALL);
        volumeSlider.setSkin(new ProgressSliderSkin(volumeSlider));
        volumeSlider.setPrefWidth(70);
        volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
        volumeSlider.setMinWidth(30);

        mediaBar.getChildren().add(volumeSlider);

        playButton.setOnAction(e-> {

                Status status = mp.getStatus();

                if (status == Status.UNKNOWN || status == Status.HALTED) {
                    // don't do anything in these states
                    return;
                }

                if (status == Status.PAUSED
                        || status == Status.READY
                        || status == Status.STOPPED) {
                    // rewind the movie if we're sitting at the end
                    if (atEndOfMedia) {
                        mp.seek(mp.getStartTime());
                        atEndOfMedia = false;
                    }
                    mp.play();
                } else {
                    mp.pause();
            }
        });

        mp.currentTimeProperty().addListener(ov -> updateValues(mp));

        mp.setOnPlaying(() -> {
            if (stopRequested) {
                mp.pause();
                stopRequested = false;
            } else {
                playButton.setGraphic(new FontIcon(Material.PAUSE));
            }
        });

        mp.setOnPaused(() -> {
            System.out.println("onPaused");
            playButton.setGraphic(new FontIcon(Material.PLAY_ARROW));
        });

        mp.setOnReady(() -> {
            duration = mp.getMedia().getDuration();
            updateValues(mp);
        });

        mp.setOnEndOfMedia(() -> {
            if (i == mpl.size() - 1) {
                playButton.setGraphic(new FontIcon(Material.PLAY_ARROW));
                stopRequested = true;
                atEndOfMedia = true;
            } else {
                mpl.removeFirst();
                setEnd(mpl);
            }
        });

        timeSlider.valueProperty().addListener(ov -> {
            if (timeSlider.isValueChanging()) {
                // multiply duration by percentage calculated by slider position
                mp.seek(duration.multiply(timeSlider.getValue() / 100.0));
            }
        });

        volumeSlider.valueProperty().addListener(ov -> {
            if (volumeSlider.isValueChanging()) {
                mp.setVolume(volumeSlider.getValue() / 100.0);
            }
        });
    }

    protected void updateValues(MediaPlayer mp) {
        if (playTime != null && timeSlider != null && volumeSlider != null) {
            Platform.runLater(() -> {
                Duration currentTime = mp.getCurrentTime();
                playTime.setText(formatTime(currentTime, duration));
                timeSlider.setDisable(duration.isUnknown());
                if (!timeSlider.isDisabled()
                        && duration.greaterThan(Duration.ZERO)
                        && !timeSlider.isValueChanging()) {
                    timeSlider.setValue(currentTime.divide(duration).toMillis()
                            * 100.0);
                }
                if (!volumeSlider.isValueChanging()) {
                    volumeSlider.setValue((int) Math.round(mp.getVolume()
                            * 100));
                }
            });
        }
    }

    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 -
                    durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }

    private void setEnd(ArrayList<MediaPlayer> mpl) {

        mediaView = new MediaView(mpl.getFirst());
        mvPane.getChildren().setAll(mediaView);
        setCenter(mvPane);

        mediaBar = new HBox();
        mediaBar.setAlignment(Pos.CENTER);
        mediaBar.setPadding(new Insets(5, 10, 5, 10));
        BorderPane.setAlignment(mediaBar, Pos.CENTER);

        playButton = new Button(null, new FontIcon(Material.PAUSE));
        mediaBar.getChildren().add(playButton);
        setBottom(mediaBar);

        // Add spacer
        spacer = new Label("   ");
        mediaBar.getChildren().add(spacer);

        // Add Time label
        timeLabel = new Label("Time: ");
        mediaBar.getChildren().add(timeLabel);

        // Add time slider
        timeSlider = new Slider();
        timeSlider.getStyleClass().add(Styles.SMALL);
        timeSlider.setSkin(new ProgressSliderSkin(timeSlider));
        HBox.setHgrow(timeSlider, Priority.ALWAYS);
        timeSlider.setMinWidth(50);
        timeSlider.setMaxWidth(Double.MAX_VALUE);
        mediaBar.getChildren().add(timeSlider);

        // Add Play label
        playTime = new Label();
        playTime.setPrefWidth(130);
        playTime.setMinWidth(50);
        mediaBar.getChildren().add(playTime);

        // Add the volume label
        volumeLabel = new Label("Vol: ");
        mediaBar.getChildren().add(volumeLabel);

        // Add Volume slider
        volumeSlider = new Slider();
        volumeSlider.getStyleClass().add(Styles.SMALL);
        volumeSlider.setSkin(new ProgressSliderSkin(volumeSlider));
        volumeSlider.setPrefWidth(70);
        volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
        volumeSlider.setMinWidth(30);

        mediaBar.getChildren().add(volumeSlider);

        playButton.setOnAction(e -> {
            Status status = mpl.getFirst().getStatus();

            if (status == Status.UNKNOWN || status == Status.HALTED) {
                // don't do anything in these states
                return;
            }

            if (status == Status.PAUSED
                    || status == Status.READY
                    || status == Status.STOPPED) {
                // rewind the movie if we're sitting at the end
                if (atEndOfMedia) {
                    mpl.getFirst().seek(mpl.getFirst().getStartTime());
                    atEndOfMedia = false;
                }
                mpl.getFirst().play();
                playButton.setGraphic(new FontIcon(Material.PAUSE));
            } else {
                mpl.getFirst().pause();
            }
        });

        duration = mpl.getFirst().getMedia().getDuration();
        updateValues(mpl.getFirst());
        mpl.getFirst().play();

        mpl.getFirst().currentTimeProperty().addListener(ov -> updateValues(mpl.getFirst()));

        mpl.getFirst().setOnPlaying(() -> {
            if (stopRequested) {
                mpl.getFirst().pause();
                stopRequested = false;
            } else {

            }
        });

        mpl.getFirst().setOnPaused(() -> {
            playButton.setGraphic(new FontIcon(Material.PLAY_ARROW));
            System.out.println("onPaused");

        });

        mpl.getFirst().setOnReady(() -> {

        });
        mpl.getFirst().setOnEndOfMedia(() -> {
            if (!mpl.isEmpty()) {
                mpl.removeFirst();
                setEnd(mpl);
            }
        });

        timeSlider.valueProperty().addListener(ov -> {
            if (timeSlider.isValueChanging()) {
                // multiply duration by percentage calculated by slider position
                mpl.getFirst().seek(duration.multiply(timeSlider.getValue() / 100.0));
            }
        });

        volumeSlider.valueProperty().addListener(ov -> {
            if (volumeSlider.isValueChanging()) {
                mpl.getFirst().setVolume(volumeSlider.getValue() / 100.0);
            }
        });
    }
}
