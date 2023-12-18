package org.jquran.jquran;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.text.Font;

import java.io.*;

public final class Query {
    public static Page getPage(int pageNum, int fontVersion) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File("src/main/resources/org/assets/quran v" + fontVersion + "/" + pageNum + ".json");
        return objectMapper.readValue(jsonFile, Page.class);

//        if (pageNum > 0 && pageNum < 605) {
//            ObjectMapper objectMapper = new ObjectMapper();
//            File jsonFile = new File("src/main/resources/org/assets/quran/" + pageNum + ".json");
//            return objectMapper.readValue(jsonFile, Page.class);
//        }
//        return null;
    }
    public static Font getFont(int pageNum, int fontSize, int fontVersion) throws Exception {
        return Font.loadFont(new FileInputStream(new File("src/main/resources/org/assets/fonts/v" + fontVersion + "/p"+ pageNum +".ttf")), fontSize);
    }
}