package org.jquran.jquran;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.text.Font;

import java.io.*;

public final class Query {
    public static Page getPage(int pageNum) throws Exception {
        if (pageNum > 0 && pageNum < 605) {
            ObjectMapper objectMapper = new ObjectMapper();
            File jsonFile = new File("src/main/resources/org/assets/quran/" + pageNum + ".json");
            return objectMapper.readValue(jsonFile, Page.class);
        }
        return null;
    }
    public static Font getFont(int pageNum, int fontSize) throws Exception {
        String index = String.valueOf(pageNum);
        if(pageNum < 10) index = "00" + index;
        else if(pageNum < 100) index = "0" + index;
        return Font.loadFont(new FileInputStream(new File("src/main/resources/org/assets/fonts/QCFV2/QCF2"+ index +".ttf")), fontSize);
    }
}
