module org.jquran.jquran {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;


    opens org.jquran.jquran to javafx.fxml;
    exports org.jquran.jquran;
}