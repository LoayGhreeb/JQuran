module org.jquran.jquran {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires org.controlsfx.controls;
    requires spark.core;
    requires atlantafx.base;
//    requires com.sparkjava.spark;

    opens org.jquran.jquran to javafx.fxml;
    exports org.jquran.jquran;
}