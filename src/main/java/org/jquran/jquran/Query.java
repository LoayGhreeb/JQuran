package org.jquran.jquran;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.text.Font;

import java.io.*;
import java.sql.*;
import java.util.List;

public final class Query {
    private static List<Chapter> chapters;
    private static List<Reciter> reciters;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static Connection connection;
    private static Statement statement;

    public static Page loadPage(int pageNum, int fontVersion) throws Exception {
        if (pageNum <= 0 || pageNum > 604 || fontVersion < 1 || fontVersion > 2)
            return null;
        return objectMapper.readValue(
                Query.class.getResourceAsStream("quran/v" + fontVersion + "/" + pageNum + ".json"), Page.class);
    }

    public static Font loadPageFont(int pageNum, int fontVersion, int fontSize) {
        if (pageNum <= 0 || pageNum > 604 || fontVersion < 1 || fontVersion > 2)
            return null;
        return Font.loadFont(Query.class.getResourceAsStream("fonts/v" + fontVersion + "/p" + pageNum + ".ttf"),
                fontSize);
    }

    public static Font loadSurahNameFont(int fontSize) {
        return Font.loadFont(Query.class.getResourceAsStream("fonts/QCF_BSML.ttf"), fontSize);
    }

    public static List<Chapter> loadChapters() {
        if (chapters == null) {
            try {
                chapters = objectMapper.readValue(Query.class.getResourceAsStream("quran/chapters.json"),
                        new TypeReference<List<Chapter>>() {
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return chapters;
    }

    public static List<Reciter> loadReciters() {
        if (reciters == null) {
            try {
                reciters = objectMapper.readValue(Query.class.getResourceAsStream("quran/reciters.json"),
                        new TypeReference<List<Reciter>>() {
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return reciters;
    }

    public static String loadSurahNameCode(int surahId) throws SQLException {
        if (connection == null) {
            connection = DriverManager
                    .getConnection("jdbc:sqlite:src/main/resources/org/jquran/jquran/fonts/glyphs.db");
            statement = connection.createStatement();
        }
        String query = "SELECT qcf_v1 FROM surah_glyphs WHERE id = " + surahId + ";";
        ResultSet resultSet = statement.executeQuery(query);
        return resultSet.getString("qcf_v1");
    }
}