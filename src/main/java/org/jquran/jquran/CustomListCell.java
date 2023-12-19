package org.jquran.jquran;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class CustomListCell extends ListCell<CustomThing> {
    private HBox content;
    private Text surahName;
    private Text surahInfo;

    public CustomListCell() {
        super();
        surahName = new Text();
        surahInfo = new Text();
        VBox vBox = new VBox(surahName, surahInfo);
        content = new HBox(new Label("[Graphic]"), vBox);
        content.setSpacing(10);
    }

    @Override
    protected void updateItem(CustomThing item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) { // <== test for null item and empty parameter
            surahName.setText(item.getName());
            surahInfo.setText(item.getSurahInfo());
            setGraphic(content);
        } else {
            setGraphic(null);
        }
    }
}
