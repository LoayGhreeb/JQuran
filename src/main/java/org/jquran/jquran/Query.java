package org.jquran.jquran;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.text.Font;

import java.io.*;
import java.util.List;

public final class Query {
    private static List<Chapter> chapters;
    private static List<Reciter> reciters;
    static ObjectMapper objectMapper = new ObjectMapper();

    public static Page loadPage(int pageNum, int fontVersion) throws Exception {
        if (pageNum <= 0 || pageNum > 604 || fontVersion < 1 || fontVersion > 2)
            return null;
        return objectMapper.readValue(
                Query.class.getResourceAsStream("quran/v" + fontVersion + "/" + pageNum + ".json"), Page.class);
    }

    public static Font loadFont(int pageNum, int fontVersion, int fontSize) {
        if (pageNum <= 0 || pageNum > 604 || fontVersion < 1 || fontVersion > 2)
            return null;
        return Font.loadFont(Query.class.getResourceAsStream("fonts/v" + fontVersion + "/p" + pageNum + ".ttf"),
                fontSize);
    }

    public static Font getBSML(int fontVersion, int fontSize) {
        return Font.loadFont(Query.class.getResourceAsStream("fonts/v" + fontVersion + "/QCF_BSML.ttf"), fontSize);
    }

    public static List<Chapter> loadChapters() throws IOException {
        if (chapters == null)
            chapters = objectMapper.readValue(Query.class.getResourceAsStream("quran/chapters.json"),
                    new TypeReference<List<Chapter>>() {
                    });
        return chapters;
    }

    public static List<Reciter> loadReciters() throws IOException {
        if (reciters == null)
            reciters = objectMapper.readValue(Query.class.getResourceAsStream("quran/reciters.json"),
                    new TypeReference<List<Reciter>>() {
                    });
        return reciters;
    }
}