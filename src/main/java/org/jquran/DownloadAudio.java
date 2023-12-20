
package org.jquran;

import java.io.IOException;
import org.jquran.jquran.CustomThing;
import org.jquran.jquran.Query;
import org.jquran.jquran.QuranChapters;
import org.jquran.jquran.QuranReciters;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class DownloadAudio {

    public static void display() throws IOException {
        Stage downloadStage = new Stage();

        QuranChapters quranChapters = Query.getChapters();
        QuranReciters reciters = Query.getReciters();

        ListView surahListView = new ListView();

        surahListView.setStyle("-fx-control-inner-background: #222222");

        ListView reciterListView = new ListView();

        reciterListView.setStyle("-fx-control-inner-background: #222222");

        for (int i = 0; i < reciters.getRecitations().size(); i++) {
            /// surah name
            String surahName = reciters.getRecitations().get(i).getReciter_name();
            /// surah info
            int surahNumber = reciters.getRecitations().get(i).getId();

            reciterListView.getItems().add(surahName);

        }
        ObservableList<CustomThing> chapterData = FXCollections.observableArrayList();
        for (int i = 0; i < quranChapters.getChapters().size(); i++) {
            /// surah name
            String surahName = quranChapters.getChapters().get(i).getName_arabic();
            /// surah info
            int surahNumber = quranChapters.getChapters().get(i).getId();
            int verseCount = quranChapters.getChapters().get(i).getVerses_count();
            String place = quranChapters.getChapters().get(i).getRevelation_place();
            if (place.equals("makkah")) {
                place = "مكيّة";
            } else {
                place = "مدنيّة";
            }
            int firstPage = quranChapters.getChapters().get(i).getPages().get(0);
            String surahInfo = "رقمها" + "_" + surahNumber + "_" + "آياتها" + "_" +
                    verseCount + "_" + place;

            chapterData.add(new CustomThing(surahName, surahInfo, firstPage));
            surahListView.getItems().add(chapterData.get(i).getName());

        }

        SplitPane sp = new SplitPane(reciterListView, surahListView);
        downloadStage.setTitle("اختر السورة والقارئ");
        downloadStage.setScene(new Scene(sp));
        downloadStage.show();
    }
}
