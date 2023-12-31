package org.jquran.jquran;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.Alert;
import javafx.scene.media.Media;
import javafx.scene.text.Font;
import java.sql.*;
import java.util.List;

public final class Query {
    private static List<Chapter> chapters;
    private static List<Reciter> reciters;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static Connection connection;
    private static Statement statement;

    public static Page loadPage(int pageNum, int fontVersion) {
        if (pageNum <= 0 || pageNum > 604 || fontVersion < 1 || fontVersion > 2) return null;
        try {
            return objectMapper.readValue(Query.class.getResourceAsStream("quran/v" + fontVersion + "/" + pageNum + ".json"), Page.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Font loadPageFont(int pageNum, int fontVersion, int fontSize) {
        if (pageNum <= 0 || pageNum > 604 || fontVersion < 1 || fontVersion > 2) return null;
        return Font.loadFont(Query.class.getResourceAsStream("fonts/v" + fontVersion + "/p" + pageNum + ".ttf"), fontSize);
    }

    public static Font loadSurahNameFont(int fontSize) {
        return Font.loadFont(Query.class.getResourceAsStream("fonts/QCF_BSML.ttf"), fontSize);
    }

    public static List<Chapter> loadChapters() {
        if (chapters == null) {
            try {
                chapters = objectMapper.readValue(Query.class.getResourceAsStream("quran/chapters.json"), new TypeReference<List<Chapter>>() {});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return chapters;
    }

    public static List<Reciter> loadReciters() {
        if (reciters == null) {
            try {
                reciters = objectMapper.readValue(Query.class.getResourceAsStream("quran/reciters.json"), new TypeReference<List<Reciter>>() {});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return reciters;
    }

    public static String loadSurahNameCode(int surahId){
        if (connection == null) {
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/org/jquran/jquran/fonts/glyphs.db");
                statement = connection.createStatement();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        String query = "SELECT qcf_v1 FROM surah_glyphs WHERE id = " + surahId + ";";
        try(ResultSet resultSet = statement.executeQuery(query)) {
            return resultSet.getString("qcf_v1");
        }catch (Exception e){
            e.printStackTrace();
        }
         return null;
    }

    public static Media loadMedia(int reciterId, int chapterId) {
        try {
            return new Media(Query.class.getResource("Quran_Audio/" + loadReciters().get(reciterId - 1).getTranslated_name().getName() + "/" + chapterId + ".mp3").toURI().toString());
        }catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Exception Dialog");
            alert.setHeaderText("No files found");
            alert.setContentText("you have to download the audio files first");
            alert.showAndWait();
        }
        return null;
    }
}