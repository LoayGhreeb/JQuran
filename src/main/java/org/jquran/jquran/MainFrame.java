package org.jquran.jquran;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class MainFrame extends Application {
    private int pageNum = 1, fontVersion = 1, fontSize = 32;
    private Text pageLines;
    private ScrollPane currentPage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();

        pageLines = new Text();
        pageLines.setTextAlignment(TextAlignment.CENTER);
//        pageLines.setFill(Color.WHITE);
        currentPage = new ScrollPane();

        currentPage.hbarPolicyProperty().set(ScrollPane.ScrollBarPolicy.NEVER);
        currentPage.vbarPolicyProperty().set(ScrollPane.ScrollBarPolicy.NEVER);
        setCurrentPage(pageNum);
        root.setCenter(currentPage);

        Scene scene = new Scene(root);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent e) -> {
            if (e.getCode() == KeyCode.LEFT) {
                setCurrentPage(pageNum + 1);
            } else if (e.getCode() == KeyCode.RIGHT) {
                setCurrentPage(pageNum - 1);
            }
        });
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public void setCurrentPage(int newPageNum) {
        try {
            Page newPage = Query.loadPage(newPageNum, fontVersion);
            if (newPage != null) {
                pageLines.setText(newPage.getLines(fontVersion));
                pageLines.setFont(Query.getFont(newPageNum, fontVersion, fontSize));
                currentPage.setContent(pageLines);
                pageNum = newPageNum;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
