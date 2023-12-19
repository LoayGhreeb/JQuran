package org.jquran.jquran;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.text.Font;

import java.io.*;

public final class Query {
    static ObjectMapper objectMapper = new ObjectMapper();
    public static Page getPage(int pageNum, int fontVersion) throws Exception {
        if(pageNum <= 0 || pageNum > 604 || fontVersion < 1 || fontVersion > 2) return null;
        return objectMapper.readValue(new File("src/main/resources/org/assets/quran/v" + fontVersion + "/" + pageNum + ".json"), Page.class);
    }
    public static Font getFont(int pageNum, int fontVersion, int fontSize) throws Exception {
        if(pageNum <= 0 || pageNum > 604 || fontVersion < 1 || fontVersion > 2) return null;
        return Font.loadFont(new FileInputStream("src/main/resources/org/assets/fonts/v" + fontVersion + "/p"+ pageNum +".ttf"), fontSize);
    }
    public static QuranChapters getChapters() throws IOException {
        return objectMapper.readValue(new File("src/main/resources/org/assets/quran/chapters.json"), QuranChapters.class);
    }
}