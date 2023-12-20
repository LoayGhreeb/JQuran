module org.jquran.jquran {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires org.controlsfx.controls;
    requires javafx.swing;


    opens org.jquran.jquran to javafx.fxml;
    exports org.jquran.jquran;
}