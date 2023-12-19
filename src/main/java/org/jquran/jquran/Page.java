package org.jquran.jquran;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties (ignoreUnknown = true)
public class Page {
    private List<Verse> verses;

    public List<Verse> getVerses() {
        return verses;
    }


    public ArrayList<Text> getLines(int fontVersion, QuranChapters quranChapters, int fontSize, int newPageNum) throws Exception {
        ArrayList<Text> fullPage = new ArrayList<>();
        List<StringBuilder> lines = new ArrayList<>();
        for(int i = 0; i < 16; i++) {
            lines.add(new StringBuilder());
            if(i == 0) {
                fullPage.add(new Text(""));
            }
            else{
                fullPage.add(new Text("tempS"));
            }
        }

        for (Verse verse : verses) {
            if(verse.getVerse_number() == 1){
                int surahId =  quranChapters.getChapters().get(verse.getChapter_id()-1).getId();
                String surahName = getSurahArabic(surahId);
                int firstVerseLine = verse.getWords().get(0).getLine_number();
                if(quranChapters.getChapters().get(verse.getChapter_id()-1).isBismillah_pre()){

                    fullPage.get(firstVerseLine - 2).setText(surahName+"\n");
                    fullPage.get(firstVerseLine - 2).setFont(Query.getsurahNames(fontSize));
                    String Basmala = "3 2 1";
                    fullPage.get(firstVerseLine - 1).setText(Basmala+"\n");
                    fullPage.get(firstVerseLine - 1).setFont(Query.getBSMLV1(fontSize));

                }else{
                    fullPage.get(firstVerseLine - 1).setText(surahName+"\n");
                    fullPage.get(firstVerseLine - 1).setFont(Query.getsurahNames(fontSize));
                }
            }
            for (Word word : verse.getWords()) {
                int lineNumber = word.getLine_number();
                lines.get(lineNumber).append(word.getCode(fontVersion)).append(' ');
            }
        }
        Font font = Query.getFont(newPageNum, fontVersion, fontSize);
        if(lines.get(15).toString().equals("")){
            int surahId =  quranChapters.getChapters().get(verses.get(verses.size()-1).getChapter_id()).getId();
            String surahName = getSurahArabic(surahId);
            fullPage.get(15).setText(surahName+"\n");
            fullPage.get(15).setFont(Query.getsurahNames(fontSize));
        }
        for (int i = 1; i <= 15; i++) {
            if(lines.get(i) != null) {
                if(fullPage.get(i).getText().equals("tempS")){
                    fullPage.get(i).setText(lines.get(i)+"\n");
                    fullPage.get(i).setFont(font);
                }
            }
        }

        return fullPage;
    }

    private String getSurahArabic(int surahId) {
        String str;
        if(surahId < 10){
            str = "00" + surahId;
        }else if (surahId < 100){
            str = "0" + surahId;
        }else{
            str = "" + surahId;
        }

        char[] arabicChars = {'٠','١','٢','٣','٤','٥','٦','٧','٨','٩'};
        StringBuilder builder = new StringBuilder();
        for(int i =0;i<str.length();i++)
        {
            if(Character.isDigit(str.charAt(i)))
            {
                builder.append(arabicChars[(int)(str.charAt(i))-48]);
            }
            else
            {
                builder.append(str.charAt(i));
            }
        }

        return builder.toString();
    }


    public void setVerses(List<Verse> verses) {
        this.verses = verses;
    }
}