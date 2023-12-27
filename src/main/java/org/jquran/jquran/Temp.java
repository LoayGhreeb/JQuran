package org.jquran.jquran;

import javafx.application.Application;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.controlsfx.glyphfont.Glyph;

import java.util.*;

public class Temp extends Application {
    int pgNum = 1;
    int fontVersion = 1;
    TextFlow textFlow = new TextFlow();

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane borderPane = new BorderPane();
        textFlow.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        textFlow.setTextAlignment(TextAlignment.CENTER);

        borderPane.setPrefWidth(550);
        borderPane.setPrefHeight(1000);
//        borderPane.setCenter(textFlow);
        ScrollPane pane = new ScrollPane();
        Scene scene = new Scene(borderPane);
//        pane.setContent(textFlow);

//
//        for(int i = 403; i <= 517; i++){
//            Glyph glyph = new Glyph();
//            glyph.setText("loay");
//            Font f = Query.getBSML(fontVersion, 33);
//            f.
//            glyph.setFont(Query.getBSML(fontVersion, 33));
//            Text t = new Text(glyph.getText());
//            t.set
////            t.setFont(Query.getBSML(fontVersion, 33));
//            textFlow.getChildren().add(t);
//        }
//        setPage(pgNum);
//        scene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent e) -> {
//            if (e.getCode() == KeyCode.LEFT) {
//                try {
//                    setPage(pgNum + 1);
//                } catch (Exception ex) {
//                    throw new RuntimeException(ex);
//                }
//            } else if (e.getCode() == KeyCode.RIGHT) {
//                try {
//                    setPage(pgNum - 1);
//                } catch (Exception ex) {
//                    throw new RuntimeException(ex);
//                }
//            }
//            e.consume();
//        });
        Text t = new Text("ﮍ");
        t.setFont(Query.getBSML(fontVersion, 33));
        borderPane.setCenter(t);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void setPage(int pageNum) throws Exception {
        List<List<String>> lines = formattedPage2(pageNum);

        Font f = Query.loadFont(pageNum, fontVersion, 33);
        textFlow.getChildren().clear();
        for (List<String> line : lines) {
            for (String s : line) {
                Text text1 = new Text(s);
                if(s.equals("002"))
                    text1.setFont(Query.getBSML(fontVersion, 33));
                else text1.setFont(f);
                textFlow.getChildren().add(text1);
            }
            textFlow.getChildren().add(new Text("\n"));
        }
        pgNum = pageNum;
    }
//    public List<List<String>> formattedPage(int pageNumber) throws Exception {
//
//        List<List<String>> lines = new ArrayList<>();
//        int linesCount;
//        if (pageNumber < 3) {
//            linesCount = 8;
//        } else {
//            linesCount = 15;
//        }
//
//        for (int i = 0; i < 15; i++) {
//            lines.add(new ArrayList<>());
//        }
//
//        int innerCounter = 0;
//        int curLineNum = 0;
//        int aftLineNum = 0;
//        boolean lineChange = false;
//        String chapterCode;
//        Page data = Query.loadPage(pageNumber, 2);
//        for (int i = 0; i < data.getVerses().size(); i++) {
//            Verse verses = data.getVerses().get(i);
//            List<Word> verseWords = verses.getWords();
//            String[] verseKey = verseWords.getFirst().getVerse_key().split(":");
//            String verseChapter = verseKey[0];
//            String currentVerse = verseKey[1];
//
//            chapterCode = String.format("%03d", Integer.parseInt(verseChapter));
//
//            if (currentVerse.equals("1")) {
//                lines.get(curLineNum).set(0, chapterCode);
//                int lineNum = verseWords.getFirst().getLine_number();
//
//                if (lineNum != 2 || pageNumber == 1) {
//                    Map<String, Object> chapterStart1 = new HashMap<>();
//                    chapterStart1.put("id", 90000 + Integer.parseInt(chapterCode));
//                    chapterStart1.put("line_number", curLineNum + 1);
//                    chapterStart1.put("chapterCode", chapterCode);
//                    chapterStart1.put("isNewChapter", true);
//                    lines.get(curLineNum).add(chapterStart1);
//
//                    Map<String, Object> bismillah = new HashMap<>();
//                    bismillah.put("id", 93000 + Integer.parseInt(chapterCode));
//                    bismillah.put("line_number", curLineNum + 2);
//                    bismillah.put("chapterCode", chapterCode);
//                    bismillah.put("isNewChapter", true);
//                    bismillah.put("isBismillah", true);
//                    bismillah.put("text", "﷽");
//                    if (curLineNum + 1 < lines.size()) {
//                        lines.get(curLineNum + 1).add(bismillah);
//                    }
//                } else {
//                    Map<String, Object> bismillah = new HashMap<>();
//                    bismillah.put("id", 93000 + Integer.parseInt(chapterCode));
//                    bismillah.put("line_number", curLineNum + 1);
//                    bismillah.put("chapterCode", chapterCode);
//                    bismillah.put("isNewChapter", true);
//                    bismillah.put("isBismillah", true);
//                    bismillah.put("text", "﷽");
//                    lines.get(curLineNum).add(bismillah);
//                }
//            }
//
//            if (pageNumber > 2 && curLineNum == 0) {
//                Map<String, Object> nextChapter = new HashMap<>();
//                nextChapter.put("id", 90000 + (Integer.parseInt(chapterCode) + 1));
//                nextChapter.put("line_number", 15);
//                nextChapter.put("chapterCode", String.valueOf(Integer.parseInt(chapterCode) + 1));
//                nextChapter.put("isNewChapter", true);
//                lines.get(14).add(nextChapter);
//            }
//
//            for (int j = 0; j < verseWords.size(); j++) {
//
//                Word word = verseWords.get(j);
//
//                curLineNum = word.getLine_number();
//
//                Map<String, Object> customWord = new HashMap<>();
//                customWord.put("text", word.getCode(2).replace(" ", ""));
//                customWord.put("id", word.getId());
//                customWord.put("line_number", word.getLine_number());
//                customWord.put("audio_url", word.getAudio_url());
//                customWord.put("char_type_name", word.getChar_type_name());
//                customWord.put("transliteration", word.getTransliteration().getText());
//
//                if (j < verseWords.size() - 1) {
//                    aftLineNum = verseWords.get(j + 1).getLine_number();
//                } else if (i < data.getVerses().size() - 1) {
//                    aftLineNum = data.getVerses().get(i + 1).getWords().getFirst().getLine_number();
//                }
//
//                lineChange = curLineNum != aftLineNum;
//
//                if (!lineChange) {
//                    lines.get(curLineNum - 1).add(customWord);
//                    innerCounter++;
//                } else {
//                    lines.get(curLineNum - 1).add(customWord);
//                    innerCounter = 0;
//                }
//            }
//
//        }
//
//        meta.put("pageNumber", pageNumber);
//        meta.put("hizbNumber", data.getVerses().getFirst().getHizb_number());
//        meta.put("juzNumber", data.getVerses().getFirst().getJuz_number());
//        meta.put("rubNumber", data.getVerses().getFirst().getRub_el_hizb_number());
//
//        List<List<Word>> result = new HashMap<>();
//        result.put("lines", lines);
//        result.put("meta", meta);
//
//        return result;
//    }

