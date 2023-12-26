package org.jquran.jquran;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.text.Font;

import java.io.*;

public final class Query {
    static ObjectMapper objectMapper = new ObjectMapper();
    public static Page loadPage(int pageNum, int fontVersion) throws Exception {
        if (pageNum <= 0 || pageNum > 604 || fontVersion < 1 || fontVersion > 2) return null;
        return objectMapper.readValue(Query.class.getResourceAsStream("quran/v" + fontVersion + "/" + pageNum + ".json"), Page.class);
    }
    public static Font getFont(int pageNum, int fontVersion, int fontSize) {
        if (pageNum <= 0 || pageNum > 604 || fontVersion < 1 || fontVersion > 2) return null;
        return Font.loadFont(Query.class.getResourceAsStream("fonts/v" + fontVersion + "/p" + pageNum + ".ttf"), fontSize);
    }
    public static Font getBSML(int fontVersion, int fontSize) {
        return Font.loadFont(Query.class.getResourceAsStream("fonts/v" + fontVersion + "/QCF_BSML.ttf"), fontSize);
    }
    public static QuranChapters loadChapters() throws IOException {
        return objectMapper.readValue(Query.class.getResourceAsStream("quran/chapters.json"), QuranChapters.class);
    }
}