module org.jquran.jquran {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires com.fasterxml.jackson.databind;
    requires org.controlsfx.controls;
    requires atlantafx.base;
    requires org.xerial.sqlitejdbc;
    requires org.apache.commons.lang3;

    opens org.jquran.jquran to javafx.fxml;

    exports org.jquran.jquran;
}