    public List<List<String>> formattedPage2(int pageNumber) throws Exception {
        List<List<String>> lines = new ArrayList<>();
        int linesCount;
        if (pageNumber < 3) {
            linesCount = 8;
        } else {
            linesCount = 15;
        }

        for (int i = 0; i < 15; i++)
            lines.add(new ArrayList<>());


        int innerCounter = 0;
        int curLineNum = 0;
        int aftLineNum = 0;
        boolean lineChange = false;
        String chapterCode;
        Page data = Query.loadPage(pageNumber, fontVersion);
        for (int i = 0; i < data.getVerses().size(); i++) {
            Verse verses = data.getVerses().get(i);
            List<Word> verseWords = verses.getWords();
            String[] verseKey = verseWords.getFirst().getVerse_key().split(":");
            String verseChapter = verseKey[0];
            String currentVerse = verseKey[1];

            chapterCode = String.format("%03d", Integer.parseInt(verseChapter));

            if (currentVerse.equals("1")) {
                lines.get(curLineNum).add(chapterCode);
            }

            for(int j = 0; j < verseWords.size(); j++){
                curLineNum = verseWords.get(j).getLine_number();
//                aftLineNum = verseWords.get(j + 1).getLine_number();
                if(j + 1 == verseWords.size()){
                    if(i + 1 < data.getVerses().size())
                        aftLineNum = data.getVerses().get(i + 1).getWords().getFirst().getLine_number();
                    else
                        aftLineNum = -1;
                }
                lineChange = curLineNum != aftLineNum;
                lines.get(curLineNum - 1).add(verseWords.get(j).getCode(fontVersion));
                if(!lineChange){
                    innerCounter++;
                }
                else {
                    innerCounter = 0;
                }
            }
        }
        return lines;
    }
}
